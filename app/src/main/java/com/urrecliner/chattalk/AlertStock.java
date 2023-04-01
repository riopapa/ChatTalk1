package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.SubFunc.logUpdate;
import static com.urrecliner.chattalk.SubFunc.sounds;
import static com.urrecliner.chattalk.Vars.alertLines;
import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.MainActivity.utils;

import com.urrecliner.chattalk.Sub.AlertLine;
import com.urrecliner.chattalk.Sub.AlertLinesPutMatch;
import com.urrecliner.chattalk.Sub.Dot;

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
        String stockName = getStockName(al.prev, al.next, iText);
        String sText = utils.strReplace(iGroup, iText);
        String head = " [" + group + "." + who + "] "+stockName;
        String keyStr = (sTalk.length() > 0) ? key12+"\n"+sTalk: key12;
        Thread thisThread = new Thread(() -> {
            String timeStamp = new SimpleDateFormat("yy-MM-dd HH:mm", Locale.KOREA).format(new Date());
            String dotText = sText.replace(stockName, new Dot().add(stockName));
            FileIO.uploadStock(group, who, percent, stockName, dotText, keyStr, timeStamp);
            NotificationBar.update(head, sText);
            logUpdate.addStock(head, sText + key12);
            if (sTalk.length() > 0) {
                String[] joins = new String[]{group, who, stockName, sTalk, stockName};
                sounds.speakAfterBeep(String.join(" , ", joins)); //.replaceAll("\\d","")
            } else {
                sounds.beepOnce(Vars.soundType.ONLY.ordinal());
            }
//            new AlertLinesGet().put(alertLines, mContext);
            new AlertLinesPutMatch().exe(al, mContext);
        });
        thisThread.start();
    }

    String getStockName(String prev, String next, String iText) {
        String str = iText;
        int p1 = str.indexOf(prev);
        if (p1 >= 0) {
            str = str.substring(p1+prev.length());
            p1 = str.indexOf(next);
            if (p1 > 0)
                return str.substring(0,p1).replaceAll("[\\d,%:|#()]","").trim();
            return "NoNext";
        }
        return "NoPrev";
    }
}