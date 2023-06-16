package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.ActivityMain.subFunc;

class MsgAndroid {

    void say(String appFullName, String who, String text) {
        text = " Android [" + who + "] " + text;
        subFunc.logUpdate.addQue(appFullName, text);
        subFunc.sounds.speakAfterBeep(text);
    }
}