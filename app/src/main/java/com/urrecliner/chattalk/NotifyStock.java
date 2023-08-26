package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.mContext;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.NotificationCompat;

public class NotifyStock {

    static NotificationManager manager = null;
    static NotificationChannel channel = null;
    static NotificationCompat.Builder notificationBuilder = null;
    public void send(Context context, String textTitle, String stockName, String textContent) {

        if (channel == null)
            channel = new NotificationChannel("M_CH_ID", "M_CH_ID", NotificationManager.IMPORTANCE_DEFAULT);
        if (manager == null) {
            manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        if (notificationBuilder ==  null)
            notificationBuilder = new NotificationCompat.Builder(mContext, "M_CH_ID");

//        Intent aIntent = new Intent(context, AlertService.class);
        final String app = "com.wooriwm.txsmart"; // NH 나무
        final PackageManager managerclock = mContext.getPackageManager();
        Intent nhIntent = managerclock.getLaunchIntentForPackage(app);
//        nhIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        nhIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pIntent = PendingIntent.getBroadcast(context, 20, nhIntent, PendingIntent.FLAG_IMMUTABLE);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.stock_icon)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .addAction(new NotificationCompat.Action.Builder(
                        android.R.drawable.stat_notify_more, "Run NH "+stockName, pIntent).build())
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(textContent))
                .setContentInfo("Info");

        int id = (int) (System.currentTimeMillis() & 0xFFFFFF);
        manager.notify(id, notificationBuilder.build());

        final int LOAD_NH_STOCK = 4321;
        Intent sIntent = new Intent(mContext, NotificationService.class);
        sIntent.putExtra("operation", LOAD_NH_STOCK);
        PendingIntent launchNH = PendingIntent.getService(mContext, 31, sIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(launchNH);

    }
}
