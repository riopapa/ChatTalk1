package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.mContext;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import java.util.Random;

public class NotifyStock {

    static NotificationManager manager = null;
    static NotificationChannel channel = null;
    static NotificationCompat.Builder notificationBuilder = null;
    public void send(Context context, String textTitle, String textContent) {

        if (channel == null)
            channel = new NotificationChannel("M_CH_ID", "M_CH_ID", NotificationManager.IMPORTANCE_DEFAULT);
        if (manager == null) {
            manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        if (notificationBuilder ==  null)
            notificationBuilder = new NotificationCompat.Builder(mContext, "M_CH_ID");

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.stock_icon)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(textContent))
                .setContentInfo("Info");
        int id = new Random().nextInt(2000)+1000;
        manager.notify(id, notificationBuilder.build());
    }
}
