package com.urrecliner.chattalk;

import static android.content.Context.MODE_PRIVATE;
import static com.urrecliner.chattalk.Vars.logQue;
import static com.urrecliner.chattalk.Vars.logSave;
import static com.urrecliner.chattalk.Vars.logStock;
import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.Vars.sharePref;
import static com.urrecliner.chattalk.Vars.sharedEditor;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;

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
    void addQue(String header, String text) {
        new ReadyToday();
        logQue += "\n" + TIME_INFO.format(new Date()) + header + "\n" + text+"\n";
        if (logQue.length() > 10000)
            logQue = squeezeQue(logQue);

        sharedEditor.putString("logQue", logQue);
        sharedEditor.apply();
    }

    void addStock(String header, String text) {
        new ReadyToday();
        logStock += "\n" + TIME_INFO.format(new Date()) + header + "\n" + text+"\n";
        if (logStock.length() > 6000)
            logStock = squeezeQue(logStock);

        sharedEditor.putString("logStock", logStock);
        sharedEditor.apply();
    }

    /*
        Remove 1/3 lines, then 2/3 is without \n
     */
    private String squeezeQue(String logStr) {
        logStr = logStr.replace("    ","")
                        .replace("\n\n","\n");
        String [] sLog = logStr.split("\n");
        int sLen = sLog.length;
        int r = sLen / 3;   // remove 1/3 front part

        while (sLog[r].length() < 2)
            r++;
        while (!StringUtils.isNumeric(""+sLog[r].charAt(0)))
            r++;
        StringBuilder sb = new StringBuilder();
        for (; r < sLen * 2/3; r++) {   // without blank line
            if (sLog[r].length() > 80)
                sLog[r] = sLog[r].substring(0,80) + " ....";
            sb.append(sLog[r]).append("\n");
        }
        for (; r < sLen; r++) { // with blank line
            if (sLog[r].length() > 80)
                sLog[r] = sLog[r].substring(0,80);
            else if (sLog[r].length() == 0)
                continue;;
            if (StringUtils.isNumeric(""+sLog[r].charAt(0)))
                sb.append("\n");
            sb.append("\n").append(sLog[r]);
        }
        sb.append("\n\n").append(TIME_INFO.format(new Date()))
                .append(" **/").append("\n---- squeezed  -----\n\n");
        return  sb.toString();

    }

}