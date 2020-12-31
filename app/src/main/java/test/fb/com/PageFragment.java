package test.fb.com;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PageFragment extends Fragment {

    private static final String CONTENT_KEY = "content_arg_key";

    private String mContent;

    public PageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * Fragment is often destroyed and recreated by the system. When system does so, it will call the no-argument constructor.
     * Thus it's important to use a Bundle to pass any necessary data.
     *
     * @return A new instance of fragment PageFragment.
     */
    public static PageFragment newInstance(String content) {
        Bundle args = new Bundle();
        args.putString(CONTENT_KEY, content);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);  // Store Bundle in Arguments. Can only be called before attach to Activity.
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Getting Bundle passed from previously destroyed fragment
        mContent = getArguments().getString(CONTENT_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment, attachToRoot must be false for fragment
        View rootView = inflater.inflate(R.layout.fragment_page, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.text_page_content);
        textView.setText(mContent);
        return rootView;
    }
}
