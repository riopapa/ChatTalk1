package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.packageIgnores;
import static com.urrecliner.chattalk.Vars.pkgFullNames;
import static com.urrecliner.chattalk.Vars.pkgNickNames;
import static com.urrecliner.chattalk.Vars.pkgTypes;
import static com.urrecliner.chattalk.Vars.sbnAppFullName;
import static com.urrecliner.chattalk.Vars.sbnGroup;
import static com.urrecliner.chattalk.Vars.sbnPackageNick;
import static com.urrecliner.chattalk.Vars.sbnPackageType;
import static com.urrecliner.chattalk.Vars.sbnText;
import static com.urrecliner.chattalk.Vars.sbnWho;
import static com.urrecliner.chattalk.Vars.systemIgnores;

import android.app.Notification;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;

import com.urrecliner.chattalk.Sub.IgnoreThis;

import java.util.Collections;

public class SbnBundle {

    Utils utils = null;
    public boolean bypassSbn(StatusBarNotification sbn) {

        sbnAppFullName = sbn.getPackageName().toLowerCase();
        if (sbnAppFullName.equals(""))
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
            new Utils().logW("sbn WHO Error", "no Who "+sbnAppFullName+" "+sbnText);
            return true;
        }
        if (sbnAppFullName.equals("android")) {
            if (sbnText.length() > 10 && !IgnoreThis.contains(sbnText, systemIgnores)
                    && !IgnoreThis.contains(sbnWho, systemIgnores)) {
                if (utils == null)
                    utils = new Utils();
                sbnText = utils.text2OneLine(sbnText);
                new MsgAndroid().say(sbnAppFullName, sbnWho, sbnText);
            }
            return true;
        }

        if (sbnAppFullName.equals("com.kakao.talk")) {
            sbnPackageNick = "카톡";
            sbnPackageType = "kk";

        } else if (sbnAppFullName.equals("viva.republica.toss")) {
                sbnPackageNick = "토스";
                sbnPackageType = "tos";

        } else {
            if (packageIgnores == null || packageIgnores.size() == 0)
                new OptionTables().readAll();
            if (Collections.binarySearch(packageIgnores, sbnAppFullName) >= 0)
                return true;
            int pDx = Collections.binarySearch(pkgFullNames, sbnAppFullName);
            if (pDx >= 0) {
                sbnPackageNick = pkgNickNames.get(pDx);
                sbnPackageType = pkgTypes.get(pDx);
            } else {
                sbnPackageNick = "None";
                sbnPackageType = "None";
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