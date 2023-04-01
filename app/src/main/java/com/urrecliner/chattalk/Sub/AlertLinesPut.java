package com.urrecliner.chattalk.Sub;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;

public class AlertLinesPut {

    public void exe(ArrayList<AlertLine> alertLines, Context context) {
        SharedPreferences sharePref = context.getSharedPreferences("alertLine", MODE_PRIVATE);
        SharedPreferences.Editor sharedEditor = sharePref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(alertLines);
        sharedEditor.putString("alertLine", json);

        for (int i = 0; i < alertLines.size(); i++) {
            AlertLine al = alertLines.get(i);
            if (al.matched != -1) {
                String[] joins = new String[]{"matched", al.group, al.who, al.key1, al.key2};
                String keyVal = String.join("~~", joins);
                sharedEditor.putInt(keyVal, al.matched);
            }
        }
        sharedEditor.apply();
    }
}
