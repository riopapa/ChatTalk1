package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.NotificationListener.sounds;
import static com.urrecliner.chattalk.NotificationListener.utils;
import static com.urrecliner.chattalk.NotificationService.msgGet;
import static com.urrecliner.chattalk.NotificationService.msgPut;
import static com.urrecliner.chattalk.Vars.HIDE_STOP;
import static com.urrecliner.chattalk.Vars.SHOW_MESSAGE;
import static com.urrecliner.chattalk.Vars.mContext;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AlertService extends Service {

    NotificationCompat.Builder mBuilder = null;
    NotificationManager mNotificationManager;
    String pkgName;
    private RemoteViews mRemoteViews;
    private static final int STOP_SAY1 = 10011;
    static String who1 = null, msg1 = "", time1 = "00:99";
    static String who2 = "Talk", msg2 = "", time2 = "00:99";
    static boolean show_stop = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        pkgName = mContext.getPackageName();
        mRemoteViews = new RemoteViews(pkgName, R.layout.notification_bar);
        if (utils == null)
            utils = new Utils();
        msgGet();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (mRemoteViews == null)
            mRemoteViews = new RemoteViews(pkgName, R.layout.notification_bar);

        int operation = -1;
        try {
            operation = intent.getIntExtra("operation", -1);
        } catch (Exception e) {
            Log.e("operation"+operation,e.toString());
        }
        if (who1 == null)
            msgGet();
        createNotification();

        switch (operation) {

            case SHOW_MESSAGE:
                who2 = who1;
                msg2 = msg1;
                time2 = time1;

                who1 = Objects.requireNonNull(intent.getStringExtra("who"))
                        .replace(" ", "\u00A0");
                msg1 = utils.makeEtc(Objects.requireNonNull(intent.getStringExtra("msg")), 300)
                        .replace(" ", "\u00A0");
                time1 = new SimpleDateFormat("HH:mm", Locale.KOREA).format(new Date())
                        +" "+who1;
                show_stop = intent.getBooleanExtra("stop", true);
                break;

            case STOP_SAY1:
                sounds.stopTTS();
                show_stop = false;
                break;

            case HIDE_STOP:
                show_stop = false;
                break;

            default:
                break;
        }

        if (operation != -1)
            updateRemoteViews();
        return START_STICKY;
    }

    private void createNotification() {

        if (null == mNotificationManager) {
            CharSequence name = "ChatTalk";
            String description = "Chat Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("ChatTalk1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            mNotificationManager = getSystemService(NotificationManager.class);
            mNotificationManager.createNotificationChannel(channel);
        }

        if (null == mBuilder) {
            mBuilder = new NotificationCompat.Builder(this, "default")
                    .setSmallIcon(R.drawable.stock1_icon)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                    .setColor(getApplicationContext().getColor(R.color.upper_line))
                    .setContent(mRemoteViews)
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(false)
                    .setCustomBigContentView(mRemoteViews)
                    .setStyle(new NotificationCompat.BigTextStyle())
                    .setOngoing(true);
        }

        Intent mIntent = new Intent(mContext, ActivityMain.class);
        mRemoteViews.setOnClickPendingIntent(R.id.line_upper,
                PendingIntent.getActivity(mContext, 0, mIntent,
                        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT));
        mRemoteViews.setOnClickPendingIntent(R.id.line_lower,
                PendingIntent.getActivity(mContext, 0, mIntent,
                        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT));

        Intent sIntent = new Intent(this, AlertService.class);
        sIntent.putExtra("operation", STOP_SAY1);
        PendingIntent stopSay1Pi = PendingIntent.getService(mContext, 21, sIntent,
            PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(stopSay1Pi);
        mRemoteViews.setOnClickPendingIntent(R.id.stop_now1, stopSay1Pi);
    }

    private void updateRemoteViews() {

        mRemoteViews.setTextViewText(R.id.msg_time1, time1);
        mRemoteViews.setTextViewText(R.id.msg_text1, msg1);
        mRemoteViews.setTextViewText(R.id.msg_time2, time2);
        mRemoteViews.setTextViewText(R.id.msg_text2, msg2);
        mRemoteViews.setViewVisibility(R.id.stop_now1, (show_stop)? View.VISIBLE : View.GONE);
        mNotificationManager.notify(100,mBuilder.build());
        msgPut();
    }
}