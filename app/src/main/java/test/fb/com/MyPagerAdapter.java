package test.fb.com;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import test.fb.com.asynctask.AsyncTaskFragment;
import test.fb.com.notification.NotificationFragment;
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
                return PageFragment.newInstance("Content for Tab #1");
            case 1:
                return BroadcastFragment.newInstance();
            case 2:
                return AsyncTaskFragment.newInstance();
            case 3:
                return NotificationFragment.newInstance();
            case 4:
                return HelloSharedPref.newInstance();
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
                return "Tab #1";
            case 1:
                return "Broadcast";
            case 2:
                return "Async";
            case 3:
                return "Notify";
            case 4:
                return "SharedPref";
            default:
                return "";
        }
    }
}
