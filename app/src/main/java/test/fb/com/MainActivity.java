package test.fb.com;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import test.fb.com.broadcast.CustomReceiver;
import test.fb.com.droidcafe.DroidCafe;
import test.fb.com.recyclerview.RecyclerViewActivity;

import static android.content.Intent.ACTION_POWER_CONNECTED;
import static android.content.Intent.ACTION_POWER_DISCONNECTED;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final String ACTION_CUSTOM_BROADCAST = BuildConfig.APPLICATION_ID + ".ACTION_CUSTOM_BROADCAST";

    private CustomReceiver mReceiver = new CustomReceiver();
    private BroadcastReceiver mLocalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Custom Broadcast " + intent.getIntExtra("INT_EXTRA", 20), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(LOG_TAG, "onCreate");

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        // Register dynamic broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_POWER_CONNECTED);
        filter.addAction(ACTION_POWER_DISCONNECTED);
        registerReceiver(mReceiver, filter);

        // Register local broadcast receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocalReceiver, new IntentFilter(ACTION_CUSTOM_BROADCAST));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");
        unregisterReceiver(mReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocalReceiver);
    }

    public void gotoRecyclerView(View view) {
        Intent intent = new Intent(this, RecyclerViewActivity.class);
        startActivity(intent);
    }

    public void gotoDroidCafe(View view) {
        startActivity(new Intent(this, DroidCafe.class));
    }
}
