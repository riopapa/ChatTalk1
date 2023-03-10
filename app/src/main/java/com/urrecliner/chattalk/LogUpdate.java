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

    }

    public void readyTodayFolderIfNewDay() {
        String nowDay = new SimpleDateFormat("yy-MM-dd", Locale.KOREA).format(new Date());
        if (toDay != null && toDay.equals(nowDay))
            return;
        toDay = nowDay;
        todayFolder = new File(packageDirectory, toDay);
        if (!todayFolder.exists()) {
            if (todayFolder.mkdirs()) {
                String logQueFile = "logQue " + toDay + ".txt";
                FileIO.writeKR(new File(todayFolder, logQueFile), logQue);
                FileIO.writeKR(new File(downloadFolder, "logQue.txt"), logQue);
                logQue += "\n /** " + toDay + new SimpleDateFormat(" (EEE) HH:mm ", Locale.KOREA).format(new Date()) + " NEW DAY " + " **/\nNew Day" + "\n";
                String logStockFile = "logStock " + toDay + ".txt";
                FileIO.writeKR(new File(todayFolder, logStockFile), logStock);
                FileIO.writeKR(new File(downloadFolder, "logStock.txt"), logStock);
                logStock += "\n /** " + toDay + new SimpleDateFormat(" (EEE) HH:mm ", Locale.KOREA).format(new Date()) + " NEW DAY " + " **/\nNew Day" + "\n";
                new Utils().deleteOldFiles();
            }
        }
    }

}