package com.urrecliner.chattalk.Sub;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class AlertLinesPutMatch {

    public void exe(AlertLine al, Context context) {

        SharedPreferences sharePref = context.getSharedPreferences("alertLine", MODE_PRIVATE);
        SharedPreferences.Editor sharedEditor = sharePref.edit();
        String[] joins = new String[]{"matched", al.group, al.who, al.key1, al.key2 };
        String keyVal = String.join("~~", joins);
        sharedEditor.putInt(keyVal, al.matched);
        sharedEditor.apply();
    }

}
