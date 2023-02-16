package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.SubFunc.logQueUpdate;
import static com.urrecliner.chattalk.SubFunc.sounds;
import static com.urrecliner.chattalk.SubFunc.utils;
import static com.urrecliner.chattalk.Vars.aAlertLineIdx;
import static com.urrecliner.chattalk.Vars.aGroupSaid;
import static com.urrecliner.chattalk.Vars.aGroupWhoKey1;
import static com.urrecliner.chattalk.Vars.aGroupWhoKey2;
import static com.urrecliner.chattalk.Vars.aGroupWhoSkip;
import static com.urrecliner.chattalk.Vars.aGroups;
import static com.urrecliner.chattalk.Vars.alertWhoIndex;
import static com.urrecliner.chattalk.Vars.nineIgnores;
import static com.urrecliner.chattalk.Vars.sharedFinish;
import static com.urrecliner.chattalk.Vars.sharedStart;

import com.urrecliner.chattalk.Sub.IsWhoNine;

import java.util.Collections;

class MsgKaTalk {

    void say(String iGroup, String iWho, String iText) {
        int gIdx = Collections.binarySearch(aGroups, iGroup);
        if (gIdx >= 0) {    // within Alert Group
            if (iText.length() < 8 || iText.contains("http"))
                return;
            if (aGroupSaid[gIdx].equals(iText))
                return;
            aGroupSaid[gIdx] = iText;
//            long nowTime = System.currentTimeMillis();
//            if (nowTime < sharedStart || nowTime > sharedFinish)
//                return;
            int gwIdx = alertWhoIndex.get(gIdx, iWho, iText);
            if (gwIdx == -1)
                return;

            for (int i = 0; i < aGroupWhoKey1[gIdx][gwIdx].length; i++) {
                if ((iText.contains(aGroupWhoKey1[gIdx][gwIdx][i])) &&
                    (iText.contains(aGroupWhoKey2[gIdx][gwIdx][i])) &&
                    (!iText.contains(aGroupWhoSkip[gIdx][gwIdx][i])))
                    new AlertStock().show(utils.strReplace(iGroup, utils.removeSpecialChars(iText)), aAlertLineIdx[gIdx][gwIdx][i]);
            }

        } else {    // normal group
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