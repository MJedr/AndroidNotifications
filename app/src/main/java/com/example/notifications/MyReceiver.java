package com.example.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static androidx.core.content.ContextCompat.getSystemService;

public class MyReceiver extends BroadcastReceiver {
    private static final String PRODUCT_ADDED = BuildConfig.APPLICATION_ID + ".PRODUCT_ADDED";
    public static final String CHANNEL_1_ID = "channel1";
    private NotificationManagerCompat notificationManager;

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is Channel 1");
        }
    }

    public MyReceiver() {
    }

    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();
        if (intentAction != null) {
            String toastMessage = "unknown intent action";
            switch (intentAction){
                case PRODUCT_ADDED:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel1 = new NotificationChannel(
                                CHANNEL_1_ID,
                                "Channel 1",
                                NotificationManager.IMPORTANCE_HIGH
                        );
                        channel1.setDescription("This is Channel 1");
                        NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                        manager.createNotificationChannel(channel1);
                    }
                    toastMessage = intent.getStringExtra("string");
                    break;
            }

            Intent myIntent = new Intent(context, ItemEditor.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context.getApplicationContext());
            stackBuilder.addNextIntentWithParentStack(myIntent);
            PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                    context.getApplicationContext(), 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT
            );

            myIntent.putExtra("message", toastMessage);
            notificationManager = NotificationManagerCompat.from(context.getApplicationContext());
            NotificationCompat.Builder notification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle("Nowy produkt")
                    .setContentText(toastMessage)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE);
//                    .build();
            notification.setContentIntent(notifyPendingIntent);
            notificationManager.notify(1, notification.build());

        }
    }
}
