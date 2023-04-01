package com.urrecliner.chattalk.Sub;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class AlertLinesGetMatch {
    public void exe(AlertLine al, Context context) {
        SharedPreferences sharePref = context.getSharedPreferences("alertLine", MODE_PRIVATE);
        String[] joins = new String[]{"matched", al.group, al.who, al.key1, al.key2 };
        String keyVal = String.join("~~", joins);
        int matchCount =  sharePref.getInt(keyVal, -3);
        if (matchCount != -3)
            al.matched = matchCount;
    }

}
