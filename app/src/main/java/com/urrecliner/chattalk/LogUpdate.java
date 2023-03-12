package com.urrecliner.chattalk;

import static android.content.Context.MODE_PRIVATE;
import static com.urrecliner.chattalk.Vars.downloadFolder;
import static com.urrecliner.chattalk.Vars.logQue;
import static com.urrecliner.chattalk.Vars.logSave;
import static com.urrecliner.chattalk.Vars.logStock;
import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.Vars.packageDirectory;
import static com.urrecliner.chattalk.Vars.sharePref;
import static com.urrecliner.chattalk.Vars.sharedEditor;
import static com.urrecliner.chattalk.Vars.toDay;
import static com.urrecliner.chattalk.Vars.todayFolder;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
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

    final SimpleDateFormat MMDDHHMM = new SimpleDateFormat("MM-dd HH:mm ", Locale.KOREA);
    void addQue(String header, String text) {
        readyTodayFolderIfNewDay();
        logQue += "\n" + MMDDHHMM.format(new Date())
                + header + "\n" + text+"\n";
        int len = logQue.length();
        if (len > 15000) {   // max log que size
            logQue = logQue.substring(5000);    // remove old 3000 bytes
            logQue = logQue.substring(logQue.indexOf("\n")+1);
            if (!StringUtils.isNumeric(""+logQue.charAt(0))) {  // start with MMDD ...
                logQue = logQue.substring(logQue.indexOf("\n")+1);
            }
            String front = logQue.substring(0, logQue.length()*2/3);
            front = front.substring(0, front.lastIndexOf("\n"));
            int pos = front.lastIndexOf("\n", front.length()-2);
            if (!StringUtils.isNumeric(""+front.charAt(pos))) {  // start with MMDD ...
                pos = front.lastIndexOf("\n", pos-2);
            }
            front = logQue.substring(0, pos).replace("\n\n","\n");
            front = front.replace("\n\n","\n");
            String remain = logQue.substring(pos);
            logQue = front+"\n\n/** " + MMDDHHMM.format(new Date()) + " **/" +
                    "\n---- squeezed to "+ (front.length()+remain.length()) + " -------" +
                    "\n"+remain +
                    "\n---- squeezed " + MMDDHHMM.format(new Date()) + "\n";
        }
        sharedEditor.putString("logQue", logQue);
        sharedEditor.apply();
    }
    void addStock(String header, String text) {
        readyTodayFolderIfNewDay();
        logStock += "\n" + MMDDHHMM.format(new Date())
                + header + "\n" + text+"\n";
        int len = logStock.length();
        if (len > 5000) {   // max log que size
            logStock = logStock.substring(2000);    // remove old 3000 bytes
            logStock = logStock.substring(logStock.indexOf("\n")+1);
            if (!StringUtils.isNumeric(""+logStock.charAt(0))) {  // start with MMDD ...
                logStock = logStock.substring(logStock.indexOf("\n")+1);
            }
            String front = logStock.substring(0, logStock.length()*2/3);
            front = front.substring(0, front.lastIndexOf("\n"));
            int pos = front.lastIndexOf("\n", front.length()-2);
            if (!StringUtils.isNumeric(""+front.charAt(pos))) {  // start with MMDD ...
                pos = front.lastIndexOf("\n", pos-2);
            }
            front = logStock.substring(0, pos).replace("\n\n","\n");
            front = front.replace("\n\n","\n");
            String remain = logStock.substring(pos);
            logStock = front+"\n\n/** " + MMDDHHMM.format(new Date()) + " **/" +
                    "\n---- squeezed to "+ (front.length()+remain.length()) + " -------" +
                    "\n"+remain +
                    "\n---- squeezed " + MMDDHHMM.format(new Date()) + "\n";
        }
        sharedEditor.putString("logStock", logStock);
        sharedEditor.apply();
    }

    public void readyTodayFolderIfNewDay() {
        String nowDay = new SimpleDateFormat("yy-MM-dd", Locale.KOREA).format(new Date());
        if (toDay != null && toDay.equals(nowDay))
            return;
        toDay = nowDay;
        todayFolder = new File(packageDirectory, toDay);
        if (!todayFolder.exists()) {
            if (todayFolder.mkdirs()) {
                logQue += "\n /** " + toDay + new SimpleDateFormat(" (EEE) HH:mm ", Locale.KOREA).format(new Date()) + " NEW DAY " + " **/\nNew Day" + "\n";
                logStock += "\n /** " + toDay + new SimpleDateFormat(" (EEE) HH:mm ", Locale.KOREA).format(new Date()) + " NEW DAY " + " **/\nNew Day" + "\n";
                sharedEditor.putString("logQue", logQue);
                sharedEditor.putString("logStock", logStock);
                sharedEditor.apply();
                String logQueFile = "logQue " + toDay + ".txt";
                FileIO.writeKR(new File(todayFolder, logQueFile), logQue);
                FileIO.writeKR(new File(downloadFolder, "logQue.txt"), logQue);
                String logStockFile = "logStock " + toDay + ".txt";
                FileIO.writeKR(new File(todayFolder, logStockFile), logStock);
                FileIO.writeKR(new File(downloadFolder, "logStock.txt"), logStock);
                new Utils().deleteOldFiles();
            }
        }
    }

}