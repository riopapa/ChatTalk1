package com.urrecliner.chattalk;

import static android.content.Context.MODE_PRIVATE;
import static com.urrecliner.chattalk.Vars.alertLines;
import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.Vars.tableFolder;
import static com.urrecliner.chattalk.Vars.todayFolder;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.urrecliner.chattalk.Sub.AlertLine;
import com.urrecliner.chattalk.Sub.ByteLength;
import com.urrecliner.chattalk.Sub.SnackBar;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;

public class AlertSave {
    public AlertSave(String msg) {
        AlertTable.sort();
        AlertTable.makeArrays();
        String sv = "sv";
        int[] padLen = getMaxLengths();
        StringBuilder s = new StringBuilder();
        for (AlertLine al : alertLines) {
            if (!(al.group+al.who).equals(sv)) {
                sv = al.group+al.who;
                s.append("\n");
            }
            s.append(strPad(al.group, padLen[0])).append("^");
            s.append(strPad(al.who, padLen[1])).append("^");
            s.append(strPad(al.key1, padLen[2])).append("^");
            s.append(strPad(al.key2, padLen[3])).append("^");
            s.append(strPad(al.talk, (padLen[4]))).append("^");
            String m = "     "+al.matched+"  ";
            s.append(m.substring(m.length()-7)).append("^");
            s.append(strPad(al.skip, (padLen[5]))).append("^");
            s.append(" ").append(al.memo);
            s.append("~").append(al.more).append("^");
            s.append(strPad(al.prev, (padLen[6]))).append("^");
            s.append(strPad(al.next, (padLen[7]))).append("^");
            s.append("\n");
        }
        if (todayFolder == null)
            new ReadyToday();
        saveTable();
//        new AlertTableIO().remove(alertLines, mContext);
        new SnackBar().show("Alert Table", msg);
    }

    void saveTable() {
        SharedPreferences sharePref = mContext.getSharedPreferences("alertLine", MODE_PRIVATE);
        SharedPreferences.Editor sharedEditor = sharePref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(alertLines);
        sharedEditor.putString("alertLine", json);
        json = json.replace("},{","},\n\n{")
                .replace("\"next\":","\n\"next\":");
        FileIO.writeFile( tableFolder,"alertTable.json",json,"");
        FileIO.writeFile( todayFolder,"alertTable.json",json,"");

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

    String strPad(String s, int size) {
        s = s.trim();
        int chars = calcBytes(s);
        if (chars >= size)
            return s;
        int padL = (size - chars) / 2;
        int padR = size - chars - padL;
        return blank.substring(0, padL) + s + blank.substring(0, padR);
    }

    int [] getMaxLengths() {
        int [] maxLen = new int[8];
        int bl;
        for (AlertLine al : alertLines){
            bl = calcBytes(al.group); if (bl > maxLen[0]) maxLen[0] = bl;
            bl = calcBytes(al.who); if (bl > maxLen[1]) maxLen[1] = bl;
            bl = calcBytes(al.key1); if (bl > maxLen[2]) maxLen[2] = bl;
            bl = calcBytes(al.key2); if (bl > maxLen[3]) maxLen[3] = bl;
            bl = calcBytes(al.talk); if (bl > maxLen[4]) maxLen[4] = bl;
            bl = calcBytes(al.skip); if (bl > maxLen[5]) maxLen[5] = bl;
            bl = calcBytes(al.prev); if (bl > maxLen[6]) maxLen[6] = bl;
            bl = calcBytes(al.next); if (bl > maxLen[7]) maxLen[7] = bl;
        }
        for (int i = 0; i < 8; i++)
            maxLen[i] = maxLen[i] + maxLen[i] % 2 + 2;
        return maxLen;
    }

    String blank = StringUtils.repeat(" ", 20);

    int calcBytes(String s) {
        return ByteLength.get(s) + 4;
    }
}