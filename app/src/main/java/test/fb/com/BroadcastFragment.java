package test.fb.com;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Random;

import static test.fb.com.MainActivity.ACTION_CUSTOM_BROADCAST;

public class BroadcastFragment extends Fragment {

    public BroadcastFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided parameters.
     */
    public static BroadcastFragment newInstance() {
        return new BroadcastFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_broadcast, container, false);
        Button button = (Button) view.findViewById(R.id.button_broadcast);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(ACTION_CUSTOM_BROADCAST);
            intent.putExtra("INT_EXTRA", new Random().nextInt(20));
            // Send local broadcast
            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
        });
        return view;
    }
}
