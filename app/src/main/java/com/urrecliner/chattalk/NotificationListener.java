package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.aGroups;
import static com.urrecliner.chattalk.Vars.appTxtIgnores;
import static com.urrecliner.chattalk.Vars.ktGroupIgnores;
import static com.urrecliner.chattalk.Vars.ktNoNumbers;
import static com.urrecliner.chattalk.Vars.ktTxtIgnores;
import static com.urrecliner.chattalk.Vars.ktWhoIgnores;
import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.Vars.sbnApp;
import static com.urrecliner.chattalk.Vars.sbnAppName;
import static com.urrecliner.chattalk.Vars.sbnAppNick;
import static com.urrecliner.chattalk.Vars.sbnAppType;
import static com.urrecliner.chattalk.Vars.sbnGroup;
import static com.urrecliner.chattalk.Vars.sbnText;
import static com.urrecliner.chattalk.Vars.sbnWho;
import static com.urrecliner.chattalk.Vars.smsTxtIgnores;
import static com.urrecliner.chattalk.Vars.smsWhoIgnores;
import static com.urrecliner.chattalk.Vars.teleChannels;
import static com.urrecliner.chattalk.Vars.teleGroups;
import static com.urrecliner.chattalk.Vars.tossTxtIgnores;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.urrecliner.chattalk.Sub.Copy2Clipboard;
import com.urrecliner.chattalk.Sub.IgnoreNumber;
import com.urrecliner.chattalk.Sub.IgnoreThis;
import com.urrecliner.chattalk.Sub.KeyVal;
import com.urrecliner.chattalk.Sub.Numbers;
import com.urrecliner.chattalk.Sub.PhoneVibrate;
import com.urrecliner.chattalk.alerts.StockName;

import java.util.Collections;

public class NotificationListener extends NotificationListenerService {
    final String SMS = "sms";
    final String KATALK = "kk";
//    final String TOSS = "tos";

    final String TELEGRAM = "tG";
    final String APP = "app";   // general application

    final String [] ignoreTelegram = { "항셍", "계약수량", "진입신호"};
    final String [] ignoreTesla = { "연결 중", "연결 해제됨", "핸드폰을 키로"};

    static long tesla_time = 0;

    public static Utils utils = null;

    public static KeyVal kvCommon = null;
    public static KeyVal kvKakao = null;
    public static KeyVal kvSMS = null;
    public static KeyVal kvTelegram = null;
    public static KeyVal kvStock = null;
    public static Vars vars;
    public static LoadFunction loadFunction = null;
    public static MsgKeyword msgKeyword = null;
    public static MsgSMS msgSMS = null;
    public static SbnBundle sbnBundle;
    public static NotificationBar notificationBar;
    public static NotificationService notificationService;
    public static StockName stockName;
    public static PhoneVibrate phoneVibrate;
    public static VibratorManager vibManager;
    public static Vibrator vibrator = null;
    public static VibrationEffect vibEffect = null;
//    public static final long[] vibPattern = {0, 20, 200, 300, 300, 400, 400, 500, 550, 10, 20, 200, 300, 300};
    public static final long[] vibPattern = {0, 20, 200, 300, 300, 400};

    public static Sounds sounds;
    public static LogUpdate logUpdate;
    public static AlertStock alertStock;
    String head = "";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        mContext = this.getApplicationContext();

        if (vars == null)
            vars = new Vars(this);
        if (loadFunction == null)
            loadFunction = new LoadFunction();
        if (utils == null)
            utils = new Utils();
        if (sbnBundle == null)
            sbnBundle = new SbnBundle();
        if (notificationBar == null)
            notificationBar  = new NotificationBar();
        if (notificationService == null)
            notificationService  = new NotificationService();
        if (kvCommon == null)
            kvCommon = new KeyVal();

        if (sbnBundle.bypassSbn(sbn))
            return;
        switch (sbnAppType) {

            case KATALK:

                if (IgnoreThis.contains(sbnText, ktTxtIgnores))
                    return;
                if (sbnGroup.isEmpty()) {  // no groupNames
                    if (sbnWho.isEmpty())  // nothing
                        return;
                    if (IgnoreThis.contains(sbnWho, ktWhoIgnores))
                        return;
                    sbnText = utils.text2OneLine(sbnText);
                    if (kvKakao.isDup(sbnGroup, sbnText))
                        return;
                    sbnText = utils.strShorten(sbnWho, sbnText);
                    NotificationBar.update("카톡!"+sbnWho, sbnText, true);
                    head = "{카톡!"+ sbnWho + "} ";
                    logUpdate.addLog( head, sbnText);
                    if (IgnoreNumber.in(ktNoNumbers, sbnWho))
                        sbnText = new Numbers().deduct(sbnText);
                    sounds.speakKakao(" 카톡 왔음 " + sbnWho + " 님이 " + utils.replaceKKHH(utils.makeEtc(sbnText, 150)));
                } else {    // with group name
                    if (IgnoreThis.contains(sbnGroup, ktGroupIgnores))
                        return;
                    else if (!sbnWho.isEmpty() && IgnoreThis.contains(sbnWho, ktWhoIgnores))
                        return;
                    sbnText = utils.text2OneLine(sbnText);
                    if (kvKakao.isDup(sbnGroup, sbnText))
                        return;
                    if (msgKeyword == null)
                        msgKeyword = new MsgKeyword("by ka");

                    int grpIdx = Collections.binarySearch(aGroups, sbnGroup);
                    Log.w("grpIdx check "+grpIdx,"grpIdx="+grpIdx+" group="+sbnGroup+" who="+sbnWho);
                    if (grpIdx >= 0) {
                        msgKeyword.say(sbnGroup, sbnWho, sbnText, grpIdx);
                        return;
                    }
                    sbnText = utils.strShorten(sbnGroup, sbnText);
                    NotificationBar.update("카톡!"+sbnGroup+"."+sbnWho, sbnText, true);
                    head = "{카톡!" +sbnGroup + "."+ sbnWho + "} ";
                    logUpdate.addLog( head, sbnText);
                    if (IgnoreNumber.in(ktNoNumbers, sbnGroup))
                        sbnText = new Numbers().deduct(sbnText);
                    sounds.speakKakao(" 카톡 왔음 " + sbnGroup +" 의 " + sbnWho + " 님이 " + utils.replaceKKHH(utils.makeEtc(sbnText, 150)));
                }
                break;

            case TELEGRAM:

                if (sbnGroup.contains("곳에서 보") || sbnText.contains("곳에서 보")) {
                    Log.w("Tele", "곳에서 보 "+ sbnGroup+" . "+sbnText);
                    return;
                }
                if (kvTelegram.isDup(sbnGroup, sbnText))
                    return;
                sbnText = utils.text2OneLine(sbnText);
                for (String s: ignoreTelegram) {
                    if (sbnText.contains(s))
                        return;
                }
                for (int i = 0; i < teleChannels.length; i++) {
                    if (sbnWho.contains(teleChannels[i])) { // 정확한 이름 다 찾지 않을려고 contains 씀
                        sbnGroup = teleGroups[i];
                        if (sbnText.length() < 15)
                            return;
                        if (kvTelegram.isDup(sbnGroup, sbnText))
                            return;
                        if (sbnWho.contains(":"))   // group : who 로 구성됨
                            sbnWho = sbnWho.substring(sbnWho.indexOf(":")+2).trim();
                        if (kvTelegram.isDup(sbnWho, sbnText))
                            return;
                        if (msgKeyword == null)
                            msgKeyword = new MsgKeyword("by tele");
                        int grpIdx = Collections.binarySearch(aGroups, sbnGroup);
                        if (grpIdx < 0)
                            utils.logE("tele","grpIdx "+grpIdx+" err "+sbnWho +" > "+sbnGroup
                                    +" "+sbnText);
                        if (sbnText.contains("종목")) {
                            utils.logW("tel " + sbnGroup, sbnWho + "_ : " + sbnText);
                            msgKeyword.say(sbnGroup, sbnWho, sbnText, grpIdx);
                        }
                        return;
                    }
                }
                head = "[텔레 "+ sbnGroup + "|" + sbnWho + "]";
                logUpdate.addLog(head, sbnText);
                NotificationBar.update(sbnGroup + "|" + sbnWho, sbnText, true);
                sbnText = head + " 로 부터. " + sbnText;
                sounds.speakAfterBeep(utils.makeEtc(sbnText, 200));
                break;

//            case TOSS:
//
//                for (String s: tossTxtIgnores) {
//                    if (sbnWho.contains(s) || sbnText.contains(s))
//                        return;
//                }
//                if (kvCommon.isDup(sbnAppType, sbnText))
//                    return;
//
//                sbnText = utils.strShorten(sbnAppNick, utils.text2OneLine(sbnWho+"|"+ sbnText));
//                head = "[" + sbnAppNick + "]";
//                logUpdate.addLog(head, sbnText);
//                NotificationBar.update(sbnAppNick, sbnText, true);
//                sbnText = "토스 로부터 " + new Numbers().deduct(sbnText);
//                sounds.speakAfterBeep(utils.makeEtc(sbnText, 200));
//                break;

            case SMS:

                if (sbnWho.replaceAll(mContext.getString(R.string.regex_number_only), "").length() < 4 &&
                        !sbnText.contains("스마트폰 배우고"))
                    return;
                if (IgnoreThis.contains(sbnWho, smsWhoIgnores) || IgnoreThis.contains(sbnText, smsTxtIgnores))
                    return;
                sbnText = utils.text2OneLine(sbnText);
                if (kvSMS.isDup(sbnWho, sbnText))
                    return;
                if (msgSMS == null)
                    msgSMS = new MsgSMS();
                msgSMS.say(sbnWho, utils.strShorten(sbnWho, sbnText));
                break;

            case APP:

                if (IgnoreThis.contains(sbnText, appTxtIgnores))
                    break;
                if (kvCommon.isDup(sbnApp.nickName, sbnText))
                    break;
                sbnText = utils.text2OneLine(sbnText);
                if (sbnApp.ignores.length > 0) {
                    for (int i = 0; i < sbnApp.ignores.length; i++) {
                        if (sbnText.contains(sbnApp.ignores[i]))
                            return;
                    }
                }
                if (sbnApp.inform != null) {
                    for (int i = 0; i < sbnApp.inform.length; i++) {
                        if ((sbnWho).contains(sbnApp.inform[i])) {
                            sbnWho = sbnAppNick;
                            sbnText = sbnApp.talk[i];
                            break;
                        }
                        if (sbnText.contains(sbnApp.inform[i])) {
                            sbnWho = sbnAppNick;
                            sbnText = sbnApp.talk[i];
                            break;
                        }
                    }
                }
//                if (sbnApp.nickName.equals("NH나무")) {
////                    utils.logW(sbnApp.nickName,sbnText);
////                    new MsgNamoo().say(utils.text2OneLine(sbnText));
//                    break;
//                }

                if (sbnAppNick.equals("테스리")) {
                    sayTesla();
                    break;
                }

                sbnText = utils.strShorten(sbnApp.nickName, utils.strShorten(sbnWho, sbnText));

                if (sbnApp.addWho)
                    sbnText = "("+sbnWho + ")" + sbnText;

                if (sbnApp.say) {
                    String say = sbnApp.nickName + " ";
                    say += (sbnApp.grp) ? sbnGroup+" ": " ";
                    say += (sbnApp.who) ? sbnWho:" ";
                    say = say + " 로부터 ";
                    say = say + ((sbnApp.num) ? sbnText : new Numbers().deduct(sbnText));
                    sounds.speakAfterBeep(utils.makeEtc(say, 200));
                }

                if (sbnApp.log) {
                    head = "[" + sbnApp.nickName;
                    head += (sbnApp.grp) ? "."+sbnGroup+"_": "";
                    head += (sbnApp.who) ? sbnWho:"";
                    head = head + "]";
                    logUpdate.addLog(head, sbnText);
                }
                String s = (sbnApp.grp) ? sbnGroup+"_": "";
                s += (sbnApp.who) ? sbnWho:"";
                NotificationBar.update(sbnApp.nickName + ":"+ s, sbnText, true);
                break;

            default:

                if (kvCommon.isDup("none", sbnText))
                    return;
                sbnText = utils.text2OneLine(sbnText);
                sounds.speakAfterBeep("새 앱 설치됨 " + sbnText);
                sbnText = "새로운 앱이 설치됨,  group : " + sbnGroup + ", who : " + sbnWho +
                        ", text : " + sbnText;
                NotificationBar.update(sbnAppName, sbnText, true);
                new Copy2Clipboard(sbnAppName);
                logUpdate.addLog("[ " + sbnAppName + " ]", sbnText);
                break;
        }
    }

    private void sayTesla() {
        for (String s: ignoreTesla) {
            if (sbnText.contains(s))
                return;
        }
        if (sbnText.contains("연결됨")) {
            long nowTime = System.currentTimeMillis();
            if ((nowTime - tesla_time) > 50 * 60 * 1000)    // 50 min.
                sounds.beepOnce(Vars.soundType.HI_TESLA.ordinal());
            tesla_time = nowTime;
            return;
        }
        if (kvCommon.isDup("테스리", sbnText))
            return;
        logUpdate.addLog("[ 테스리 ]", sbnText);
        NotificationBar.update(sbnAppNick, sbnText, true);
        sounds.speakAfterBeep("테스리로 부터 " + sbnText);
    }
}