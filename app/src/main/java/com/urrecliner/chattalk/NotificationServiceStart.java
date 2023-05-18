package com.urrecliner.chattalk;

import android.content.Context;
import android.content.Intent;

public class NotificationServiceStart {
    public NotificationServiceStart(Context context) {
        Intent updateIntent = new Intent(context, NotificationService.class);
        context.startForegroundService(updateIntent);

    }
}
