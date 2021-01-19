package test.fb.com;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import test.fb.com.asynctask.AsyncTaskFragment;
import test.fb.com.broadcast.BroadcastFragment;
import test.fb.com.notification.NotificationFragment;
import test.fb.com.security.SecurityFragment;
import test.fb.com.sharedpref.HelloSharedPref;

public class MyPagerAdapter extends FragmentPagerAdapter {

    public MyPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return BroadcastFragment.newInstance();
            case 1:
                return AsyncTaskFragment.newInstance();
            case 2:
                return NotificationFragment.newInstance();
            case 3:
                return HelloSharedPref.newInstance();
            case 4:
                return SecurityFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 0:
                return "Broadcast";
            case 1:
                return "Async";
            case 2:
                return "Notify";
            case 3:
                return "SharedPref";
            case 4:
                return "Security";
            default:
                return "";
        }
    }
}
