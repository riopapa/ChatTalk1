package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.alertIndex;
import static com.urrecliner.chattalk.Vars.kGroupDot;
import static com.urrecliner.chattalk.Vars.logQueUpdate;
import static com.urrecliner.chattalk.Vars.sharedFinish;
import static com.urrecliner.chattalk.Vars.sharedStart;
import static com.urrecliner.chattalk.Vars.sounds;
import static com.urrecliner.chattalk.Vars.utils;

import com.urrecliner.chattalk.Sub.IsWhoText;

import java.util.ArrayList;

class MsgKaTalk {

    static ArrayList<Vars.WhoText> kkWhoTexts = new ArrayList<>();

    void say(String iGroup, String iWho, String iText) {

        if (IsWhoText.repeated(kkWhoTexts, iWho, iText))
            return;
        int groupPos = kGroupDot.indexOf(iGroup+"!");
        if (groupPos > 0) {
            if (iText.length() < 8 || iText.contains("http"))
                return;
            long nowTime = System.currentTimeMillis();
            if (nowTime < sharedStart || nowTime > sharedFinish)
                return;
            int aIdx = alertIndex.get(groupPos, iGroup, iWho, iText);
            if (aIdx == -1)
                return;
            new MsgStock().alert(utils.strReplace(iGroup, utils.removeSpecialChars(iText)), aIdx);

        } else {
            String head = "[카톡 " + iGroup + "." + iWho + "]";
            iText = utils.strReplace(iGroup, iText);
            NotificationBar.update(head + " "+iText);
            logQueUpdate.add(head, iText);
            iText = "단톡방 " + iGroup + " 에서 " + iWho + " 님이 " + utils.makeEtc(iText, 180);
            sounds.speakAfterBeep(utils.replaceKKHH(iText));
        }
    }
}