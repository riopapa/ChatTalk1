package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.NotificationListener.subFunc;
import static com.urrecliner.chattalk.Vars.HIDE_STOP;
import static com.urrecliner.chattalk.Vars.SHOW_MESSAGE;
import static com.urrecliner.chattalk.Vars.mContext;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.urrecliner.chattalk.Sub.IsScreen;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationBar {

    static String svMsg = "";

    public void update(String who, String msg, boolean show_icon) {


        svMsg = new SimpleDateFormat("HH:mm\u00A0", Locale.KOREA).format(new Date());
        svMsg += (msg.length() > 30) ? msg.substring(0, msg.length()*2/3): msg;
        if (mContext != null) {
            Intent intent = new Intent(mContext, NotificationService.class);
            intent.putExtra("operation", SHOW_MESSAGE);
            intent.putExtra("who", who);
            intent.putExtra("msg", msg);
            intent.putExtra("stop", show_icon && !subFunc.sounds.isSilent());
            try {
                if (isMyServiceRunning(NotificationService.class))
                    mContext.startService(intent);
                else
                    mContext.startForegroundService(intent);
            } catch (Exception e) {
                Log.e("NotificationBar","intent Error \n"+e);
            }
            if (IsScreen.On(mContext)) {
                new Handler(Looper.getMainLooper()).post(()
                        -> Toast.makeText(mContext, who + " > " + msg, Toast.LENGTH_SHORT).show());
            }
        }

    }

    static void hideStop() {
        if (mContext != null) {
            Intent intent = new Intent(mContext, NotificationService.class);
            intent.putExtra("operation", HIDE_STOP);
            intent.putExtra("who", "None");
            intent.putExtra("msg", "none");
            intent.putExtra("stop", false);
            try {
                mContext.startService(intent);
            } catch (Exception e) {
                Log.e("NotificationBar","intent Error \n"+e);
            }
        }
    }

    static boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
