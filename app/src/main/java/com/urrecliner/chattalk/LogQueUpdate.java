package com.urrecliner.chattalk;

import static android.content.Context.MODE_PRIVATE;
import static com.urrecliner.chattalk.Vars.logQue;
import static com.urrecliner.chattalk.Vars.logSave;
import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.Vars.packageDirectory;
import static com.urrecliner.chattalk.Vars.sharePref;
import static com.urrecliner.chattalk.Vars.sharedEditor;
import static com.urrecliner.chattalk.Vars.toDay;
import static com.urrecliner.chattalk.Vars.todayFolder;
import static com.urrecliner.chattalk.Vars.utils;

import android.content.Context;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogQueUpdate {

    public LogQueUpdate (Context context) {
        mContext = context;
        if (sharePref == null) {
            sharePref = mContext.getSharedPreferences("sayText", MODE_PRIVATE);
            sharedEditor = sharePref.edit();
        }
        logQue = sharePref.getString("logQue", "");
        logSave = sharePref.getString("logSave", "");
    }

    final SimpleDateFormat MMDDHHMM = new SimpleDateFormat("MM-dd HH:mm ", Locale.KOREA);
    void add(String header, String text) {
        readyTodayFolderIfNewDay();
        logQue += "\n" + MMDDHHMM.format(new Date())
                + header + "\n" + text+"\n";
        int len = logQue.length();
        if (len > 15000) {   // max log que size
            logQue = logQue.substring(3000);    // remove old 3000 bytes
            logQue = logQue.substring(logQue.indexOf("\n")+1);
            if (!StringUtils.isNumeric(""+logQue.charAt(0))) {  // start with MMDD ...
                logQue = logQue.substring(logQue.indexOf("\n")+1);
            }
            String front = logQue.substring(0, logQue.length()*2/3);
            front = front.substring(0, front.lastIndexOf("\n"));
            int pos = front.lastIndexOf("\n", front.length()-2);
            if (!StringUtils.isNumeric(""+front.charAt(pos-1))) {  // start with MMDD ...
                pos = front.lastIndexOf("\n", pos-2);
            }
            front = logQue.substring(0, pos).replace("\n\n","\n");
            front = front.replace("\n\n","\n");
            String remain = logQue.substring(pos);
            logQue = front+"\n\n/** " + MMDDHHMM.format(new Date()) + " **/" +
                    "\n---- squeezed to "+ (front.length()+remain.length()) + " -------" +
                    "\n"+remain;
        }
        sharedEditor.putString("logQue", logQue);
        sharedEditor.apply();
    }

    void readyTodayFolderIfNewDay() {
        String nowDay = new SimpleDateFormat("yy-MM-dd", Locale.KOREA).format(new Date());
        if (toDay.equals(nowDay))
            return;
        toDay = nowDay;
        utils.setTimeBoundary();
        todayFolder = new File(packageDirectory, toDay);
        if (!todayFolder.exists()) {
            if (todayFolder.mkdirs()) {
                String logQueFile = "logQue " + toDay;
                FileIO.writeTextFile(todayFolder, logQueFile, logQue);
                logQue += "\n\n /** " + toDay + new SimpleDateFormat(" (EEE) HH:mm ", Locale.KOREA).format(new Date()) + " NEW DAY " + " **/\nNew Day" + "\n\n";
                utils.deleteOldFiles();
            }
        }
    }
}