package com.urrecliner.chattalk;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.NotificationCompat;

public class NotifyStock {

    static NotificationChannel channel = null;
    static int stockId = 0;
    public void send(Context context, String textTitle, String stockName, String textContent) {

        if (channel == null)
            channel = new NotificationChannel("M_CH_ID", "M_CH_ID", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, "M_CH_ID");

        final int LOAD_NH_STOCK = 4321;
        Intent sIntent = new Intent(context, NotificationService.class);
        sIntent.putExtra("operation", LOAD_NH_STOCK);
        PendingIntent launchNH = PendingIntent.getService(context, 31, sIntent,
                PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(launchNH);

        final String app = "com.wooriwm.txsmart"; // NH 나무
        final PackageManager managerclock = context.getPackageManager();
        Intent nhIntent = managerclock.getLaunchIntentForPackage(app);
        nhIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, nhIntent,
                PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        stockId++;
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon((stockId%2) == 0? R.drawable.stock1_icon:R.drawable.stock2_icon)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(textContent))
                .addAction(new NotificationCompat.Action.Builder(R.drawable.share_app,
                        "NH "+stockName, pIntent).build())
                .setContentInfo("Info");
//                .addAction(android.R.drawable.stat_notify_more, "NH "+stockName, pIntent)

        int id = (int) (System.currentTimeMillis() & 0xFFFFFF);
        manager.notify(id, notificationBuilder.build());

    }
}
