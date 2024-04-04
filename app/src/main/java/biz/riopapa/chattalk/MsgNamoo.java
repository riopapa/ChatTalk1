package biz.riopapa.chattalk;

import static biz.riopapa.chattalk.NotificationListener.logUpdate;
import static biz.riopapa.chattalk.NotificationListener.sounds;

import biz.riopapa.chattalk.Sub.Numbers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MsgNamoo {

    final String naMoo = "NH나무";

    void say(String text) {
        final String[] excludes = {"근접후", "큰 폭", "재실행 하시려면"};
        for (String s : excludes) {
            if (text.contains(s))
                return;
        }
        if (text.contains("체결")) {
            String sText = "";
            String stkName = "";
            /*
            // [나무] 매도 전량체결 기산텔레콤(035460) 100주 3,370원 주문No.125378
            //   0     1    2        3            4     5
             */
            String[] words = text.split(" ");
            if (words.length < 6) {
                sText = naMoo + " App 에러]" + words.length + " msg="+text;
                logUpdate.addStock(sText, text);
                sounds.speakAfterBeep("체결 메시지 에러 " + text);
            } else {
                int p = words[3].indexOf("(");
                stkName = words[3].substring(0, p);
                String[] joins = new String[]{ words[1], ".",  stkName, ";", words[4], words[5],
                        words[1]};
                sText = String.join(" ", joins);
                sounds.speakAfterBeep(sText);
            }
            NotificationBar.update(words[3]+"."+words[1], sText, true);
            logUpdate.addStock(naMoo +"."+stkName, sText);
            String timeStamp = new SimpleDateFormat("yy-MM-dd HH:mm", Locale.KOREA).format(new Date());
            FileIO.uploadStock("힝체결", naMoo, words[1], stkName, words[5]+" "+words[2], words[4], timeStamp);

        } else {
            logUpdate.addStock("[NH나무App]", text);
            sounds.speakAfterBeep("나무 증권 " + new Numbers().deduct(text));
        }
    }
}