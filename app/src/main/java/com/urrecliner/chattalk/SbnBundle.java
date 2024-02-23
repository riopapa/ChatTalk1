package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.NotificationListener.utils;
import static com.urrecliner.chattalk.Vars.appNameIdx;
import static com.urrecliner.chattalk.Vars.apps;
import static com.urrecliner.chattalk.Vars.appIgnores;
import static com.urrecliner.chattalk.Vars.appFullNames;
import static com.urrecliner.chattalk.Vars.sbnApp;
import static com.urrecliner.chattalk.Vars.sbnAppName;
import static com.urrecliner.chattalk.Vars.sbnGroup;
import static com.urrecliner.chattalk.Vars.sbnAppIdx;
import static com.urrecliner.chattalk.Vars.sbnAppNick;
import static com.urrecliner.chattalk.Vars.sbnAppType;
import static com.urrecliner.chattalk.Vars.sbnText;
import static com.urrecliner.chattalk.Vars.sbnWho;
import static com.urrecliner.chattalk.Vars.sysIgnores;

import android.app.Notification;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.urrecliner.chattalk.Sub.AppsTable;
import com.urrecliner.chattalk.Sub.IgnoreThis;

import java.util.Collections;

public class SbnBundle {

    public SbnBundle() {
        Log.w("sbnBundle","new SbnBundle");
    }

    public boolean bypassSbn(StatusBarNotification sbn) {

        sbnAppName = sbn.getPackageName();  // to LowCase
        if (sbnAppName.equals(""))
            return true;
        Notification mNotification = sbn.getNotification();
        Bundle extras = mNotification.extras;
        // get eText //
        try {
            sbnText = ""+extras.get(Notification.EXTRA_TEXT);
            if (sbnText.equals("") || sbnText.equals("null"))
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

        if (sbnAppName.equals("android")) {
            if (sbnText.length() > 8 && !IgnoreThis.contains(sbnText, sysIgnores)
                    && !IgnoreThis.contains(sbnWho, sysIgnores)) {
                if (utils == null)
                    utils = new Utils();
                sbnText = utils.text2OneLine(sbnText);
                new MsgAndroid().say(sbnAppName, sbnWho, sbnText);
            }
            return true;
        }

        switch (sbnAppName) {
            case "com.kakao.talk":
                sbnAppNick = "카톡";
                sbnAppType = "kk";
                break;

            case "viva.republica.toss":
                sbnAppNick = "토스";
                sbnAppType = "tos";
                break;

            case "org.telegram.messenger":
                sbnAppNick = "텔레";
                sbnAppType = "tG";
                break;

            case "com.samsung.android.messaging":
                sbnAppNick = "문자";
                sbnAppType = "sms";
                break;

            default:
                if (apps == null || appIgnores == null) {
                    new AppsTable().get();
                    Log.e("reloading", "apps is null new size=" + apps.size());
                }
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