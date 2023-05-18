package com.urrecliner.chattalk;

import static android.content.Context.MODE_PRIVATE;
import static com.urrecliner.chattalk.NotificationListener.notificationBar;
import static com.urrecliner.chattalk.SubFunc.logUpdate;
import static com.urrecliner.chattalk.SubFunc.sounds;
import static com.urrecliner.chattalk.Vars.alertLines;
import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.ActivityMain.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.urrecliner.chattalk.Sub.AlertLine;
import com.urrecliner.chattalk.Sub.Dot;
import com.urrecliner.chattalk.Sub.StockName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AlertStock {
    void sayNlog(String iGroup, String iText, int aIdx) {

        String key12, sTalk, group, who;
        if (utils == null)
            utils = new Utils();
        AlertLine al = alertLines.get(aIdx);
        al.matched++;
        alertLines.set(aIdx, al);
        String k1 = al.key1, k2 = al.key2;
        group = al.group;
        who = al.who;
        sTalk = al.talk;
        String percent = (iText.contains("매도") || iText.contains("익절"))? "1.9" :sTalk;
        key12 = " {" + k1 + "." + k2 + "}";
        String stockName = new StockName().get(al.prev, al.next, iText);
        String sText = utils.strReplace(iGroup, iText);
        String head = " [" + group + "." + who + "] "+stockName;
        String keyStr = key12+sTalk;
        Thread thisThread = new Thread(() -> {
            String timeStamp = new SimpleDateFormat("yy-MM-dd HH:mm", Locale.KOREA).format(new Date());
            String dotText = sText.replace(stockName, new Dot().add(stockName));
            FileIO.uploadStock(group, who, percent, stockName, dotText, keyStr, timeStamp);
            logUpdate.addStock(head, sText + keyStr);
            if (sTalk.length() > 0) {
                sounds.beepOnce(Vars.soundType.STOCK.ordinal());
                notificationBar.update("["+stockName+"]", "<"+who+"> "+sText, true);
                String[] strs = new String[]{group, group, who, stockName, sTalk, stockName};
                sounds.speakBuyStock(String.join(" , ", strs)); //.replaceAll("\\d","", )
            } else {
                notificationBar.update(stockName, who+" : "+sText, false);
                sounds.beepOnce(Vars.soundType.ONLY.ordinal());
            }
            save(al, mContext);
        });
        thisThread.start();
    }
    void save(AlertLine al, Context context) {

        SharedPreferences sharePref = context.getSharedPreferences("alertLine", MODE_PRIVATE);
        SharedPreferences.Editor sharedEditor = sharePref.edit();
        String[] joins = new String[]{"matched", al.group, al.who, al.key1, al.key2 };
        String keyVal = String.join("~~", joins);
        sharedEditor.putInt(keyVal, al.matched);
        sharedEditor.apply();
    }

}