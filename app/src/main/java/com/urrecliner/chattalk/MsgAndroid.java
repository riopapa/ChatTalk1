package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.SubFunc.logUpdate;
import static com.urrecliner.chattalk.SubFunc.sounds;

class MsgAndroid {

    void say(String appFullName, String who, String text) {
        text = " Android [" + who + "] " + text;
        logUpdate.addQue(appFullName, text);
        sounds.speakAfterBeep(text);
    }
}