package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.MainActivity.utils;
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
    NotificationManager mNotificationManager;
    private RemoteViews mRemoteViews;
    private static final int ERASER1 = 1013;
    private static final int STOP_SAY1 = 10011;
    private static final int ERASER2 = 1023;
    private static final int STOP_SAY2 = 10021;
    String who1 = "Chat", msgText1 = "", time1 = "00:99";
    String who2 = "Talk", msgText2 = "", time2 = "00:99";

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
            case STOP_SAY1:
            case STOP_SAY2:
                sounds.stopTTS();
                break;

            case SHOW_MESSAGE:
                who2 = who1;
                msgText2 = msgText1;
                who1 = Objects.requireNonNull(intent.getStringExtra("who")).replace(" ", "\u00A0");
                while (ByteLength.get(who1) > 18)
                    who1 = who1.substring(0, who1.length()-1);
                msgText1 = utils.makeEtc(Objects.requireNonNull(intent.getStringExtra("msg")), 100).replace(" ", "\u00A0");

                break;

            case ERASER1:
                msgText1 = "";
                who1 = "Chat Talk..";
                break;
            case ERASER2:
                msgText2 = "";
                who2 = "Chat Talk..";
                break;
            default:
                break;
        }

        time2 = time1;
        time1 = new SimpleDateFormat("HH:mm", Locale.KOREA).format(new Date());

        updateRemoteViews();
        return START_STICKY;
    }

    private void createNotification() {

        if (null == mNotificationManager) {
            CharSequence name = "Chat Talk";
            String description = "Chat Talk Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("ChatTalk", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            mNotificationManager = getSystemService(NotificationManager.class);
            mNotificationManager.createNotificationChannel(channel);

        }
        if (null == mBuilder) {
            who1 = "newly Loaded";
            msgText1 = "";
            mBuilder = new NotificationCompat.Builder(this,"default")
                    .setSmallIcon(R.drawable.chat_talk)
                    .setColor(getApplicationContext().getColor(R.color.alertTalk))
                    .setContent(mRemoteViews)
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(false)
                    .setCustomBigContentView(mRemoteViews)
                    .setStyle(new NotificationCompat.BigTextStyle())
                    .setOngoing(true);
        }

        Intent mainIntent = new Intent(svcContext, MainActivity.class);
        mainIntent.putExtra("load","load");
        mRemoteViews.setOnClickPendingIntent(R.id.ll_customNotification, PendingIntent.getActivity(svcContext, 0, mainIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT));

        Intent erase1Intent = new Intent(this, NotificationService.class);
        erase1Intent.putExtra("operation", ERASER1);
        PendingIntent erase1Pi = PendingIntent.getService(svcContext, 11, erase1Intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(erase1Pi);
        mRemoteViews.setOnClickPendingIntent(R.id.erase1, erase1Pi);

        Intent erase2Intent = new Intent(this, NotificationService.class);
        erase2Intent.putExtra("operation", ERASER2);
        PendingIntent erase2Pi = PendingIntent.getService(svcContext, 12, erase2Intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(erase2Pi);
        mRemoteViews.setOnClickPendingIntent(R.id.erase2, erase2Pi);

        Intent stopSay1Intent = new Intent(this, NotificationService.class);
        stopSay1Intent.putExtra("operation", STOP_SAY1);
        PendingIntent stopSay1Pi = PendingIntent.getService(svcContext, 21, stopSay1Intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(stopSay1Pi);
        mRemoteViews.setOnClickPendingIntent(R.id.stop_now1, stopSay1Pi);

        Intent stopSay2Intent = new Intent(this, NotificationService.class);
        stopSay2Intent.putExtra("operation", STOP_SAY2);
        PendingIntent stopSay2Pi = PendingIntent.getService(svcContext, 22, stopSay2Intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(stopSay2Pi);
        mRemoteViews.setOnClickPendingIntent(R.id.stop_now1, stopSay2Pi);

    }

    private void updateRemoteViews() {

        mRemoteViews.setTextViewText(R.id.msg_time1, time1);
        mRemoteViews.setTextViewText(R.id.msg_who1, who1);
        mRemoteViews.setTextViewText(R.id.msg_text1, msgText1);
        mRemoteViews.setTextViewText(R.id.msg_time2, time2);
        mRemoteViews.setTextViewText(R.id.msg_who2, who2);
        mRemoteViews.setTextViewText(R.id.msg_text2, msgText2);
        mNotificationManager.notify(100,mBuilder.build());
    }
}