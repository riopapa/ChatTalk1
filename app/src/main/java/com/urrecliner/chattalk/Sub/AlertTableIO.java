package com.urrecliner.chattalk.Sub;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.urrecliner.chattalk.FileIO;
import com.urrecliner.chattalk.ReadyToday;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AlertTableIO {

    public ArrayList<AlertLine> get(Context context, File tableFolder) {

        ArrayList<AlertLine> list;
        Gson gson = new Gson();
        String json = FileIO.readFile(tableFolder, "alertTable.json");
//
//        } else {
//            SharedPreferences sharePref = context.getSharedPreferences("alertLine", MODE_PRIVATE);
//            json = sharePref.getString("alertLine", "");
//        }
        if (json.isEmpty()) {
            list = new ArrayList<>();
        } else {
            Type type = new TypeToken<List<AlertLine>>() {
            }.getType();
            list = gson.fromJson(json, type);
        }
        return list;
    }

    public void remove(ArrayList<AlertLine> alertLines, Context context) {

        SharedPreferences sharePref = context.getSharedPreferences("alertLine", MODE_PRIVATE);
        SharedPreferences.Editor sharedEditor = sharePref.edit();
        Map<String, ?> map = sharePref.getAll();
        for(Map.Entry<String,?> entry : map.entrySet()){
            String [] grpWho = entry.getKey().split("~~");
            if (grpWho[0].equals("matched")) {
                int idx = -1;
                for (int i = 0; i < alertLines.size(); i++) {
                    AlertLine al = alertLines.get(i);
                    if (al.group.equals(grpWho[1]) && al.who.equals(grpWho[2]) &&
                            al.key1.equals(grpWho[3]) && al.key2.equals(grpWho[4])) {
                        idx = i;
                        break;
                    }
                }
                if (idx == -1) {
//                    Log.w("sharedPref","removing ... " +entry.getKey());
                    sharedEditor.remove(entry.getKey());
                }
            }
        }
        sharedEditor.apply();
    }

}