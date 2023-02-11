package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.SubFunc.logQueUpdate;
import static com.urrecliner.chattalk.SubFunc.sounds;

class MsgAndroid {

    void say(String appFullName, String who, String text) {
        text = " Android [" + who + "] " + text;
        logQueUpdate.add(appFullName, text);
        sounds.speakAfterBeep(text);
    }
}