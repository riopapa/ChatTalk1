package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.NotificationListener.logUpdate;
import static com.urrecliner.chattalk.NotificationListener.sounds;

class MsgAndroid {

    void say(String appFullName, String who, String text) {
        text = " Android [" + who + "] " + text;
        logUpdate.addQue(appFullName, text);
        sounds.speakAfterBeep(text);
    }
}