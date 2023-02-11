package com.urrecliner.chattalk.Sub;

import static com.urrecliner.chattalk.SubFunc.logQueUpdate;
import static com.urrecliner.chattalk.Vars.sharePref;
import static com.urrecliner.chattalk.Vars.toDay;

import android.content.SharedPreferences;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeBoundary {

    public long sharedStart, sharedFinish;
    public void set() {
        final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yy-MM-dd HH:mm", Locale.KOREA);
        if (toDay.equals("ToDay"))
            logQueUpdate.readyTodayFolderIfNewDay();
        Date currentDay = dateTimeFormat.parse(toDay+" 08:40", new ParsePosition(0));
        sharedStart = currentDay.getTime();
        currentDay = dateTimeFormat.parse(toDay+" 18:30", new ParsePosition(0));
        sharedFinish = currentDay.getTime();
        SharedPreferences.Editor editor = sharePref.edit();
        editor.putLong("start", sharedStart);
        editor.putLong("finish",sharedFinish);
        editor.apply();
    }

}