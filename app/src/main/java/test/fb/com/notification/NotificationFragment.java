package test.fb.com.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import test.fb.com.MainActivity;
import test.fb.com.R;

public class NotificationFragment extends Fragment {
    private static final String ACTION_UPDATE_NOTIFICATION = "test.fb.com.notifyme.ACTION_UPDATE_NOTIFICATION";
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private static final int NOTIFICATION_ID = 0;
    private NotificationManager mNotifyManager; // Android system uses NotificationManager to deliver notifications to the user.
    private Button mButtonNotify;
    private Button mButtonUpdate;
    private Button mButtonCancel;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        // Receiver to receive broadcast from notification action
        @Override
        public void onReceive(Context context, Intent intent) {
            setButtonState(true, false, false);
            Toast.makeText(context, "Broadcast is received.", Toast.LENGTH_SHORT).show();
        }
    };

    public NotificationFragment() {
        // Required empty public constructor
    }

    public static NotificationFragment newInstance() {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) { }
        mNotifyManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        // NOTIFY ME!
        mButtonNotify = (Button) view.findViewById(R.id.button_notify);
        mButtonNotify.setOnClickListener(v -> {
            setButtonState(false, true, true);
            NotificationCompat.Builder builder = getNotificationBuilder(); // Use Builder to build notification
            // Add action button to the notification to trigger a broadcast
            PendingIntent piAction = PendingIntent.getBroadcast
                    (getContext(), NOTIFICATION_ID, new Intent(ACTION_UPDATE_NOTIFICATION), PendingIntent.FLAG_ONE_SHOT);
            builder.addAction(R.drawable.ic_hit_me, "hit me", piAction);
            mNotifyManager.notify(NOTIFICATION_ID, builder.build()); // Associate the notification with a ID so that the code can update or cancel it in the future
        });

        // UPDATE ME!
        mButtonUpdate = (Button) view.findViewById(R.id.button_update);
        mButtonUpdate.setOnClickListener(v -> {
            setButtonState(false, false, true);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mascot_1); // Read drawable resource and convert to Bitmap obj
            NotificationCompat.Builder builder = getNotificationBuilder();
            builder.setStyle(new NotificationCompat.BigPictureStyle() // BigPictureStyle: Predefined notification style
                        .bigPicture(bitmap)
                        .setBigContentTitle("Notification Updated!"));
            mNotifyManager.notify(NOTIFICATION_ID, builder.build()); // Notify to the same ID to update the notification
        });

        // CANCEL ME!
        mButtonCancel = (Button) view.findViewById(R.id.button_cancel);
        mButtonCancel.setOnClickListener(v -> {
            setButtonState(true, false, false);
            mNotifyManager.cancel(NOTIFICATION_ID);
        });

        // Can't use LocalBroadcastManager here since the broadcast will be sent from notification by Android framework
        getContext().registerReceiver(mReceiver, new IntentFilter(ACTION_UPDATE_NOTIFICATION));

        setButtonState(true, false, false);
        createNotificationChannel();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getContext().unregisterReceiver(mReceiver);
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create and configure notification channel for Andoid8 (API26) or higher.
            NotificationChannel notifyChannel = new NotificationChannel(PRIMARY_CHANNEL_ID, "Mascot Notification", NotificationManager.IMPORTANCE_HIGH); // High importance
            notifyChannel.enableLights(true);
            notifyChannel.setLightColor(Color.RED);
            notifyChannel.enableVibration(true);
            notifyChannel.setDescription("Notification from Mascot"); // Appear at this notification channel's setting
            mNotifyManager.createNotificationChannel(notifyChannel);
        }
    }

    private NotificationCompat.Builder getNotificationBuilder() {
        // Click notification PI: create a new MainActivity on top of current Activity.
        PendingIntent piContent = PendingIntent.getActivity
                (getContext(), NOTIFICATION_ID, new Intent(getContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        // CLear notification PI: trigger broadcast to restore button states
        PendingIntent piDelete = PendingIntent.getBroadcast
                (getContext(), NOTIFICATION_ID, new Intent(ACTION_UPDATE_NOTIFICATION), PendingIntent.FLAG_ONE_SHOT);
        return new NotificationCompat.Builder(getContext(), PRIMARY_CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // For backward compatibility (< Android7.1, API25) when channel is not available
                .setDefaults(NotificationCompat.DEFAULT_ALL) // For backward compatibility to set sound/vibration/led etc.
                .setContentTitle("You've been notified!")
                .setContentText("This is your notification text.")
                .setSmallIcon(R.drawable.ic_dollar)
                .setContentIntent(piContent) // PendingIntent on click on the notification
                .setDeleteIntent(piDelete)   // PendingIntent on clear this notification
                .setAutoCancel(true); // Auto close the notification when user taps on it.
    }

    // Toggle button state
    void setButtonState(boolean isNotifyEnabled, boolean isUpdateEnabled, boolean isCancelEnabled) {
        mButtonNotify.setEnabled(isNotifyEnabled);
        mButtonUpdate.setEnabled(isUpdateEnabled);
        mButtonCancel.setEnabled(isCancelEnabled);
    }
}
