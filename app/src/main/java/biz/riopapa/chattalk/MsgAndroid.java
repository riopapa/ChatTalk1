package biz.riopapa.chattalk;

import static biz.riopapa.chattalk.NotificationListener.logUpdate;
import static biz.riopapa.chattalk.NotificationListener.sounds;

class MsgAndroid {

    void say(String appFullName, String who, String text) {
        text = " Android [" + who + "] " + text;
        logUpdate.addLog(appFullName, text);
        sounds.speakAfterBeep(text);
    }
}