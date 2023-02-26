package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.SubFunc.logQueUpdate;
import static com.urrecliner.chattalk.SubFunc.sounds;
import static com.urrecliner.chattalk.Vars.lastChar;
import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.Vars.nineIgnores;
import static com.urrecliner.chattalk.MainActivity.utils;

import com.urrecliner.chattalk.Sub.IsWhoNine;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class MsgSMS {

    final static String trade = "체결";
    final static String jrGroup = "찌라";
    final static String nhStock = "NH투자증권";

    static MsgKaTalk msgKaTalk = null;
    void say(String mWho, String mText) {

        if (utils == null)
            utils = new Utils();

        mWho = mWho.replaceAll("[\\u200C-\\u206F]", "");
        mText = mText.replace(mContext.getString(R.string.web_sent), "").replaceAll("[\\u200C-\\u206F]", "");
//        manageLogQue.add("MsgSMS", "who="+mWho+", txt="+mText);
        if (mWho.startsWith(jrGroup)) {
            if (msgKaTalk == null)
                msgKaTalk = new MsgKaTalk();
            msgKaTalk.say(jrGroup, mWho, mText);
        } else if (mWho.equals(nhStock)) {
            // |[NH투자]|매수 전량체결|KMH    |10주|9,870원|주문 0001026052
            //   0       1    2      3       4    5
            if (mText.contains(trade))
                sayTrade(mWho, mText);
            else {
                sayNormal(mWho, mText);
            }
        } else {
            String head = "[sms "+mWho + "]";
            mText = utils.strReplace(mWho, mText);
            logQueUpdate.add(head, mText);
            NotificationBar.update("sms "+mWho, mText);
            if (IsWhoNine.in(nineIgnores, mWho))
                mText = mText.replaceAll("[0-9]", "");
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
                    logQueUpdate.add("SMS NH증권 에러 " + words.length + ".txt", mText);
                    sounds.speakAfterBeep(mText);
                } else {
                    String stockName = words[3].trim();  // 종목명
                    String buySell = (words[2].contains("매수")) ? "매수" : "매도";
                    String amount = words[4];
                    String uPrice = words[5];
                    String sGroup = lastChar + trade;
                    String sayMsg = (buySell.equals("매수") ? stockName + " "
                            + amount + " " + uPrice + "으로 샀음 " :
                            stockName + " " + amount + " " + uPrice + " 에 팔렸음 ");
                    NotificationBar.update(trade +":"+buySell, sayMsg);
                    logQueUpdate.add("sms>"+nhStock, sayMsg);
                    String timeStamp = new SimpleDateFormat("yy-MM-dd HH:mm", Locale.KOREA).format(new Date());
                    FileIO.uploadStock(sGroup, mWho, buySell, stockName, mText, amount, timeStamp);
                    sounds.speakAfterBeep(sayMsg.replaceAll("[0-9]",""));
                }
            } catch (Exception e) {
                mText = "Parsing Exception_01 " + mText;
                logQueUpdate.add(nhStock, mText);
                sounds.speakAfterBeep(mText);
            }
        } else
            sayNormal(mWho, mText);
    }

    private void sayNormal(String mWho, String mText) {
        String head = "[sms."+ mWho + "] ";
        mText = utils.strReplace("sms", mText);
        NotificationBar.update(head, mText);
        logQueUpdate.add(head, mText);
        sounds.speakAfterBeep(head + utils.makeEtc(mText, 160));
    }
}