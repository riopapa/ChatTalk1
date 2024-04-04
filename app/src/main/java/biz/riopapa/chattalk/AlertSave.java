package biz.riopapa.chattalk;

import static android.content.Context.MODE_PRIVATE;
import static biz.riopapa.chattalk.Vars.alertLines;
import static biz.riopapa.chattalk.Vars.mContext;
import static biz.riopapa.chattalk.Vars.tableFolder;
import static biz.riopapa.chattalk.Vars.todayFolder;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import biz.riopapa.chattalk.Sub.SnackBar;
import biz.riopapa.chattalk.model.AlertLine;

public class AlertSave {
    public AlertSave(String msg) {
        new SnackBar().show("Alert Table", msg);
        AlertTable.sort();
        if (todayFolder == null)
            new ReadyToday();
        SharedPreferences sharePref = mContext.getSharedPreferences("alertLine", MODE_PRIVATE);
        SharedPreferences.Editor sharedEditor = sharePref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(alertLines);
        sharedEditor.putString("alertLine", json);
        json = json.replace("},{","},\n\n{")
                .replace("\"next\":","\n\"next\":");
        FileIO.writeFile( tableFolder,"alertTable.json",json);
        AlertTable.makeArrays();
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