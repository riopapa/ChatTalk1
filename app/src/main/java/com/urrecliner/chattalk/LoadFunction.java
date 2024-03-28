package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.NotificationListener.alertStock;
import static com.urrecliner.chattalk.NotificationListener.kvCommon;
import static com.urrecliner.chattalk.NotificationListener.logUpdate;
import static com.urrecliner.chattalk.NotificationListener.notificationService;
import static com.urrecliner.chattalk.NotificationListener.utils;
import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.Vars.toDay;
import static com.urrecliner.chattalk.NotificationListener.sounds;

import com.urrecliner.chattalk.Sub.KeyVal;

public class LoadFunction {

    public LoadFunction() {

        if (logUpdate == null) {
            logUpdate = new LogUpdate(mContext);
        }
        if (toDay.length() < 6) {
            new ReadyToday();
        }
        if (sounds == null) {
            sounds = new Sounds();
            sounds.init();
        }
        if (utils == null)
            utils = new Utils();
        if (notificationService == null)
            notificationService = new NotificationService();
        if (kvCommon == null)
            kvCommon = new KeyVal();
        if (sounds == null)
            sounds = new Sounds();

        Upload2Google.initSheetQue();
    }
}