package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.NotificationListener.notificationBar;
import static com.urrecliner.chattalk.NotificationListener.subFunc;
import static com.urrecliner.chattalk.NotificationListener.vars;
import static com.urrecliner.chattalk.SubFunc.logUpdate;
import static com.urrecliner.chattalk.SubFunc.sounds;
import static com.urrecliner.chattalk.Vars.aAlertLineIdx;
import static com.urrecliner.chattalk.Vars.aGroupSaid;
import static com.urrecliner.chattalk.Vars.aGroupWhoKey1;
import static com.urrecliner.chattalk.Vars.aGroupWhoKey2;
import static com.urrecliner.chattalk.Vars.aGroupWhoSkip;
import static com.urrecliner.chattalk.Vars.aGroups;
import static com.urrecliner.chattalk.Vars.alertWhoIndex;
import static com.urrecliner.chattalk.Vars.alertsAdapter;
import static com.urrecliner.chattalk.Vars.nineIgnores;
import static com.urrecliner.chattalk.ActivityMain.utils;

import android.util.Log;

import com.urrecliner.chattalk.Sub.IsWhoNine;

import java.util.Collections;

class MsgKaTalk {

    public MsgKaTalk() {
        utils.logW("kakaoTalk", "Initiated");
    }

    void say(String iGroup, String iWho, String iText) {
        if (utils == null)
            utils = new Utils();
        int gIdx = Collections.binarySearch(aGroups, iGroup);
        if (gIdx >= 0) {    // within Alert Group
            if (iText.length() < 8 || iText.contains("http"))
                return;
            if (aGroupSaid[gIdx].equals(iText))
                return;
            aGroupSaid[gIdx] = iText;
            if (vars.timeBegin == 0)
                new ReadyToday();
            long nowTime = System.currentTimeMillis();
            if (nowTime < vars.timeBegin || nowTime > vars.timeEnd) {
                return;
            }
            int gwIdx = alertWhoIndex.get(gIdx, iWho, iText);
            if (gwIdx == -1)
                return;
            iText = utils.removeSpecialChars(iText);
            for (int i = 0; i < aGroupWhoKey1[gIdx][gwIdx].length; i++) {
                if ((iText.contains(aGroupWhoKey1[gIdx][gwIdx][i])) &&
                    (iText.contains(aGroupWhoKey2[gIdx][gwIdx][i])) &&
                    (!iText.contains(aGroupWhoSkip[gIdx][gwIdx][i]))) {
                    if (subFunc == null)
                        subFunc  = new SubFunc();
                    subFunc.alertStock.sayNlog(iGroup, iText, aAlertLineIdx[gIdx][gwIdx][i]);
                    if (alertsAdapter == null)
                        alertsAdapter = new AlertsAdapter();
                    alertsAdapter.notifyItemChanged(aAlertLineIdx[gIdx][gwIdx][i]);
                    return;
                }
            }

        } else {    // normal group
            String head = "[카톡 " + iGroup + "." + iWho + "]";
            iText = utils.strReplace(iGroup, iText);
            notificationBar.update(iGroup+":"+iWho, iText, true);
            logUpdate.addQue(head, iText);
            if (IsWhoNine.in(nineIgnores, iWho))
                iText = iText.replaceAll("\\d","");
            iText = "단톡방 " + iGroup + " 에서 " + iWho + " 님이 " + utils.makeEtc(iText, 180);
            sounds.speakAfterBeep(utils.replaceKKHH(iText));
        }
    }
}