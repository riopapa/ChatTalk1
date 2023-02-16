package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.mContext;

public class SubFunc {
    public static LogQueUpdate logQueUpdate;
    static MsgAndroid msgAndroid;
    static MsgKaTalk msgKaTalk;
    static MsgSMS msgSMS;
    static Sounds sounds;
    static Utils utils;
    static SbnBundle sbnBundle;

    public SubFunc() {
        msgAndroid = new MsgAndroid();
        logQueUpdate = new LogQueUpdate(mContext);
        msgKaTalk = new MsgKaTalk();
        msgSMS = new MsgSMS();
        sounds = new Sounds();  sounds.init();
        utils = new Utils();
        utils.setTimeBoundary();
        sbnBundle = new SbnBundle();
        Upload2Google.initSheetQue();
    }
}