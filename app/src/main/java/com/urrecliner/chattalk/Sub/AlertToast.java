package com.urrecliner.chattalk.Sub;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class AlertToast {

    public void show (Context context, Activity activity, String msg) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (IsScreen.On(context)) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
}
