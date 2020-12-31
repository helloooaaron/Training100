package test.fb.com;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class CustomReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();
        if (intentAction != null) {
            String msg = "unknown intent action";
            switch(intentAction) {
                case Intent.ACTION_POWER_CONNECTED:
                    msg = "Power connected";
                    break;
                case Intent.ACTION_POWER_DISCONNECTED:
                    msg = "Power disconnected";
                    break;
            }
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }
}
