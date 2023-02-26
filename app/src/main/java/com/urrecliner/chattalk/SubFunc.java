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
        if (sounds == null) {
            sounds = new Sounds();
            sounds.init();
        }
        if (toDay.equals("ToDay"))
            logQueUpdate.readyTodayFolderIfNewDay();
        Upload2Google.initSheetQue();
    }
}