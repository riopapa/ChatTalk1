package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.NotificationListener.logUpdate;
import static com.urrecliner.chattalk.NotificationListener.msgKaTalk;
import static com.urrecliner.chattalk.NotificationListener.notificationBar;
import static com.urrecliner.chattalk.NotificationListener.sounds;
import static com.urrecliner.chattalk.NotificationListener.utils;
import static com.urrecliner.chattalk.Vars.lastChar;
import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.Vars.nineIgnores;

import com.urrecliner.chattalk.Sub.IsWhoNine;
import com.urrecliner.chattalk.Sub.Numbers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class MsgSMS {

    final static String trade = "체결";
    final static String jrGroup = "허찌";
    final static String nhStock = "NH투자";
    void say(String mWho, String mText) {

        mWho = mWho.replaceAll("[\\u200C-\\u206F]", "");
        mText = mText.replace(mContext.getString(R.string.web_sent), "").replaceAll("[\\u200C-\\u206F]", "");
        if (mWho.contains(nhStock)) {
            if (mText.contains(trade))
                sayTrade(mWho, mText);
            else {
                sayNormal(mWho, mText);
            }
        } else if (mWho.startsWith(jrGroup)) {
                if (msgKaTalk == null)
                    msgKaTalk = new MsgKaTalk();
                msgKaTalk.say(jrGroup, mWho, mText);
        } else {
            String head = "[SMS "+mWho + "]";
            if (utils == null)
                utils = new Utils();
            mText = utils.strShorten(mWho, mText);
            logUpdate.addLog(head, mText);
            mText = utils.makeEtc(mText, 150);
            notificationBar.update("sms "+mWho, mText, true);
            if (IsWhoNine.in(nineIgnores, mWho))
                mText = new Numbers().deduct(mText);
            sounds.speakAfterBeep(head+" 으로부터 "+ mText);
        }
    }

    private void sayTrade(String mWho, String mText) {
        int pos = mText.indexOf("주문");
        if (pos > 0) {
            mText = mText.substring(0, pos);
            try {
                String[] words = mText.split("\\|");
                // |[NH투자]|매수 전량체결|KMH    |10주|9,870원|주문 0001026052
                //   0       1          2       3    4       5
                if (words.length < 5) {
                    logUpdate.addStock("SMS NH 증권 에러 " + words.length, mText);
                    sounds.speakAfterBeep(mText);
                } else {
                    String stockName = words[2].trim();  // 종목명
                    boolean buySell = words[1].contains("매수");
                    String samPam = (buySell) ?  " 샀음": " 팔림";
                    String amount = words[3];
                    String uPrice = words[4];
                    String sGroup = lastChar + trade;
                    String sayMsg = stockName + " " + amount + " " + uPrice + samPam;
                    notificationBar.update(samPam +":"+stockName, sayMsg, true);
                    logUpdate.addStock("sms>"+nhStock, sayMsg);
                    FileIO.uploadStock(sGroup, mWho, samPam, stockName,
                            mText.replace(stockName, new StringBuffer(stockName).insert(1, ".").toString()), samPam,
                            new SimpleDateFormat("yy-MM-dd HH:mm", Locale.KOREA).format(new Date()));
                    sayMsg = stockName + samPam;
                    sounds.speakAfterBeep(new Numbers().deduct(sayMsg));
                }
            } catch (Exception e) {
                logUpdate.addStock(nhStock, "Exception " + mText + e);
//                sounds.speakAfterBeep(mText);
            }
        } else
            sayNormal(mWho, mText);
    }

    private void sayNormal(String mWho, String mText) {
        String head = "[sms."+ mWho + "] ";
        mText = utils.strShorten("sms", mText);
        notificationBar.update(head, mText, true);
        logUpdate.addLog(head, mText);
        if (utils == null)
            utils = new Utils();
        if (IsWhoNine.in(nineIgnores, mWho))
            mText = new Numbers().deduct(mText);
        sounds.speakAfterBeep(head + utils.makeEtc(mText, 120));
    }
}