package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.NotificationListener.sounds;
import static com.urrecliner.chattalk.NotificationListener.utils;
import static com.urrecliner.chattalk.Vars.HIDE_STOP;
import static com.urrecliner.chattalk.Vars.LOAD_NH_STOCK;
import static com.urrecliner.chattalk.Vars.SHOW_MESSAGE;
import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.Vars.sharePref;
import static com.urrecliner.chattalk.Vars.sharedEditor;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
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

public class NotificationService extends Service {

    NotificationCompat.Builder mBuilder = null;
    NotificationManager mNotificationManager;
    NotificationChannel mNotificationChannel = null;

    String pkgName;
    private RemoteViews mRemoteViews;
    private static final int STOP_SAY1 = 10011;
    static String who1 = null, msg1 = "", time1 = "00:99";
    static String who2 = "Talk", msg2 = "", time2 = "00:99";
    static boolean show_stop = false;
    static Context nContext;
//
    public NotificationService() {}

    public NotificationService(Context context) {this.nContext = context;}


    @Override
    public void onCreate() {
        super.onCreate();
        nContext = this;
        pkgName = nContext.getPackageName();
        mRemoteViews = new RemoteViews(nContext.getPackageName(), R.layout.notification_bar);
    }

    @Override
    public IBinder onBind(Intent intent) {return null;}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        createNotification();
        if (utils == null)
            utils = new Utils();

        int operation = -1;
        try {
            operation = intent.getIntExtra("operation", -1);
        } catch (Exception e) {
            Log.e("operation"+operation,e.toString());
        }
        if (who1 == null)
            msgGet();

        switch (operation) {

            case SHOW_MESSAGE:
                who2 = who1;
                msg2 = msg1;
                time2 = time1;

                who1 = Objects.requireNonNull(intent.getStringExtra("who"))
                        .replace(" ", "\u00A0");
                msg1 = utils.makeEtc(Objects.requireNonNull(intent.getStringExtra("msg")), 200)
                        .replace(" ", "\u00A0");
                time1 = new SimpleDateFormat("HH:\nmm", Locale.KOREA).format(new Date());
                show_stop = intent.getBooleanExtra("stop", true);
                break;

            case LOAD_NH_STOCK:

                launchNHStock();
                break;

            case STOP_SAY1:
                if (sounds != null)
                    sounds.stopTTS();
                show_stop = false;
                break;

            case HIDE_STOP:
                show_stop = false;
                break;

            default:
                Log.e("Notivication SVC","Case "+operation);
                break;
        }

        if (operation != -1)
                updateRemoteViews();
        return START_STICKY;
    }

    private void launchNHStock() {
        Intent appIntent = nContext.getPackageManager().getLaunchIntentForPackage(
                "com.wooriwm.txsmart");
        appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        nContext.startActivity(appIntent);
    }

    private void createNotification() {

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationChannel = new NotificationChannel("default","default", NotificationManager.IMPORTANCE_DEFAULT);
        mNotificationManager.createNotificationChannel(mNotificationChannel);
//        mBuilder = new NotificationCompat.Builder(nContext,"default")
//                .setSmallIcon(R.drawable.chat_talk)
//                .setOnlyAlertOnce(true)
//                .setAutoCancel(false)
//                .setCustomBigContentView(mRemoteViews)
//                .setOngoing(true);

        mBuilder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.chat_talk)
//                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
//                .setColor(getApplicationContext().getColor(R.color.barLine1))
//                .setContent(mRemoteViews)
                .setOnlyAlertOnce(true)
                .setAutoCancel(false)
                .setCustomBigContentView(mRemoteViews)
//                .setStyle(new NotificationCompat.BigTextStyle())
                .setOngoing(true);

        Intent mIntent = new Intent(nContext, ActivityMain.class);
        mRemoteViews.setOnClickPendingIntent(R.id.ll_customNotification,
            PendingIntent.getActivity(nContext, 0, mIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT));

        Intent sIntent = new Intent(this, NotificationService.class);
        sIntent.putExtra("operation", STOP_SAY1);
        PendingIntent stopSay1Pi = PendingIntent.getService(nContext, STOP_SAY1, sIntent,
            PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(stopSay1Pi);
        mRemoteViews.setOnClickPendingIntent(R.id.stop_now1, stopSay1Pi);
    }

    private void updateRemoteViews() {

        mBuilder.setSmallIcon(R.drawable.chat_talk);
        mRemoteViews.setTextViewText(R.id.msg_time1, time1);
        mRemoteViews.setTextViewText(R.id.msg_who1, who1);
        mRemoteViews.setTextViewText(R.id.msg_text1, msg1);
        mRemoteViews.setTextViewText(R.id.msg_time2, time2);
        mRemoteViews.setTextViewText(R.id.msg_who2, who2);
        mRemoteViews.setTextViewText(R.id.msg_text2, msg2);
        mRemoteViews.setViewVisibility(R.id.stop_now1, (show_stop)? View.VISIBLE : View.GONE);
        mNotificationManager.notify(110,mBuilder.build());
        msgPut();
    }
    public static void msgGet() {

        if (sharePref == null) {
            sharePref = nContext.getSharedPreferences("sayText", MODE_PRIVATE);
            sharedEditor = sharePref.edit();
        }
        who1 = sharePref.getString("who1", "New Loaded 1");
        who2 = sharePref.getString("who2", "New Loaded 2");
        msg1 = sharePref.getString("msg1", "None 1");
        msg2 = sharePref.getString("msg2", "None 2");
        time1 = sharePref.getString("time1","00:99");
        time2 = sharePref.getString("time2","00:99");
    }
    public static void msgPut() {
        sharedEditor.putString("who1", who1);
        sharedEditor.putString("who2", who2);
        sharedEditor.putString("msg1", msg1);
        sharedEditor.putString("msg2", msg2);
        sharedEditor.putString("time1", time1);
        sharedEditor.putString("time2", time2);
        sharedEditor.apply();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}