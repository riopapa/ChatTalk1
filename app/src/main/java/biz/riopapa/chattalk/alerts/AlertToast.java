package biz.riopapa.chattalk.alerts;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import biz.riopapa.chattalk.Sub.IsScreen;

public class AlertToast {

    public void show (Context context, Activity activity, String msg) {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (IsScreen.On(context) && activity != null) {
                    activity.runOnUiThread(() -> Toast.makeText(context, msg, Toast.LENGTH_LONG).show());
            }
        });
    }
}
