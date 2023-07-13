package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.NotificationListener.msgKaTalk;
import static com.urrecliner.chattalk.NotificationListener.notificationBar;
import static com.urrecliner.chattalk.NotificationListener.sounds;
import static com.urrecliner.chattalk.NotificationListener.subFunc;
import static com.urrecliner.chattalk.NotificationListener.utils;
import static com.urrecliner.chattalk.Vars.lastChar;
import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.Vars.nineIgnores;

import com.urrecliner.chattalk.Sub.Dot;
import com.urrecliner.chattalk.Sub.IsWhoNine;
import com.urrecliner.chattalk.Sub.Numbers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class MsgSMS {

    final static String trade = "체결";
    final static String jrGroup = "찌라";
    final static String nhStock = "NH투자";
    void say(String mWho, String mText) {

        mWho = mWho.replaceAll("[\\u200C-\\u206F]", "");
        mText = mText.replace(mContext.getString(R.string.web_sent), "").replaceAll("[\\u200C-\\u206F]", "");
        if (mWho.startsWith(jrGroup)) {
            if (msgKaTalk == null)
                msgKaTalk = new MsgKaTalk();
            msgKaTalk.say(jrGroup, mWho, mText);
        } else if (mWho.contains(nhStock)) {
            // |[NH투자]|매수 전량체결|KMH    |10주|9,870원|주문 0001026052
            //   0       1    2      3       4    5
            if (mText.contains(trade))
                sayTrade(mWho, mText);
            else {
                sayNormal(mWho, mText);
            }
        } else {
            String head = "[sms "+mWho + "]";
            if (utils == null)
                utils = new Utils();
            mText = utils.strReplace(mWho, mText);
            subFunc.logUpdate.addQue(head, mText);
            notificationBar.update("sms "+mWho, mText, true);
            if (IsWhoNine.in(nineIgnores, mWho))
                mText = new Numbers().out(mText);
            sounds.speakAfterBeep(head+" 으로부터 "+ utils.makeEtc(mText, 160));
        }
    }

    private void sayTrade(String mWho, String mText) {
        int pos = mText.indexOf("주문");
        if (pos > 0) {
            mText = mText.substring(0, pos);
            try {
                String[] words = mText.split("\\|");
                if (words.length < 5) {
                    subFunc.logUpdate.addStock("SMS NH 증권 에러 " + words.length, mText);
                    sounds.speakAfterBeep(mText);
                } else {
                    String stockName = words[3].trim();  // 종목명
                    boolean buySell = words[2].contains("매수");
                    String samPam = (buySell) ?  " 샀음": " 팔림";
                    String amount = words[4];
                    String uPrice = words[5];
                    String sGroup = lastChar + trade;
                    String sayMsg = stockName + " " + amount + " " + uPrice + samPam;
                    notificationBar.update(samPam +":"+stockName, sayMsg, true);
                    subFunc.logUpdate.addStock("sms>"+nhStock, sayMsg);
                    FileIO.uploadStock(sGroup, mWho, samPam, stockName,
                            mText.replace(stockName, new Dot().add(stockName)), samPam,
                            new SimpleDateFormat("yy-MM-dd HH:mm", Locale.KOREA).format(new Date()));
                    sayMsg = stockName + samPam;
                    sounds.speakAfterBeep(new Numbers().out(sayMsg));
                }
            } catch (Exception e) {
                subFunc.logUpdate.addStock(nhStock, "Exception " + mText + e);
//                sounds.speakAfterBeep(mText);
            }
        } else
            sayNormal(mWho, mText);
    }

    private void sayNormal(String mWho, String mText) {
        String head = "[sms."+ mWho + "] ";
        mText = utils.strReplace("sms", mText);
        notificationBar.update(head, mText, true);
        subFunc.logUpdate.addQue(head, mText);
        if (utils == null)
            utils = new Utils();
        if (IsWhoNine.in(nineIgnores, mWho))
            mText = new Numbers().out(mText);
        sounds.speakAfterBeep(head + utils.makeEtc(mText, 160));
    }
}