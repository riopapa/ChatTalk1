package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.NotificationListener.notificationBar;
import static com.urrecliner.chattalk.SubFunc.logUpdate;
import static com.urrecliner.chattalk.SubFunc.sounds;

class MsgNamoo {

    void say(String text) {
        final String[] excludes = {"근접후", "큰 폭", "재실행 하시려면"};
        for (String s : excludes) {
            if (text.contains(s))
                return;
        }
        if (text.contains("체결")) {
            String sText = "나무App : " + text;
            // [나무] 매도 전량체결 기산텔레콤(035460) 100주 3,370원 주문No.125378
            //   0     1    2        3            4     5
            String[] words = text.split(" ");
            if (words.length < 6) {
                logUpdate.addStock("[NH나무App 에러]" + words.length, text);
                sounds.speakAfterBeep("체결 메시지 에러 " + text);
            } else {
                String[] joins = new String[]{ words[1], words[3], ";", words[4], words[5],
                        words[1], words[1]};
                sText = String.join(" ", joins);
                sounds.speakAfterBeep(sText);
            }
            notificationBar.update(words[3]+"."+words[1], sText, true);
            logUpdate.addStock("[NH나무] "+words[3], sText);
        } else {
            logUpdate.addStock("[NH나무App]", text);
            sounds.speakAfterBeep("나무 증권 " + text);
        }
    }
}