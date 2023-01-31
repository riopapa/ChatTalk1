package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.SHOW_MESSAGE;
import static com.urrecliner.chattalk.Vars.mActivity;
import static com.urrecliner.chattalk.Vars.mContext;

import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationBar {

    static String savedMessage = "";
    static int count;
    static Timer timer = new Timer();
    static TimerTask timerTask = null;
    static final long LOOP_INTERVAL = 25 * 60 * 1000;
    static long lastTime = 0;

    static void update(String msg) {

        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        savedMessage = new SimpleDateFormat("HH:mm\u00A0", Locale.KOREA).format(new Date());
        savedMessage += msg.substring(0, msg.length()/2);
        count = 0;
        if (mActivity != null) {
            Intent updateIntent = new Intent(mContext, NotificationService.class);
            updateIntent.putExtra("operation", SHOW_MESSAGE);
            updateIntent.putExtra("msg", msg);
            try {
                mActivity.startService(updateIntent);
            } catch (Exception e) {
                Log.e("NotificationBar","updateIntent Error \n"+e);
            }
        }
        lastTime = System.currentTimeMillis();

        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run () {
                count++;
                Log.w("NotiBar "+count, (System.currentTimeMillis()-lastTime)/1000+"  " + savedMessage);
                lastTime = System.currentTimeMillis();
                System.gc();
            }
        };
        timer.schedule(timerTask, LOOP_INTERVAL, LOOP_INTERVAL);
    }
}