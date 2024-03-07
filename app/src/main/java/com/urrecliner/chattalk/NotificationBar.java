package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.NotificationListener.sounds;
import static com.urrecliner.chattalk.Vars.HIDE_STOP;
import static com.urrecliner.chattalk.Vars.SHOW_MESSAGE;
import static com.urrecliner.chattalk.Vars.mActivity;
import static com.urrecliner.chattalk.Vars.mContext;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationBar {

    public NotificationBar() {
        Log.e("noti bar", "new ");
    }

    public static void update(String who, String msg, boolean stop_icon) {

        final String iMsg = (msg.length() > 400) ?msg.substring(0,400) : msg;
        Intent intent = new Intent(mContext, NotificationService.class);
        intent.putExtra("operation", SHOW_MESSAGE);
        intent.putExtra("who", who);
        intent.putExtra("msg", iMsg);
        intent.putExtra("stop", stop_icon && !sounds.isSilent());
        if (mContext == null) {
            if (mActivity != null)
                mContext = mActivity.getApplicationContext();
            else
                Log.e("Noti Bar", "// Context, Activity null //");
        }
        try {
//            if (isMyServiceRunning(NotificationService.class))
            mContext.stopService(intent);
            mContext.startService(intent);
//            else
        } catch (Exception e) {
            try {
                mContext.startForegroundService(intent);
            } catch (Exception ex) {
                Log.e("Notifi Bar","svc E r r o r \n"+ex);
            }
        }
    }

    public static void hideStop() {
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


    boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
