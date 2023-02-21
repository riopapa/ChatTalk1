package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.SubFunc.logQueUpdate;
import static com.urrecliner.chattalk.SubFunc.sounds;
import static com.urrecliner.chattalk.SubFunc.utils;
import static com.urrecliner.chattalk.Vars.alertLines;
import static com.urrecliner.chattalk.Vars.alertsAdapter;

import com.urrecliner.chattalk.Sub.AlertLine;

public class AlertStock {

    void show(String sText, int aIdx) {

        String key12, sTalk, sayMore, group, who;
        AlertLine al = alertLines.get(aIdx);
        al.matched++;
        alertLines.set(aIdx, al);
        key12 = " {" + al.key1 + "/" + al.key2 + "}";
        group = al.group;
        who = al.who;
        sTalk = al.talk;
        sayMore = al.more;
        Thread thisThread = new Thread(() -> {
            String head = " [" + group + "." + who + "] ";
            FileIO.uploadStock(group, who, "", sTalk, sText, key12);
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
}