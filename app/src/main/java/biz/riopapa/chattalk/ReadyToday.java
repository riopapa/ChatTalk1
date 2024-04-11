package biz.riopapa.chattalk;

import static biz.riopapa.chattalk.NotificationListener.kvCommon;
import static biz.riopapa.chattalk.NotificationListener.kvKakao;
import static biz.riopapa.chattalk.NotificationListener.kvSMS;
import static biz.riopapa.chattalk.NotificationListener.kvStock;
import static biz.riopapa.chattalk.NotificationListener.kvTelegram;
import static biz.riopapa.chattalk.Vars.logQue;
import static biz.riopapa.chattalk.Vars.logStock;
import static biz.riopapa.chattalk.Vars.logWork;
import static biz.riopapa.chattalk.Vars.packageDirectory;
import static biz.riopapa.chattalk.Vars.sharedEditor;
import static biz.riopapa.chattalk.Vars.tableFolder;
import static biz.riopapa.chattalk.Vars.timeBegin;
import static biz.riopapa.chattalk.Vars.timeEnd;
import static biz.riopapa.chattalk.Vars.toDay;
import static biz.riopapa.chattalk.Vars.todayFolder;

import biz.riopapa.chattalk.Sub.KeyVal;

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
            logWork += new_day;
            sharedEditor.apply();
            FileIO.writeFile(tableFolder, "logStock.txt", logStock);
            FileIO.writeFile(tableFolder, "logQue.txt", logQue);
            FileIO.writeFile(tableFolder, "logWork.txt", logWork);
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
