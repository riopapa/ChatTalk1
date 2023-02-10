package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.alertIndex;
import static com.urrecliner.chattalk.Vars.aGroupDot;
import static com.urrecliner.chattalk.Vars.logQueUpdate;
import static com.urrecliner.chattalk.Vars.nineIgnores;
import static com.urrecliner.chattalk.Vars.sharedFinish;
import static com.urrecliner.chattalk.Vars.sharedStart;
import static com.urrecliner.chattalk.Vars.sounds;
import static com.urrecliner.chattalk.Vars.utils;

import com.urrecliner.chattalk.Sub.IsWhoNine;

class MsgKaTalk {

    void say(String iGroup, String iWho, String iText) {

        int groupPos = aGroupDot.indexOf(iGroup+"!");
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
            NotificationBar.update(iGroup+":"+iWho, iText);
            logQueUpdate.add(head, iText);
            if (IsWhoNine.in(nineIgnores, iWho))
                iText = iText.replaceAll("[0-9]","");
            iText = "단톡방 " + iGroup + " 에서 " + iWho + " 님이 " + utils.makeEtc(iText, 180);
            sounds.speakAfterBeep(utils.replaceKKHH(iText));
        }
    }
}