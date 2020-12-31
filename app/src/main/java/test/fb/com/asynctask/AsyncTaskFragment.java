package test.fb.com.asynctask;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Random;

import test.fb.com.R;

public class AsyncTaskFragment extends Fragment {

    private EditText mEditBook;
    private TextView mTextTitle;
    private TextView mTextAuthor;

    public AsyncTaskFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided parameters.
     */
    public static AsyncTaskFragment newInstance() {
        AsyncTaskFragment fragment = new AsyncTaskFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) { }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_async_task, container, false);

        final TextView textView = view.findViewById(R.id.text_async_task);
        final ProgressBar progressBar = view.findViewById(R.id.progressBar);
        Button button = view.findViewById(R.id.button_async_task);
        button.setOnClickListener(v -> {
            /*
             * Access a local variable/param -> captures that variable/param -> captured variable/param
             *
             * A local class can access local variables/parameters of the enclosing block that are final or effectively final.
             * (effectively final: value is never changed after it's initialized)
             * So "final" to textView is optional as long as textView's value is never changed (e.g. textView = null).
             */
            textView.setText(R.string.napping);

            /*
             * AsyncTask will continue running even if the activity is destroyed (e.g. configuration change). But it will lose
             * the ability to report back to the new activity's UI as textView has been destroyed.
             */
            new SimpleAsyncTask(textView, progressBar).execute();
        });

        // Another AsyncTask to query books on the internet
        mEditBook = (EditText) view.findViewById(R.id.edit_bookInput);
        mTextTitle = (TextView) view.findViewById(R.id.text_title);
        mTextAuthor = (TextView) view.findViewById(R.id.text_author);
        Button buttonSearch = (Button) view.findViewById(R.id.button_search);
        buttonSearch.setOnClickListener(v -> {
            String query = mEditBook.getText().toString();

            // Hide keyboard while querying
            InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }

            // Check network connection
            ConnectivityManager connMgr = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;
            if (connMgr != null) networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected() && query.length() != 0) {
                new FetchBook(mTextTitle, mTextAuthor).execute(query); // Perform AsyncTask
                mTextTitle.setText(R.string.loading);
            } else if (query.length() == 0) {
                mTextTitle.setText(R.string.no_search_term);
            } else {
                mTextTitle.setText(R.string.no_network);
            }
            mTextAuthor.setText("");
        });

        return view;
    }
}

class SimpleAsyncTask extends AsyncTask<Void, Integer, String> {
    private static final int MAX_SLEEP = 200;
    // Use WeakReference to prevent memory leak by allowing the object held by the reference to be garbage collected if necessary
    private WeakReference<TextView> mTextView;
    private WeakReference<ProgressBar> mProgressBar;

    public SimpleAsyncTask(TextView tv, ProgressBar pb) {
        this.mTextView = new WeakReference<>(tv);
        this.mProgressBar = new WeakReference<>(pb);
    }

    @Override
    protected void onPreExecute() {
        // In UI thread
        super.onPreExecute();
        mProgressBar.get().setVisibility(View.VISIBLE);
        mProgressBar.get().setMax(MAX_SLEEP);
    }

    @Override
    protected String doInBackground(Void... voids) {
        // In Worker thread
        Random r = new Random();
        int s = r.nextInt(11);
        try {
            for (int i = 0; i < MAX_SLEEP; i++) {
                Thread.sleep(s);
                publishProgress(i);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Awake at last after sleeping for " + s * MAX_SLEEP + "milliseconds!";
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onProgressUpdate(Integer... values) {
        // In UI thread
        super.onProgressUpdate(values);
        mProgressBar.get().setProgress(values[0], true);
    }

    @Override
    protected void onPostExecute(String s) {
        // In UI thread
        // It won't be able to update the view as expected if the activity is destroyed and recreated during the process (e.g. screen rotate).
        mTextView.get().setText(s);
    }
}

class FetchBook extends AsyncTask<String, Void, String> {
    // Use WeakReference (rather than actual View obj) to avoid leaking context from the Activity by allowing garbage-collected if necessary
    private WeakReference<TextView> mTextTitle;
    private WeakReference<TextView> mTextAuthor;

    public FetchBook(TextView title, TextView author) {
        this.mTextTitle = new WeakReference<>(title);
        this.mTextAuthor = new WeakReference<>(author);
    }

    @Override
    protected String doInBackground(String... strings) {
        // Query books from internet
        return NetworkUtils.getBookInfo(strings[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            JSONObject jsonObj = new JSONObject(s);
            JSONArray itemsArray = jsonObj.getJSONArray("items");

            int i = 0;
            String title = null, authors = null;
            while (i < itemsArray.length() && (authors == null && title == null)) { // Only find the first match
                JSONObject book = itemsArray.getJSONObject(i++);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");
                title = volumeInfo.getString("title");
                authors = volumeInfo.getString("authors");
            }

            if (title != null && authors != null) {
                mTextTitle.get().setText(title);
                mTextAuthor.get().setText(authors);
            } else {
                mTextTitle.get().setText(R.string.no_results);
                mTextAuthor.get().setText("");
            }
        } catch (JSONException e) {
            mTextTitle.get().setText(R.string.no_results);
            mTextAuthor.get().setText("");
        }
    }
}
