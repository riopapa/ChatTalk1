package com.urrecliner.chattalk;

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
import static com.urrecliner.chattalk.Vars.systemIgnores;

import android.app.Notification;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.urrecliner.chattalk.Sub.AppsTable;
import com.urrecliner.chattalk.Sub.IgnoreThis;

import java.util.Collections;

public class SbnBundle {

    Utils utils = null;
    public boolean bypassSbn(StatusBarNotification sbn) {

        sbnAppName = sbn.getPackageName();  // tolowCase
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
            if (sbnText.length() > 10 && !IgnoreThis.contains(sbnText, systemIgnores)
                    && !IgnoreThis.contains(sbnWho, systemIgnores)) {
                if (utils == null)
                    utils = new Utils();
                sbnText = utils.text2OneLine(sbnText);
                new MsgAndroid().say(sbnAppName, sbnWho, sbnText);
            }
            return true;
        }

        if (sbnAppName.equals("com.kakao.talk")) {
            sbnAppNick = "카톡";
            sbnAppType = "kk";

        } else if (sbnAppName.equals("viva.republica.toss")) {
                sbnAppNick = "토스";
                sbnAppType = "tos";

        } else if (sbnAppName.equals("org.telegram.messenger")) {
            sbnAppNick = "텔레";
            sbnAppType = "tG";

        } else if (sbnAppName.equals("com.samsung.android.messaging")) {
            sbnAppNick = "문자";
            sbnAppType = "sms";

        } else {
            if (apps == null) {
                apps = new AppsTable().get();
                Log.w("reloading", "apps is null new size="+apps.size());
            }
            if (appIgnores == null || appIgnores.size() == 0)
                apps = new AppsTable().get();
            if (Collections.binarySearch(appIgnores, sbnAppName) >= 0)
                return true;
            sbnAppIdx = Collections.binarySearch(appFullNames, sbnAppName);
//            Log.w("sa0 a idx="+sbnAppIdx, "searched");
            if (sbnAppIdx >= 0) {
                sbnAppIdx = appNameIdx.get(sbnAppIdx);
//                Log.w("saa ", sbnAppName);
                sbnApp = apps.get(sbnAppIdx);
                sbnAppNick = sbnApp.nickName;
                sbnAppType = "app";
//                Log.w("saNick"+ sbnAppNick, sbnAppName
//                        +" After "+sbnAppIdx+" "+sbnApp.fullName);
            } else {
                sbnAppNick = "None";
                sbnAppType = "None";
                sbnAppIdx = -1;
            }
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