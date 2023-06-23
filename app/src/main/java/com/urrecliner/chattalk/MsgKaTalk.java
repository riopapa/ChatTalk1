package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.NotificationListener.notificationBar;
import static com.urrecliner.chattalk.NotificationListener.subFunc;
import static com.urrecliner.chattalk.NotificationListener.utils;
import static com.urrecliner.chattalk.NotificationListener.vars;
import static com.urrecliner.chattalk.Vars.aAlertLineIdx;
import static com.urrecliner.chattalk.Vars.aGroupSaid;
import static com.urrecliner.chattalk.Vars.aGroupWhoKey1;
import static com.urrecliner.chattalk.Vars.aGroupWhoKey2;
import static com.urrecliner.chattalk.Vars.aGroupWhoSkip;
import static com.urrecliner.chattalk.Vars.aGroups;
import static com.urrecliner.chattalk.Vars.alertWhoIndex;
import static com.urrecliner.chattalk.Vars.alertsAdapter;
import static com.urrecliner.chattalk.Vars.mActivity;
import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.Vars.nineIgnores;

import com.urrecliner.chattalk.Sub.IsWhoNine;

import java.util.Collections;

class MsgKaTalk {
    void say(String iGroup, String who, String text) {
        final String iText = text;
        Thread thisThread = new Thread(() -> {
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
                int gwIdx = alertWhoIndex.get(gIdx, who, iText);
                if (gwIdx == -1)
                    return;
                String sText = utils.removeSpecialChars(text);
                for (int i = 0; i < aGroupWhoKey1[gIdx][gwIdx].length; i++) {
                    if ((sText.contains(aGroupWhoKey1[gIdx][gwIdx][i])) &&
                            (sText.contains(aGroupWhoKey2[gIdx][gwIdx][i])) &&
                            (!sText.contains(aGroupWhoSkip[gIdx][gwIdx][i]))) {
                        if (subFunc == null)
                            subFunc  = new SubFunc();
                        subFunc.alertStock.sayNlog(iGroup, sText, aAlertLineIdx[gIdx][gwIdx][i]);
                        int finalI = i;
                        mActivity.runOnUiThread(() -> {
                            if (alertsAdapter == null)
                                alertsAdapter = new AlertsAdapter();
                            alertsAdapter.notifyItemChanged(aAlertLineIdx[gIdx][gwIdx][finalI]);
                        });
                        return;
                    }
                }

            } else {    // normal group
                String head = "[카톡 " + iGroup + "." + who + "]";
                String sText = utils.strReplace(iGroup, iText);
                notificationBar.update(iGroup+":"+ who, sText, true);
                subFunc.logUpdate.addQue(head, sText);
                if (IsWhoNine.in(nineIgnores, who))
                    sText = sText.replaceAll("\\d","");
                sText = "단톡방 " + iGroup + " 에서 " + who + " 님이 " + utils.makeEtc(sText, 180);
                subFunc.sounds.speakAfterBeep(utils.replaceKKHH(sText));
            }
        });
        thisThread.start();
    }
}