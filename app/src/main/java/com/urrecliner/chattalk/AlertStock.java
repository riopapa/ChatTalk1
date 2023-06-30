package com.urrecliner.chattalk;

import static android.content.Context.CLIPBOARD_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.urrecliner.chattalk.NotificationListener.notificationBar;
import static com.urrecliner.chattalk.NotificationListener.phoneVibrate;
import static com.urrecliner.chattalk.NotificationListener.stockName;
import static com.urrecliner.chattalk.NotificationListener.subFunc;
import static com.urrecliner.chattalk.NotificationListener.utils;
import static com.urrecliner.chattalk.Vars.alertLines;
import static com.urrecliner.chattalk.Vars.mContext;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;

import com.urrecliner.chattalk.Sub.AlertLine;
import com.urrecliner.chattalk.Sub.AlertToast;
import com.urrecliner.chattalk.Sub.Dot;
import com.urrecliner.chattalk.Sub.PhoneVibrate;
import com.urrecliner.chattalk.Sub.StockName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AlertStock {
    void sayNlog(String iGroup, String iText, int aIdx) {

        String key12, sTalk, group, who;
        if (utils == null)
            utils = new Utils();
        AlertLine al = alertLines.get(aIdx);
        al.matched++;
        alertLines.set(aIdx, al);
        String k1 = al.key1, k2 = al.key2;
        group = al.group;
        who = al.who;
        sTalk = al.talk;
        String percent = (iText.contains("매도") || iText.contains("익절"))? "1.9" :sTalk;
        key12 = " {" + k1 + "." + k2 + "}";
        if (stockName == null)
            stockName = new StockName();
        String stock_Name = stockName.parse(al.prev, al.next, iText);
        String sText = utils.strReplace(iGroup, iText);
        String head = " [" + group + "." + who + "] "+stock_Name;
        String keyStr = key12+sTalk;
        Thread thisThread = new Thread(() -> {
            if (sTalk.length() > 0) {
                subFunc.sounds.beepOnce(Vars.soundType.STOCK.ordinal());
//                String cho = new Hangul().getCho(stock_Name);
//                if (cho.length() > 4)
//                    cho = cho.substring(0,4);
//                String[] joins = new String[]{who, group, who, stock_Name, sTalk, cho, stock_Name, sText};
                String[] joins = new String[]{group, who, stock_Name, sTalk, stock_Name, sText};
                subFunc.sounds.speakBuyStock(String.join(" , ", joins)); //.replaceAll("\\d","", )
                if (isSilentNow()) {
                    if (phoneVibrate == null)
                        phoneVibrate = new PhoneVibrate();
                    phoneVibrate.vib();
                }
                String title = "["+stock_Name+"/"+who+"]";
                String text = group + " : " + who+" > "+sText;
                notificationBar.update( title, text, true);
                new ShowMessage().send(mContext, title,text);
                new AlertToast().show(mContext, title);
                copyToClipBoard(stock_Name);
            } else {
                notificationBar.update(stock_Name, who+" : "+sText, false);
                subFunc.sounds.beepOnce(Vars.soundType.ONLY.ordinal());
            }
            save(al, mContext);
            String timeStamp = new SimpleDateFormat("yy-MM-dd HH:mm", Locale.KOREA).format(new Date());
            String dotText = sText.replace(stock_Name, new Dot().add(stock_Name));
            FileIO.uploadStock(group, who, percent, stock_Name, dotText, keyStr, timeStamp);
            subFunc.logUpdate.addStock(head, sText + keyStr);
        });
        thisThread.start();
    }

    private void save(AlertLine al, Context context) {

        SharedPreferences sharePref = context.getSharedPreferences("alertLine", MODE_PRIVATE);
        SharedPreferences.Editor sharedEditor = sharePref.edit();
        String[] joins = new String[]{"matched", al.group, al.who, al.key1, al.key2 };
        String keyVal = String.join("~~", joins);
        sharedEditor.putInt(keyVal, al.matched);
        sharedEditor.apply();
    }
    boolean isSilentNow() {
        AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        return (mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT ||
                mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE);
    }

    void copyToClipBoard(String s) {
        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("stock", s);
        clipboard.setPrimaryClip(clip);
    }

}