package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.NotificationListener.alertStock;
import static com.urrecliner.chattalk.NotificationListener.loadFunction;
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
import static com.urrecliner.chattalk.Vars.timeBegin;
import static com.urrecliner.chattalk.Vars.timeEnd;

import android.util.Log;

import java.util.Collections;

public class MsgKeyword {

    public MsgKeyword(String str) {
        Log.e("MsgKeyword", "new "+str);
    }

    void say(String group, String who, String text, int grpIdx) {

        if (timeBegin == 0)
            new ReadyToday();
        long nowTime = System.currentTimeMillis();
        if (nowTime < timeBegin || nowTime > timeEnd) {
            return;
        }
        if (text.length() < 14 || aGroupsPass.get(grpIdx) || aGroupSaid[grpIdx].equals(text))
            return;
        aGroupSaid[grpIdx] = text;
        if (text.contains(aGSkip1[grpIdx]) || text.contains(aGSkip2[grpIdx]) ||
                text.contains(aGSkip3[grpIdx]))
            return;
        int gWhoIdx = alertWhoIndex.get(grpIdx, who, text);
        if (gWhoIdx == -1)
            return;

        for (int i = 0; i < aGroupWhoKey1[grpIdx][gWhoIdx].length; i++) {
            if (!text.contains(aGroupWhoKey1[grpIdx][gWhoIdx][i]))
                continue;
            if (!text.contains(aGroupWhoKey2[grpIdx][gWhoIdx][i]))
                continue;
            if (text.contains(aGroupWhoSkip[grpIdx][gWhoIdx][i]))
                continue;
            if (loadFunction == null)
                loadFunction = new LoadFunction();
            if (alertStock == null)
                alertStock = new AlertStock();

            alertStock.sayNlog(group, text, aAlertLineIdx[grpIdx][gWhoIdx][i]);
            if (alertsAdapter == null)
                alertsAdapter = new AlertsAdapter();
            else {
                alertsAdapter.notifyItemChanged(aAlertLineIdx[grpIdx][gWhoIdx][i]);
            }
            return;
        }
    }
}