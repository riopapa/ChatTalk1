package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.NotificationListener.alertStock;
import static com.urrecliner.chattalk.NotificationListener.loadFunction;
import static com.urrecliner.chattalk.NotificationListener.logUpdate;
import static com.urrecliner.chattalk.NotificationListener.notificationBar;
import static com.urrecliner.chattalk.NotificationListener.notificationService;
import static com.urrecliner.chattalk.NotificationListener.sounds;
import static com.urrecliner.chattalk.NotificationListener.utils;
import static com.urrecliner.chattalk.Vars.aAlertLineIdx;
import static com.urrecliner.chattalk.Vars.aGSkip1;
import static com.urrecliner.chattalk.Vars.aGSkip2;
import static com.urrecliner.chattalk.Vars.aGSkip3;
import static com.urrecliner.chattalk.Vars.aGroupSaid;
import static com.urrecliner.chattalk.Vars.aGroupWhoKey1;
import static com.urrecliner.chattalk.Vars.aGroupWhoKey2;
import static com.urrecliner.chattalk.Vars.aGroupWhoSkip;
import static com.urrecliner.chattalk.Vars.aGroups;
import static com.urrecliner.chattalk.Vars.aGroupsPass;
import static com.urrecliner.chattalk.Vars.alertWhoIndex;
import static com.urrecliner.chattalk.Vars.alertsAdapter;
import static com.urrecliner.chattalk.Vars.mActivity;
import static com.urrecliner.chattalk.Vars.sbnGroup;
import static com.urrecliner.chattalk.Vars.sbnText;
import static com.urrecliner.chattalk.Vars.sbnWho;
import static com.urrecliner.chattalk.Vars.timeBegin;
import static com.urrecliner.chattalk.Vars.timeEnd;

import android.util.Log;

import com.urrecliner.chattalk.Sub.Numbers;

import java.util.ArrayList;
import java.util.Collections;

class MsgKaTalk {

    public MsgKaTalk(String str) {
        Log.e("MsgKaTalk", "new "+str);
    }

    void say(String group, String who, String text) {

//        Thread thisThread = new Thread(() -> {
            int grpIdx = Collections.binarySearch(aGroups, group);
            if (grpIdx >= 0) {    // within Alert Group
                if (timeBegin == 0)
                    new ReadyToday();
                long nowTime = System.currentTimeMillis();
                if (nowTime < timeBegin || nowTime > timeEnd) {
                    return;
                }
                if (aGroupsPass.get(grpIdx) || text.length() < 8)
                    return;
                if (aGroupSaid[grpIdx].equals(text))
                    return;
                aGroupSaid[grpIdx] = text;
                if (text.contains(aGSkip1[grpIdx]) || text.contains(aGSkip2[grpIdx]) ||
                        text.contains(aGSkip3[grpIdx]))
                    return;
                int gWhoIdx = alertWhoIndex.get(grpIdx, who, text);
                if (gWhoIdx == -1)
                    return;
//                if (group.startsWith("텔"))
//                    utils.logW("Grp " + sbnGroup, "_" + sbnWho + "_ : " + sbnText);


                for (int i = 0; i < aGroupWhoKey1[grpIdx][gWhoIdx].length; i++) {
                    if (!text.contains(aGroupWhoKey1[grpIdx][gWhoIdx][i]))
                        continue;
//                    Log.w("Grp " + sbnGroup + "_" + sbnWho, " k1 check "+ aGroupWhoKey1[grpIdx][gWhoIdx][i]);

                    if (!text.contains(aGroupWhoKey2[grpIdx][gWhoIdx][i]))
                        continue;
//                    Log.w("Grp " + sbnGroup + "_" + sbnWho, " k2 check "+ aGroupWhoKey2[grpIdx][gWhoIdx][i]);
                    if (text.contains(aGroupWhoSkip[grpIdx][gWhoIdx][i]))
                            continue;
//                    Log.w("Grp " + sbnGroup + "_" + sbnWho, aGroupWhoSkip[grpIdx][gWhoIdx][i]+ " no skip");
                    if (loadFunction == null)
                        loadFunction = new LoadFunction();
                    alertStock.sayNlog(group, text, aAlertLineIdx[grpIdx][gWhoIdx][i]);
                    int fI = i;
                    if (alertsAdapter == null)
                        alertsAdapter = new AlertsAdapter();
                    else {
//                        if (mActivity != null) {
//                            mActivity.runOnUiThread(() ->
                                    alertsAdapter.notifyItemChanged(aAlertLineIdx[grpIdx][gWhoIdx][fI]);
//                            );
//                        }
                    }
                    return;
                }

            } else {    // normal group
                if (utils == null)
                    utils = new Utils();
                String head = "[카톡 " + group + "." + who + "]";
                String sText = utils.strShorten(group, text);
                logUpdate.addLog(head, sText);
                sText = utils.makeEtc(sText, 160);
                NotificationBar.update(group+":"+ who, sText, true);
                sText = "단톡방 " + group + " 에서 " + who + " 님이 " + new Numbers().deduct(sText);
                sounds.speakAfterBeep(utils.replaceKKHH(sText));
            }
//        });
//        thisThread.start();
    }
}