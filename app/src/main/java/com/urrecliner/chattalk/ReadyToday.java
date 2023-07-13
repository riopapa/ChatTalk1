package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.logQue;
import static com.urrecliner.chattalk.Vars.logStock;
import static com.urrecliner.chattalk.Vars.packageDirectory;
import static com.urrecliner.chattalk.Vars.sharedEditor;
import static com.urrecliner.chattalk.Vars.tableFolder;
import static com.urrecliner.chattalk.Vars.timeBegin;
import static com.urrecliner.chattalk.Vars.timeEnd;
import static com.urrecliner.chattalk.Vars.toDay;
import static com.urrecliner.chattalk.Vars.todayFolder;

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
        c.set(Calendar.HOUR_OF_DAY, 16);
        timeEnd = c.getTimeInMillis();

        todayFolder = new File(packageDirectory, toDay);
        if (!todayFolder.exists()) {
            if (todayFolder.mkdirs()) {
                String new_day = "\n" + new SimpleDateFormat("MM-dd (EEE) HH:mm ", Locale.KOREA).format(new Date())
                        + " NEW DAY " + " **/\nNew Day" + "\n";
                logQue += new_day;
                logStock += new_day;
                sharedEditor.putString("logQue", logQue);
                sharedEditor.putString("logStock", logStock);
                sharedEditor.apply();
//                String logStockFile = "logStock " + toDay + ".txt";
//                FileIO.writeKR(new File(todayFolder, logStockFile), logStock);
                FileIO.writeKR(new File(tableFolder, "logStock.txt"), logStock);
//                String logQueFile = "logQue " + toDay + ".txt";
//                FileIO.writeKR(new File(todayFolder, logQueFile), logQue);
                FileIO.writeKR(new File(tableFolder, "logQue.txt"), logQue);
                new Utils().deleteOldFiles();
            }
        }
    }
}
