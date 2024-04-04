package biz.riopapa.chattalk;

import static biz.riopapa.chattalk.NotificationListener.alertStock;
import static biz.riopapa.chattalk.NotificationListener.loadFunction;
import static biz.riopapa.chattalk.Vars.aAlertLineIdx;
import static biz.riopapa.chattalk.Vars.aGSkip1;
import static biz.riopapa.chattalk.Vars.aGSkip2;
import static biz.riopapa.chattalk.Vars.aGSkip3;
import static biz.riopapa.chattalk.Vars.aGroupSaid;
import static biz.riopapa.chattalk.Vars.aGroupWhoKey1;
import static biz.riopapa.chattalk.Vars.aGroupWhoKey2;
import static biz.riopapa.chattalk.Vars.aGroupWhoSkip;
import static biz.riopapa.chattalk.Vars.aGroups;
import static biz.riopapa.chattalk.Vars.aGroupsPass;
import static biz.riopapa.chattalk.Vars.alertWhoIndex;
import static biz.riopapa.chattalk.Vars.alertsAdapter;
import static biz.riopapa.chattalk.Vars.timeBegin;
import static biz.riopapa.chattalk.Vars.timeEnd;

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