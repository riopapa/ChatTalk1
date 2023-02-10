package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.alertLines;
import static com.urrecliner.chattalk.Vars.kKey1;
import static com.urrecliner.chattalk.Vars.kKey2;
import static com.urrecliner.chattalk.Vars.logQueUpdate;
import static com.urrecliner.chattalk.Vars.sounds;
import static com.urrecliner.chattalk.Vars.speakSwitchOn;
import static com.urrecliner.chattalk.Vars.utils;

import com.urrecliner.chattalk.Sub.AlertLine;

public class MsgStock {
    String key12, sTalk, sayMore, group, who;

    void alert(String sText, int aIdx) {

        AlertLine al = alertLines.get(aIdx);
        al.matched++;
        alertLines.set(aIdx, al);
        key12 = " {" + kKey1[aIdx] + "/" + kKey2[aIdx] + "}";
        group = al.group;
        who = al.who;
        sTalk = al.talk;
        sayMore = al.more;

        Thread thisThread = new Thread(() -> {
            String head = " [" + group + "." + who + "] ";
            FileIO.uploadStock(group, who, "", sTalk, sText, key12);
            NotificationBar.update(head, sText);
            logQueUpdate.add(head, sText + key12);
            if (speakSwitchOn || sTalk.length() > 0) {
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