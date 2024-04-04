package biz.riopapa.chattalk;

import static biz.riopapa.chattalk.NotificationListener.utils;
import static biz.riopapa.chattalk.Vars.appNameIdx;
import static biz.riopapa.chattalk.Vars.apps;
import static biz.riopapa.chattalk.Vars.appIgnores;
import static biz.riopapa.chattalk.Vars.appFullNames;
import static biz.riopapa.chattalk.Vars.sbnApp;
import static biz.riopapa.chattalk.Vars.sbnAppName;
import static biz.riopapa.chattalk.Vars.sbnGroup;
import static biz.riopapa.chattalk.Vars.sbnAppIdx;
import static biz.riopapa.chattalk.Vars.sbnAppNick;
import static biz.riopapa.chattalk.Vars.sbnAppType;
import static biz.riopapa.chattalk.Vars.sbnText;
import static biz.riopapa.chattalk.Vars.sbnWho;

import android.app.Notification;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import biz.riopapa.chattalk.Sub.AppsTable;
import biz.riopapa.chattalk.Sub.IgnoreThis;

import java.util.Collections;

public class SbnBundle {

    public SbnBundle() {
        Log.w("sbnBundle","new SbnBundle");
    }

    public boolean bypassSbn(StatusBarNotification sbn) {

        sbnAppName = sbn.getPackageName();  // to LowCase
        if (sbnAppName.isEmpty())
            return true;
        Notification mNotification = sbn.getNotification();
        Bundle extras = mNotification.extras;
        // get eText //
        try {
            sbnText = ""+extras.get(Notification.EXTRA_TEXT);
            if (sbnText.isEmpty() || sbnText.equals("null"))
                return true;
        } catch (Exception e) {
            return true;
        }
        // get eWho //
        try {
            sbnWho = ""+extras.get(Notification.EXTRA_TITLE);
            if (sbnWho.equals("null"))
                sbnWho = "";
        } catch (Exception e) {
            new Utils().logW("sbn WHO Error", "no Who "+ sbnAppName +" "+sbnText);
            return true;
        }
        if (apps == null || appIgnores == null) {
            new AppsTable().get();
            Log.e("reloading", "apps is null new size=" + apps.size());
        }

        switch (sbnAppName) {

            case "com.kakao.talk":
                sbnAppNick = "카톡";
                sbnAppType = "kk";
                break;

            case "android":
                if (Collections.binarySearch(appIgnores, sbnAppName) >= 0)
                    return true;
                sbnAppIdx = Collections.binarySearch(appFullNames, sbnAppName);
                sbnApp = apps.get(sbnAppIdx);
                sbnAppNick = sbnApp.nickName;
                sbnAppType = "app";
                return false;

            case "com.samsung.android.messaging":
                sbnAppNick = "문자";
                sbnAppType = "sms";
                return false;

            default:
                if (Collections.binarySearch(appIgnores, sbnAppName) >= 0)
                    return true;
                sbnAppIdx = Collections.binarySearch(appFullNames, sbnAppName);
                if (sbnAppIdx >= 0) {
                    sbnAppIdx = appNameIdx.get(sbnAppIdx);
                    sbnApp = apps.get(sbnAppIdx);
                    sbnAppNick = sbnApp.nickName;
                    sbnAppType = "app";
                } else {
                    sbnAppNick = "None";
                    sbnAppType = "None";
                    sbnAppIdx = -1;
                }
                break;
        }

        // get eGroup //
        try {
            sbnGroup = extras.getString(Notification.EXTRA_SUB_TEXT);
            if (sbnGroup == null || sbnGroup.equals("null"))
                sbnGroup = "";
        } catch (Exception e) {
            sbnGroup = "";
        }
        return false;
    }
}