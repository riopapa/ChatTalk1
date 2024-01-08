package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.NotificationListener.alertStock;
import static com.urrecliner.chattalk.NotificationListener.loadFunction;
import static com.urrecliner.chattalk.NotificationListener.logUpdate;
import static com.urrecliner.chattalk.NotificationListener.notificationBar;
import static com.urrecliner.chattalk.NotificationListener.sounds;
import static com.urrecliner.chattalk.NotificationListener.utils;
import static com.urrecliner.chattalk.Vars.aAlertLineIdx;
import static com.urrecliner.chattalk.Vars.aGroupSaid;
import static com.urrecliner.chattalk.Vars.aGroupWhoKey1;
import static com.urrecliner.chattalk.Vars.aGroupWhoKey2;
import static com.urrecliner.chattalk.Vars.aGroupWhoSkip;
import static com.urrecliner.chattalk.Vars.aGroups;
import static com.urrecliner.chattalk.Vars.aGroupsPass;
import static com.urrecliner.chattalk.Vars.alertWhoIndex;
import static com.urrecliner.chattalk.Vars.alertsAdapter;
import static com.urrecliner.chattalk.Vars.mActivity;
import static com.urrecliner.chattalk.Vars.timeBegin;
import static com.urrecliner.chattalk.Vars.timeEnd;

import com.urrecliner.chattalk.Sub.Numbers;

import java.util.Collections;

class MsgKaTalk {
    void say(String group, String who, String text) {
        final String fText = text.trim();

        Thread thisThread = new Thread(() -> {
            if (utils == null)
                utils = new Utils();
            int gIdx = Collections.binarySearch(aGroups, group);
            if (gIdx >= 0) {    // within Alert Group
                if (aGroupsPass.get(gIdx) || fText.length() < 8 || fText.contains("http"))
                    return;
                if (aGroupSaid[gIdx].equals(fText))
                    return;
                aGroupSaid[gIdx] = fText;
                if (timeBegin == 0)
                    new ReadyToday();
                long nowTime = System.currentTimeMillis();
                if (nowTime < timeBegin || nowTime > timeEnd) {
                    return;
                }
                int gwIdx = alertWhoIndex.get(gIdx, who, fText);
                if (gwIdx == -1)
                    return;
                for (int i = 0; i < aGroupWhoKey1[gIdx][gwIdx].length; i++) {
                    if ((text.contains(aGroupWhoKey1[gIdx][gwIdx][i])) &&
                            (text.contains(aGroupWhoKey2[gIdx][gwIdx][i])) &&
                            (!text.contains(aGroupWhoSkip[gIdx][gwIdx][i]))) {
                        if (loadFunction == null)
                            loadFunction = new LoadFunction();
                        alertStock.sayNlog(group, text, aAlertLineIdx[gIdx][gwIdx][i]);
                        int fI = i;
                        if (alertsAdapter == null)
                            alertsAdapter = new AlertsAdapter();
                        else {
                            if (mActivity != null) {
                                mActivity.runOnUiThread(() ->
                                        alertsAdapter.notifyItemChanged(aAlertLineIdx[gIdx][gwIdx][fI])
                                );
                            }
                        }
                        return;
                    }
                }

            } else {    // normal group
                String head = "[카톡 " + group + "." + who + "]";
                String sText = utils.strShorten(group, fText);
                logUpdate.addLog(head, sText);
                sText = utils.makeEtc(sText, 160);
                notificationBar.update(group+":"+ who, sText, true);
                sText = "단톡방 " + group + " 에서 " + who + " 님이 " + new Numbers().deduct(sText);
                sounds.speakAfterBeep(utils.replaceKKHH(sText));
            }
        });
        thisThread.start();
    }
}