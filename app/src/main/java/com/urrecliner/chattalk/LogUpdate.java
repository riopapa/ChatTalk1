package com.urrecliner.chattalk;

import static android.content.Context.MODE_PRIVATE;
import static com.urrecliner.chattalk.Vars.logQue;
import static com.urrecliner.chattalk.Vars.logSave;
import static com.urrecliner.chattalk.Vars.logStock;
import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.Vars.packageDirectory;
import static com.urrecliner.chattalk.Vars.sharePref;
import static com.urrecliner.chattalk.Vars.sharedEditor;

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

    final SimpleDateFormat TIME_INFO = new SimpleDateFormat("MM-dd HH:mm ", Locale.KOREA);
    void addLog(String header, String text) {
        if (text.length() < 10)
            return;
        new ReadyToday();
        logQue += "\n" + TIME_INFO.format(new Date()) + header + "\n" + text+"\n";
        if (logQue.length() > 32000) {
            Thread logThread = new Thread(() -> {
                logQue = squeezeLog(logQue, "logQue");
                sharedEditor.putString("logQue", logQue);
                sharedEditor.apply();
            });
            logThread.start();
        } else {
            sharedEditor.putString("logQue", logQue);
            sharedEditor.apply();
        }
    }

    void addStock(String header, String text) {
        new ReadyToday();
        logStock += "\n" + TIME_INFO.format(new Date()) + header + "\n" + text+"\n";
        if (logStock.length() > 12000) {
            Thread stockThread = new Thread(() -> {
                logStock = squeezeLog(logStock, "logStock");
                sharedEditor.putString("logStock", logStock);
                sharedEditor.apply();
            });
            stockThread.start();
        } else {
            sharedEditor.putString("logStock", logStock);
            sharedEditor.apply();
        }
    }

    /*
        Remove upper lines, then 3/4 is without \n
     */
    String squeezeLog(String logStr, String queName) {
        logStr = logStr.replace("    ","")
                        .replace("\n\n","\n");
        String [] sLog = logStr.split("\n");
        int sLen = sLog.length;
        int row = sLen / 4;   // remove 1/4 front part

        while (sLog[row].length() < 6)
            row++;
        while (!StringUtils.isNumeric(""+sLog[row].charAt(0)))
            row++;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < row; i++)
            sb.append(sLog[i].trim()).append("\n");
        FileIO.append2File(new File(packageDirectory, queName+".txt"), "\n- backUp -\n", sb+"\n");

        sb = new StringBuilder();
        for (; row < sLen * 3/4; row++) {   // without blank line
            String s = sLog[row].trim();
            if (!s.isEmpty()) {
                if (s.length() > 50)
                    s = s.substring(0, 50) + "...";
                sb.append(s).append("\n");
            }
        }
        sb.append("- sqz -\n");

        for (; row < sLen; row++) { // with blank line
            String s = sLog[row].trim();
            if (!s.isEmpty()) {
                if (StringUtils.isNumeric("" + s.charAt(0)))
                    sb.append("\n");
                sb.append("\n").append(s);
            }
        }
        return  sb.toString();
    }

}
