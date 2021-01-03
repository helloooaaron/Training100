package test.fb.com.sharedpref;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import test.fb.com.BuildConfig;
import test.fb.com.R;

public class HelloSharedPref extends Fragment {
    private static final String KEY_COUNT = "count";
    private static final String KEY_COLOR = "color";
    int mCount;
    int mColor;
    TextView mTextCount;
    Button mButtonCount;
    Button mButtonReset;

    private SharedPreferences mPref;
    private String mSharedPrefFile = BuildConfig.APPLICATION_ID + ".hellosharedprefs"; // Conventionally the file has same name as the pkg

    public HelloSharedPref() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static HelloSharedPref newInstance() {
        HelloSharedPref fragment = new HelloSharedPref();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Restore states per fragment recreation or app restart from preferences
        mPref = getContext().getSharedPreferences(mSharedPrefFile, Context.MODE_PRIVATE);
        mCount = mPref.getInt(KEY_COUNT, 0);
        mColor = mPref.getInt(KEY_COLOR, getResources().getColor(android.R.color.darker_gray));
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = mPref.edit();
        editor.putInt(KEY_COUNT, mCount);
        editor.putInt(KEY_COLOR, mColor);
        editor.apply();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hello_shared_pref, container, false);
        mTextCount = (TextView) view.findViewById(R.id.text_count);
        mTextCount.setBackgroundColor(mColor);
        mTextCount.setText(String.valueOf(mCount));

        final View.OnClickListener changeColor = v -> {
            mColor = ((ColorDrawable) v.getBackground()).getColor();
            mTextCount.setBackgroundColor(mColor);
        };
        Button buttonBlack = (Button) view.findViewById(R.id.button_black);
        buttonBlack.setOnClickListener(changeColor);
        Button buttonRed = (Button) view.findViewById(R.id.button_red);
        buttonRed.setOnClickListener(changeColor);
        Button buttonBlue = (Button) view.findViewById(R.id.button_blue);
        buttonBlue.setOnClickListener(changeColor);
        Button buttonGreen = (Button) view.findViewById(R.id.button_green);
        buttonGreen.setOnClickListener(changeColor);

        mButtonCount = (Button) view.findViewById(R.id.button_count);
        mButtonCount.setOnClickListener(v -> {
            mTextCount.setText(String.valueOf(++mCount));
        });

        mButtonReset = (Button) view.findViewById(R.id.button_reset);
        mButtonReset.setOnClickListener(v -> {
            mTextCount.setText(String.valueOf(mCount = 0));
            mTextCount.setBackgroundColor(mColor = getResources().getColor(android.R.color.darker_gray));

            // Clear preferences
            SharedPreferences.Editor editor = mPref.edit();
            editor.clear();
            editor.apply();
        });

        return view;
    }
}
