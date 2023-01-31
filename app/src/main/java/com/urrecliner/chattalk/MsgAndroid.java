package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.logQueUpdate;
import static com.urrecliner.chattalk.Vars.sounds;

class MsgAndroid {

    void say(String appFullName, String who, String text) {
        text = " Android [" + who + "] " + text;
        logQueUpdate.add(appFullName, text);
        sounds.speakAfterBeep(text);
    }
}