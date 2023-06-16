package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.Vars.toDay;

public class SubFunc {
    public LogUpdate logUpdate;
    public Sounds sounds;
    public AlertStock alertStock;

    public SubFunc() {

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