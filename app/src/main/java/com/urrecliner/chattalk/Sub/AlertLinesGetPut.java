package com.urrecliner.chattalk.Sub;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AlertLinesGetPut {
    public void put(ArrayList<AlertLine> alertLines, Context context) {

        SharedPreferences sharePref = context.getSharedPreferences("alertLine", MODE_PRIVATE);
        SharedPreferences.Editor sharedEditor = sharePref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(alertLines);
        sharedEditor.putString("alertLine", json);
        sharedEditor.apply();
    }

    public ArrayList<AlertLine> get(Context context) {

        ArrayList<AlertLine> list;
        SharedPreferences sharePref = context.getSharedPreferences("alertLine", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharePref.getString("alertLine", "");
        if (json.isEmpty()) {
            list = new ArrayList<>();
        } else {
            Type type = new TypeToken<List<AlertLine>>() {
            }.getType();
            list = gson.fromJson(json, type);
        }
        return list;
    }

}