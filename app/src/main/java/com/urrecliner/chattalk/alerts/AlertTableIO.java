package com.urrecliner.chattalk.alerts;

import static android.content.Context.MODE_PRIVATE;

import static com.urrecliner.chattalk.Vars.alertLines;
import static com.urrecliner.chattalk.Vars.downloadFolder;
import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.Vars.tableFolder;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.urrecliner.chattalk.AlertTable;
import com.urrecliner.chattalk.FileIO;
import com.urrecliner.chattalk.model.AlertLine;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AlertTableIO {

    public void get() {
        if (tableFolder ==  null) {
            downloadFolder = new File(Environment.getExternalStorageDirectory(), "download");
            tableFolder = new File(downloadFolder, "_ChatTalk");
        }

        ArrayList<AlertLine> list;
        Gson gson = new Gson();
        String json = FileIO.readFile(tableFolder, "alertTable.json");
        if (json.isEmpty()) {
            list = new ArrayList<>();
        } else {
            Type type = new TypeToken<List<AlertLine>>() {
            }.getType();
            list = gson.fromJson(json, type);
        }
        alertLines = list;
        updateMatched();
        AlertTable.makeArrays();
    }

    void updateMatched() {
        SharedPreferences sharePref = mContext.getSharedPreferences("alertLine", MODE_PRIVATE);
        for (int i = 0; i < alertLines.size(); i++) {
            AlertLine al = alertLines.get(i);
            if (al.matched >= 0) {
                String[] joins = new String[]{"matched", al.group, al.who, al.key1, al.key2 };
                String keyVal = String.join("~~", joins);
                int matchCount =  sharePref.getInt(keyVal, -3);
                if (matchCount != -3)
                    al.matched = matchCount;
                alertLines.set(i, al);
            }
        }
    }

//    public ArrayList<AlertLine> put() {
//        if (tableFolder ==  null) {
//            downloadFolder = new File(Environment.getExternalStorageDirectory(), "download");
//            tableFolder = new File(downloadFolder, "_ChatTalk");
//        }
//
//        ArrayList<AlertLine> list;
//        Gson gson = new Gson();
//        String json = FileIO.readFile(tableFolder, "alertTable.json");
//        if (json.isEmpty()) {
//            list = new ArrayList<>();
//        } else {
//            Type type = new TypeToken<List<AlertLine>>() {
//            }.getType();
//            list = gson.fromJson(json, type);
//        }
//        return list;
//    }

}