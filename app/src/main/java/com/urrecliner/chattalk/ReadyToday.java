package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.NotificationListener.kvCommon;
import static com.urrecliner.chattalk.NotificationListener.kvKakao;
import static com.urrecliner.chattalk.NotificationListener.kvSMS;
import static com.urrecliner.chattalk.NotificationListener.kvStock;
import static com.urrecliner.chattalk.NotificationListener.kvTelegram;
import static com.urrecliner.chattalk.Vars.logQue;
import static com.urrecliner.chattalk.Vars.logStock;
import static com.urrecliner.chattalk.Vars.packageDirectory;
import static com.urrecliner.chattalk.Vars.sharedEditor;
import static com.urrecliner.chattalk.Vars.tableFolder;
import static com.urrecliner.chattalk.Vars.timeBegin;
import static com.urrecliner.chattalk.Vars.timeEnd;
import static com.urrecliner.chattalk.Vars.toDay;
import static com.urrecliner.chattalk.Vars.todayFolder;

import com.urrecliner.chattalk.Sub.KeyVal;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReadyToday {
    public ReadyToday() {
        String nowDay = new SimpleDateFormat("yy-MM-dd", Locale.KOREA).format(new Date());
        if (toDay.equals(nowDay))
            return;
        toDay = nowDay;
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY, 8);
        c.set(Calendar.MINUTE, 30);
        timeBegin =c.getTimeInMillis();
        c.set(Calendar.HOUR_OF_DAY, 15);
        timeEnd = c.getTimeInMillis();

        todayFolder = new File(packageDirectory, toDay);
        if (!todayFolder.exists()) {
            if (todayFolder.mkdirs())
                new Utils().deleteOldFiles();
            String new_day = "\n" + new SimpleDateFormat("MM-dd (EEE) HH:mm ", Locale.KOREA).format(new Date())
                    + " NEW DAY " + " **/\nNew Day" + "\n";
            logQue += new_day;
            sharedEditor.apply();
            FileIO.writeFile(tableFolder, "logStock.txt", logStock);
            FileIO.writeFile(tableFolder, "logQue.txt", logQue);
            StringBuilder sb = new StringBuilder();
            sb.append("\n\nkvCommon =\n").append(kvCommon.toString());
            sb.append("\n\nkvSMS =\n").append(kvSMS.toString());
            sb.append("\n\nkvTelegram =\n").append(kvTelegram.toString());
            sb.append("\n\nkvStock =\n").append(kvStock.toString());
            sb.append("\n\nkvKakao =\n").append(kvKakao.toString());
            FileIO.writeFile(todayFolder, "keyVal.txt", sb.toString());
            kvCommon = new KeyVal();
            kvStock = new KeyVal();
            kvSMS = new KeyVal();
            kvTelegram = new KeyVal();
            kvKakao = new KeyVal();
        }

    }
}
