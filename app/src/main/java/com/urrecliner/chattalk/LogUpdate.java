package com.urrecliner.chattalk;

import static android.content.Context.MODE_PRIVATE;
import static com.urrecliner.chattalk.NotificationListener.vars;
import static com.urrecliner.chattalk.Vars.logQue;
import static com.urrecliner.chattalk.Vars.logSave;
import static com.urrecliner.chattalk.Vars.logStock;
import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.Vars.packageDirectory;
import static com.urrecliner.chattalk.Vars.sharePref;
import static com.urrecliner.chattalk.Vars.sharedEditor;
import static com.urrecliner.chattalk.Vars.tableFolder;
import static com.urrecliner.chattalk.Vars.toDay;
import static com.urrecliner.chattalk.Vars.todayFolder;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LogUpdate {

    public LogUpdate(Context context) {
        mContext = context;
        if (sharePref == null) {
            sharePref = mContext.getSharedPreferences("sayText", MODE_PRIVATE);
            sharedEditor = sharePref.edit();
        }
        logQue = sharePref.getString("logQue", "");
        logStock = sharePref.getString("logStock", "");
        logSave = sharePref.getString("logSave", "");
    }

    final SimpleDateFormat TIME_INFO = new SimpleDateFormat("MM-dd HH:mm ", Locale.KOREA);
    void addQue(String header, String text) {
        readyTodayFolderIfNewDay();
        logQue += "\n" + TIME_INFO.format(new Date()) + header + "\n" + text+"\n";
        if (logQue.length() > 15000)
            logQue = squeezeQue(logQue);

        sharedEditor.putString("logQue", logQue);
        sharedEditor.apply();
    }

    void addStock(String header, String text) {
        readyTodayFolderIfNewDay();
        logStock += "\n" + TIME_INFO.format(new Date()) + header + "\n" + text+"\n";
        if (logStock.length() > 8000)
            logStock = squeezeQue(logStock);

        sharedEditor.putString("logStock", logStock);
        sharedEditor.apply();
    }

    private String squeezeQue(String logStr) {
        logStr = logStr.substring(5000);    // remove old 3000 bytes
        logStr = logStr.substring(logStr.indexOf("\n")+1);
        if (!StringUtils.isNumeric(""+logStr.charAt(0))) {  // start with MMDD ...
            logStr = logStr.substring(logStr.indexOf("\n")+1);
        }
        String front = logStr.substring(0, logStr.length()*2/3);
        front = front.substring(0, front.lastIndexOf("\n"));
        int pos = front.lastIndexOf("\n", front.length()-2);
        if (!StringUtils.isNumeric(""+front.charAt(pos))) {  // start with MMDD ...
            pos = front.lastIndexOf("\n", pos-2);
        }
        front = logStr.substring(0, pos).replace("\n\n","\n");
        front = front.replace("\n\n","\n");
        String remain = logStr.substring(pos);
        logStr = front+"\n\n" + TIME_INFO.format(new Date()) + " **/" +
                "\n---- squeezed to "+ (front.length()+remain.length()) + " -------" +
                "\n"+remain +
                "\n---- squeezed " + TIME_INFO.format(new Date()) + "\n";
        return logStr;
    }

    public void readyTodayFolderIfNewDay() {
        String nowDay = new SimpleDateFormat("yy-MM-dd", Locale.KOREA).format(new Date());
        if (toDay != null && toDay.equals(nowDay))
            return;
        toDay = nowDay;
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY, 8);
        c.set(Calendar.MINUTE, 30);
        vars.timeBegin = c.getTimeInMillis();
        c.set(Calendar.HOUR_OF_DAY, 16);
        vars.timeEnd = c.getTimeInMillis();

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
                String logStockFile = "logStock " + toDay + ".txt";
                FileIO.writeKR(new File(todayFolder, logStockFile), logStock);
                FileIO.writeKR(new File(tableFolder, "logStock.txt"), logStock);
                String logQueFile = "logQue " + toDay + ".txt";
                FileIO.writeKR(new File(todayFolder, logQueFile), logQue);
                FileIO.writeKR(new File(tableFolder, "logQue.txt"), logQue);
                new Utils().deleteOldFiles();
            }
        }
    }

}