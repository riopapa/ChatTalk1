package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.SubFunc.sounds;
import static com.urrecliner.chattalk.Vars.SHOW_MESSAGE;

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

import com.urrecliner.chattalk.Sub.ByteLength;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class NotificationService extends Service {

    private Context svcContext;
    NotificationCompat.Builder mBuilder = null;
    NotificationChannel mNotificationChannel = null;
    NotificationManager mNotificationManager;
    private RemoteViews mRemoteViews;
    private static final int ERASER = 1013;
    private static final int STOP_SAY = 10011;
    String who = null, msgText = null, time = null;

    @Override
    public void onCreate() {
        super.onCreate();
        svcContext = this;
        String thisPackageName = getApplicationContext().getPackageName();
        if (null != mRemoteViews)
            mRemoteViews = null;
        mRemoteViews = new RemoteViews(thisPackageName, R.layout.notification_bar);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int operation = -1;
        try {
            operation = intent.getIntExtra("operation", -1);
        } catch (Exception e) {
            Log.e("operation"+operation,e.toString());
        }
        createNotification();
        switch (operation) {
            case STOP_SAY:
                sounds.stopTTS();
                break;

            case SHOW_MESSAGE:
                who = Objects.requireNonNull(intent.getStringExtra("who")).replace(" ", "\u00A0");
                while (ByteLength.get(who) > 17)
                    who = who.substring(0, who.length()-1);
                msgText = Objects.requireNonNull(intent.getStringExtra("msg")).replace(" ", "\u00A0");
                break;

            case ERASER:
                msgText = null;
                who = "Chat Talk..";

                break;
            default:
                break;
        }

        time = new SimpleDateFormat("HH:mm", Locale.KOREA).format(new Date());
        updateRemoteViews();
        return START_STICKY;
    }

    private void createNotification() {

        if (null == mNotificationChannel) {
                mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                mNotificationChannel = new NotificationChannel("default","default", NotificationManager.IMPORTANCE_DEFAULT);
                mNotificationManager.createNotificationChannel(mNotificationChannel);
        }
        if (null == mBuilder) {

            mBuilder = new NotificationCompat.Builder(this,"default")
                    .setSmallIcon(R.mipmap.chat_talk_mini)
                    .setContent(mRemoteViews)
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(false)
                    .setCustomContentView(mRemoteViews)
                    .setCustomBigContentView(mRemoteViews)
                    .setOngoing(true);

        }

        Intent mainIntent = new Intent(svcContext, MainActivity.class);
        mainIntent.putExtra("load","load");
        mRemoteViews.setOnClickPendingIntent(R.id.ll_customNotification, PendingIntent.getActivity(svcContext, 0, mainIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT));

        Intent eraseIntent = new Intent(this, NotificationService.class);
        eraseIntent.putExtra("operation", ERASER);
        PendingIntent erasePi = PendingIntent.getService(svcContext, 11, eraseIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(erasePi);
        mRemoteViews.setOnClickPendingIntent(R.id.erase, erasePi);

        Intent stopSayIntent = new Intent(this, NotificationService.class);
        stopSayIntent.putExtra("operation", STOP_SAY);
        PendingIntent stopSayPi = PendingIntent.getService(svcContext, 22, stopSayIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(stopSayPi);
        mRemoteViews.setOnClickPendingIntent(R.id.stop_now, stopSayPi);

//        Intent popIntent = new Intent(this, NotificationService.class);
//        popIntent.putExtra("operation", SPEAK_ON_OFF);
//        PendingIntent popPI = PendingIntent.getService(svcContext, 44, popIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
//        mBuilder.setContentIntent(popPI);
//        mRemoteViews.setOnClickPendingIntent(R.id.speak, popPI);
    }

    private void updateRemoteViews() {

        if (msgText == null) {
            mRemoteViews.setViewVisibility(R.id.msg_line, View.GONE);
        } else {
            mRemoteViews.setViewVisibility(R.id.msg_line, View.VISIBLE);
            mRemoteViews.setTextViewText(R.id.msg_time, time);
            mRemoteViews.setTextViewText(R.id.msg_who, who);
            mRemoteViews.setTextViewText(R.id.msg_text, msgText);
        }
        mNotificationManager.notify(100,mBuilder.build());
    }
}