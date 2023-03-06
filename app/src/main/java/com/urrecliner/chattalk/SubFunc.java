package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.Vars.toDay;

public class SubFunc {
    static LogQueUpdate logQueUpdate;
    static Sounds sounds;
    static AlertStock alertStock;

    public SubFunc() {

        if (logQueUpdate == null)
            logQueUpdate = new LogQueUpdate(mContext);
        if (toDay == null || toDay.equals("ToDay"))
            logQueUpdate.readyTodayFolderIfNewDay();

        if (sounds == null) {
            sounds = new Sounds();
            sounds.init();
        }
        if (alertStock == null)
            alertStock = new AlertStock();

        Upload2Google.initSheetQue();
    }
}