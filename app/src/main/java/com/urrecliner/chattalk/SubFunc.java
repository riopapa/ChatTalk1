package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.Vars.toDay;

public class SubFunc {
    public static LogQueUpdate logQueUpdate;
    static MsgAndroid msgAndroid;
    static MsgKaTalk msgKaTalk;
    static MsgSMS msgSMS;
    static Sounds sounds;
    static Utils utils;
    static SbnBundle sbnBundle;
    static AlertStock alertStock;

    public SubFunc() {
        msgAndroid = new MsgAndroid();
        logQueUpdate = new LogQueUpdate(mContext);
        msgKaTalk = new MsgKaTalk();
        msgSMS = new MsgSMS();
        sounds = new Sounds();  sounds.init();
        utils = new Utils();
        if (toDay.equals("ToDay"))
            logQueUpdate.readyTodayFolderIfNewDay();
        sbnBundle = new SbnBundle();
        alertStock = new AlertStock();
        Upload2Google.initSheetQue();

    }
}