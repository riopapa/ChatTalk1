package biz.riopapa.chattalk;

import static biz.riopapa.chattalk.NotificationListener.alertStock;
import static biz.riopapa.chattalk.NotificationListener.kvCommon;
import static biz.riopapa.chattalk.NotificationListener.logUpdate;
import static biz.riopapa.chattalk.NotificationListener.notificationService;
import static biz.riopapa.chattalk.NotificationListener.utils;
import static biz.riopapa.chattalk.Vars.mContext;
import static biz.riopapa.chattalk.Vars.toDay;
import static biz.riopapa.chattalk.NotificationListener.sounds;

import biz.riopapa.chattalk.Sub.KeyVal;

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