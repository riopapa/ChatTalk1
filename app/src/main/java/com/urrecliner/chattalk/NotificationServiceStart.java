package com.urrecliner.chattalk;

import android.content.Context;
import android.content.Intent;

public class NotificationServiceStart {
    public NotificationServiceStart(Context context) {
//        NotificationService notificationService  = new NotificationService(context);
////        if (!BootReceiver.isServiceRunning(context, notificationService.getClass())) {
//            Intent intent = new Intent(context, notificationService.getClass());
//            context.startForegroundService(intent);
////        }
        Intent updateIntent = new Intent(context, NotificationService.class);
        updateIntent.putExtra("isStarted", true);
        context.startForegroundService(updateIntent);

    }
}
