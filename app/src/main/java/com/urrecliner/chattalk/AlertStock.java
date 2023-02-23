package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.SubFunc.logQueUpdate;
import static com.urrecliner.chattalk.SubFunc.sounds;
import static com.urrecliner.chattalk.SubFunc.utils;
import static com.urrecliner.chattalk.Vars.alertLines;
import static com.urrecliner.chattalk.Vars.alertsAdapter;

import android.util.Log;

import com.urrecliner.chattalk.Sub.AlertLine;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AlertStock {

    void show(String iGroup, String iText, int aIdx) {

        String key12, sTalk, sayMore, group, who;
        AlertLine al = alertLines.get(aIdx);
        al.matched++;
        alertLines.set(aIdx, al);
        String k1 = al.key1, k2 = al.key2;
        String prev = al.prev;
        group = al.group;
        who = al.who;
        sTalk = al.talk;
        sayMore = al.more;
        key12 = " {" + k1 + "/" + k2 + "}";
        String stockName = getStockName(al.prev, al.next, iText);
        String sText = utils.strReplace(iGroup, utils.removeSpecialChars(iText));
        String head = " [" + group + "." + who + "] "+stockName;
        Thread thisThread = new Thread(() -> {
            String timeStamp = new SimpleDateFormat("yy-MM-dd HH:mm", Locale.KOREA).format(new Date());
            FileIO.uploadStock(group, who, stockName, sTalk, sText, key12, timeStamp);
            NotificationBar.update(head, sText);
            logQueUpdate.add(head, sText + key12);
            if (sTalk.length() > 0) {
                String[] joins = new String[]{sTalk, group, sTalk, who,
                        (sayMore.length()> 0) ? sayMore:"", sText,
                        utils.makeEtc(sText, 80)};
                sounds.speakAfterBeep(String.join(" ", joins).replaceAll("[0-9]",""));
            } else {
                sounds.beepOnce(Vars.soundType.ONLY.ordinal());
            }
        });
        thisThread.start();
    }

    String getStockName(String prev, String next, String iText) {
        String [] texts = iText.split(prev);
        try {
            if (texts.length> 1) {
                for (int i = 0; i < texts.length; i++) {
                    if (texts[i].equals(prev)) {
                        String[] txts = texts[i].split(next);
                        if (txts.length > 1) {
                            for (int j = 1; j < txts.length; j++) {
                                if (txts[j].equals(next))
                                    return txts[j - 1]
                                            .replace("[0-9]()%","").trim();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "NoName";
    }
}