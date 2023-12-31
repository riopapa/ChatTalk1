package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.NotificationListener.alertStock;
import static com.urrecliner.chattalk.NotificationListener.logUpdate;
import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.Vars.toDay;
import static com.urrecliner.chattalk.NotificationListener.sounds;

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
        if (alertStock == null)
            alertStock = new AlertStock();

        Upload2Google.initSheetQue();
    }
}