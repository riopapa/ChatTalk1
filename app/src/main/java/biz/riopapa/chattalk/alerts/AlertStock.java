package biz.riopapa.chattalk.alerts;

import static android.content.Context.MODE_PRIVATE;
import static biz.riopapa.chattalk.NotificationListener.logUpdate;
import static biz.riopapa.chattalk.NotificationListener.phoneVibrate;
import static biz.riopapa.chattalk.NotificationListener.sounds;
import static biz.riopapa.chattalk.NotificationListener.stockName;
import static biz.riopapa.chattalk.NotificationListener.utils;
import static biz.riopapa.chattalk.Vars.alertLines;
import static biz.riopapa.chattalk.Vars.mActivity;
import static biz.riopapa.chattalk.Vars.mContext;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.util.Log;

import biz.riopapa.chattalk.Sub.FileIO;
import biz.riopapa.chattalk.NotificationBar;
import biz.riopapa.chattalk.NotifyStock;
import biz.riopapa.chattalk.Sounds;
import biz.riopapa.chattalk.Sub.Copy2Clipboard;
import biz.riopapa.chattalk.Sub.PhoneVibrate;
import biz.riopapa.chattalk.Utils;
import biz.riopapa.chattalk.Vars;
import biz.riopapa.chattalk.model.AlertLine;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AlertStock {
    public void sayNlog(String iGroup, String iText, int aIdx) {

        String sTalk, who;

        if (utils == null)
            utils = new Utils();
        if (sounds == null)
            sounds = new Sounds();

        AlertLine al = alertLines.get(aIdx);
        al.matched++;
        alertLines.set(aIdx, al);
        String k1 = al.key1, k2 = al.key2;
        who = al.who;
        sTalk = al.talk;
        String percent = (!iText.contains("매수") && (iText.contains("매도") || iText.contains("익절")))? "1.9" :sTalk;
        if (stockName == null)
            stockName = new StockName();
        String [] sParse = stockName.get(al.prev, al.next, iText);
        sParse[1] = utils.strShorten(iGroup, utils.removeSpecialChars(sParse[1]));
        String key12 = " {" + k1 + "." + k2 + "}";

        if (!sTalk.isEmpty()) {
            String [] joins;
            String won = "";
            if (iGroup.equals("텔단타") || iGroup.equals("텔데봇")) {
                // 매수가 가 있으면 금액 말하기
                String [] ss = sParse[1].split("매수가");
                if (ss.length > 0) {
                    int p = ss[1].indexOf("원");
                    won = (p > 0) ? ss[1].substring(2,p) :ss[1].substring(0,7);
                }
                joins = new String[]{iGroup, who, sParse[0], sTalk, won};
            } else {
                joins = new String[]{iGroup, who, sParse[0], sTalk, sParse[0]};
            }
            sounds.speakBuyStock(String.join(" , ", joins));
            String netStr = won + " " + ((sParse[1].length() > 50) ? sParse[1].substring(0, 50) : sParse[1]);
            Log.w(iGroup, netStr);
            String title = sParse[0]+" / " + who;
            NotificationBar.update(title, netStr, true);
            logUpdate.addStock(sParse[0] + " ["+iGroup+":"+who+"]", sParse[1]+key12);
            new Copy2Clipboard(sParse[0]);
            if (isSilentNow()) {
                if (phoneVibrate == null)
                    phoneVibrate = new PhoneVibrate();
                phoneVibrate.vib(1);
            }
            new AlertToast().show(mContext, mActivity, title);
            new NotifyStock().send(mContext, title, sParse[0], netStr);
        } else {
            String title = sParse[0]+" | "+iGroup+". "+who;
            logUpdate.addStock(title, sParse[1] + key12);
            if (!isSilentNow()) {
                sounds.beepOnce(Vars.soundType.ONLY.ordinal());
            }
            String shortParse1 = (sParse[1].length() > 50) ? sParse[1].substring(0, 50) : sParse[1];
            NotificationBar.update(title, shortParse1, false);
        }
        save(al, mContext);

        String timeStamp = new SimpleDateFormat("yy-MM-dd HH:mm", Locale.KOREA).format(new Date());
        FileIO.uploadStock(iGroup, who, percent, sParse[0], sParse[1], key12+sTalk, timeStamp);

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


}