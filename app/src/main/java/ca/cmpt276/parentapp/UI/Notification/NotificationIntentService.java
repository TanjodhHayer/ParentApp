package ca.cmpt276.parentapp.UI.Notification;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.legacy.content.WakefulBroadcastReceiver;

import ca.cmpt276.parentapp.R;
import ca.cmpt276.parentapp.UI.TimeoutTimer.TimeoutTimerActivity;

public class NotificationIntentService extends IntentService {
    public static final String CHANNEL = "Timer";
    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";

    public NotificationIntentService() {
        super(NotificationIntentService.class.getSimpleName());
    }

    public static Intent createIntentStartNotificationService(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_START);
        return intent;
    }

    public static Intent createIntentDeleteNotification(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_DELETE);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(getClass().getSimpleName(), "onHandleIntent, started handling a notification event");
        try {
            String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                createFinishNotification();
            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private void createFinishNotification() {
        NotificationChannel channel = new NotificationChannel(CHANNEL, "Timer Finished", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Timer Notification");
        Log.d("Notification", "created");
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
        processStartNotification();
    }

    public void processStartNotification() {
        String title = "Timer";
        String message = "FINISHED";

        Intent timerIntent = new Intent(this, TimeoutTimerActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, timerIntent, 0);

        Intent broadcastIntent = new Intent(this, NotificationServiceStarterReceiver.class);
        broadcastIntent.putExtra("Stop Timer", message);
        PendingIntent actionIntent = PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        broadcastIntent.putExtra("Stop Timer", message);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Log.d("Timer", "sound on");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL);
        builder.setSmallIcon(R.drawable.done)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.BLACK)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setSound(alarmSound)
                .addAction(R.mipmap.ic_launcher, "Stop Timer", actionIntent)
                .build();
        NotificationManagerCompat.from(this).notify(1, builder.build());
    }
}

