package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.kGroupWhoIgnores;
import static com.urrecliner.chattalk.Vars.kkTxtIgnores;
import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.Vars.nineIgnores;
import static com.urrecliner.chattalk.Vars.sbnAppFullName;
import static com.urrecliner.chattalk.Vars.sbnGroup;
import static com.urrecliner.chattalk.Vars.sbnPackageNick;
import static com.urrecliner.chattalk.Vars.sbnPackageType;
import static com.urrecliner.chattalk.Vars.sbnText;
import static com.urrecliner.chattalk.Vars.sbnWho;
import static com.urrecliner.chattalk.Vars.smsTextIgnores;
import static com.urrecliner.chattalk.Vars.smsWhoIgnores;
import static com.urrecliner.chattalk.Vars.teleChannels;
import static com.urrecliner.chattalk.Vars.teleGroups;
import static com.urrecliner.chattalk.Vars.textIgnores;
import static com.urrecliner.chattalk.Vars.tossIgnores;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.urrecliner.chattalk.Sub.IgnoreThis;
import com.urrecliner.chattalk.Sub.IsWhoNine;
import com.urrecliner.chattalk.Sub.KeyVal;
import com.urrecliner.chattalk.Sub.Numbers;
import com.urrecliner.chattalk.Sub.PhoneVibrate;
import com.urrecliner.chattalk.Sub.StockName;

public class NotificationListener extends NotificationListenerService {
    final String SMS = "sms";
    final String KATALK = "kk";
    final String NAHMOO = "nh";
    final String TOSS = "tos";
    //                            Say  Que Log
    final String YYY = "yyy"; //   Y    Y   Y
    final String YYN = "yyn"; //   Y    Y   N
    final String YYX = "yyx"; //   Y    Y   X exclude Group
    final String YNX = "ynx"; //   Y    N   X  no Who
    final String YWX = "ywx"; //   Y    N   X  who should be in text
    final String YNN = "ynn"; //   Y    N   X  no Who, speak only
    final String TESLA = "ts";             // tesla only
    final String TELEGRAM = "tG";
    final String BAND = "bd";                    // band

    long tesla_time = 0;

    String head;

    public static Utils utils = null;

    public static KeyVal kvCommon = null;
    static KeyVal kvKakao = null;
    static KeyVal kvSMS = null;
    static KeyVal kvTelegram = null;
    static KeyVal kvStock = null;
    static Vars vars = null;
    static LoadFunction loadFunction = null;
    static MsgKaTalk msgKaTalk = null;
    static MsgSMS msgSMS = null;
    static SbnBundle sbnBundle = null;
    static NotificationBar notificationBar = null;
    static StockName stockName = null;
    public static PhoneVibrate phoneVibrate = null;
    public static VibratorManager vibManager = null;
    public static Vibrator vibrator = null;
    public static VibrationEffect vibEffect = null;
//    public static final long[] vibPattern = {0, 20, 200, 300, 300, 400, 400, 500, 550, 10, 20, 200, 300, 300};
    public static final long[] vibPattern = {0, 20, 200, 300, 300, 400};

    public static Sounds sounds;
    public static LogUpdate logUpdate;
    public static AlertStock alertStock;

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
            notificationBar = new NotificationBar();

        if (kvCommon == null)
            kvCommon = new KeyVal();

        if (sbnBundle.bypassSbn(sbn))
            return;


        switch (sbnPackageType) {

            case KATALK:

                if (IgnoreThis.contains(sbnText, kkTxtIgnores))
                    return;
                if (sbnGroup.equals("")) {  // no groupNames
                    if (sbnWho.equals(""))  // nothing
                        return;
                    if (IgnoreThis.contains(sbnWho, kGroupWhoIgnores))
                        return;
                    sbnText = utils.text2OneLine(sbnText);
                    if (kvKakao.isDup(sbnGroup, sbnText))
                        return;
                    sbnText = utils.strShorten(sbnWho, sbnText);
                    String head = "{카톡!"+ sbnWho + "} ";
                    notificationBar.update("카톡!"+sbnWho, sbnText, true);
                    logUpdate.addQue( head, sbnText);
                    if (IsWhoNine.in(nineIgnores, sbnWho))
                        sbnText = new Numbers().deduct(sbnText);
                    sounds.speakAfterBeep(" 카톡왔음 " + sbnWho + " 님이 " + utils.replaceKKHH(utils.makeEtc(sbnText, 150)));
                } else {    // with group name
                    if (IgnoreThis.contains(sbnGroup, kGroupWhoIgnores))
                        return;
                    else if (!sbnWho.equals("") && IgnoreThis.contains(sbnWho, kGroupWhoIgnores))
                        return;
                    sbnText = utils.text2OneLine(sbnText);
                    if (kvKakao.isDup(sbnGroup, sbnText))
                        return;
                    if (msgKaTalk == null)
                        msgKaTalk = new MsgKaTalk();
                    msgKaTalk.say(sbnGroup, sbnWho, sbnText);
                }
                break;

            case TELEGRAM:

                if (sbnText.contains("곳에서 보냄"))
                    return;
                sbnText = utils.text2OneLine(sbnText);
                if (kvTelegram.isDup(sbnGroup+sbnWho, sbnText))
                    return;
                for (int i = 0; i < teleChannels.length; i++) {
                    if (sbnWho.contains(teleChannels[i])) {
                        sbnGroup = teleGroups[i];
                        if (sbnWho.contains(":"))   // 부자 인 겅우 group : who 로 구성됨
                            sbnWho = sbnWho.substring(sbnWho.indexOf(":")+2).trim();
                        if (msgKaTalk == null)
                            msgKaTalk = new MsgKaTalk();
                        msgKaTalk.say(sbnGroup, sbnWho, sbnGroup+sbnText);
                        return;
                    }
                }
                head = "[텔레 "+ sbnGroup + "|" + sbnWho + "]";
                logUpdate.addQue(head, sbnText);
                notificationBar.update(sbnGroup + "|" + sbnWho, sbnText, true);
                sbnText = head + " 로 부터. " + sbnText;
                sounds.speakAfterBeep(utils.makeEtc(sbnText, 200));
                break;

            case SMS:

                if (sbnWho.replaceAll(mContext.getString(R.string.regex_number_only), "").length() < 4 &&
                    !sbnText.contains("스마트폰 배우고"))
                        return;
                if (IgnoreThis.contains(sbnWho, smsWhoIgnores) || IgnoreThis.contains(sbnText, smsTextIgnores))
                    return;
                sbnText = utils.text2OneLine(sbnText);
                if (kvSMS.isDup(sbnWho, sbnText))
                    return;
                if (msgSMS == null)
                    msgSMS = new MsgSMS();
                msgSMS.say(sbnWho, utils.strShorten(sbnWho, sbnText));
                break;

            case TOSS:

                for (String s: tossIgnores) {
                    if (sbnWho.contains(s) || sbnText.contains(s))
                        return;
                }
                head = "[" + sbnPackageNick + "]";
                sbnText = sbnWho+"🖐"+ utils.text2OneLine(sbnText);
                logUpdate.addQue(head , sbnText);
                notificationBar.update(sbnPackageNick, sbnText, true);
                sbnText = "토스 로부터 " + new Numbers().deduct(sbnText);
                sounds.speakAfterBeep(utils.makeEtc(sbnText, 200));
                break;

            case NAHMOO:

                new MsgNamoo().say(utils.text2OneLine(sbnText));
                break;

            case TESLA:

                if (kvCommon.isDup("tesla", sbnText))
                    return;
                final String [] ignoreTesla = { "연결 중", "연결 해제됨", "핸드폰을 키로"};
                for (String s: ignoreTesla) {
                    if (sbnText.contains(s))
                        return;
                }
                if (sbnText.contains("연결됨")) {
                    long nowTime = System.currentTimeMillis();
                    if ((nowTime - tesla_time) > 50 * 60 * 1000)    // 30 min.
                        sounds.beepOnce(Vars.soundType.TESRY.ordinal());
                    tesla_time = nowTime;
                    break;
                }
                if (kvCommon.isDup(sbnWho, sbnText))
                    break;
                logUpdate.addQue("[ 테스리 ]", sbnText);
                notificationBar.update(sbnPackageNick, sbnText, true);
//                FileIO.append2Today("Tesla.txt", sbnText);
                sounds.speakAfterBeep("테스리로 부터 " + sbnText);
                break;

            case YYX:     // exclude Group e.g. bank app

                if (IgnoreThis.contains(sbnText, textIgnores))
                    break;
                sbnText = utils.text2OneLine(sbnText);
                if (kvCommon.isDup(sbnWho, sbnText))
                    return;
                sbnText = utils.strShorten(sbnWho, utils.text2OneLine(sbnText));
                head = "[" + sbnPackageNick + "🖐️"+ sbnWho +"] ";
                logUpdate.addQue(head , sbnText);
                notificationBar.update(sbnPackageNick + ":"+ sbnWho, sbnText, true);
                if (IsWhoNine.in(nineIgnores, sbnPackageNick))
                    sbnText = new Numbers().deduct(sbnText);
                sbnText = sbnPackageNick + " 로부터 " + head + sbnText;
                sounds.speakAfterBeep(utils.makeEtc(sbnText, 200));
                break;

            case YNX: // no who, log Yes, say Yes

                if (IgnoreThis.contains(sbnText, textIgnores))
                    return;
                sbnText = sbnGroup + "✓" + utils.text2OneLine(sbnText);
                logUpdate.addQue("["+sbnPackageNick+"]", sbnText);
                if (IsWhoNine.in(nineIgnores, sbnPackageNick))
                    sbnText = new Numbers().deduct(sbnText);
                sounds.speakAfterBeep(sbnPackageNick + " 로 부터 " + sbnText);
                break;

            case YWX: // treat who as text

                if (IgnoreThis.contains(sbnText, textIgnores))
                    return;
                if (kvCommon.isDup(sbnWho, sbnText))
                    return;
                sbnText = sbnWho + "✓" + utils.text2OneLine(sbnText);
                logUpdate.addQue(sbnPackageNick, sbnText);
                notificationBar.update(sbnPackageNick, sbnText, true);
                if (IsWhoNine.in(nineIgnores, sbnPackageNick))
                    sbnText = new Numbers().deduct(sbnText);
                sounds.speakAfterBeep(sbnPackageNick + " 로 부터 " + sbnText);
                break;

            case YYN:   //

                if (IgnoreThis.contains(sbnText, textIgnores) || IgnoreThis.contains(sbnWho, textIgnores))
                    return;
                sbnText = utils.text2OneLine(sbnText);
                if (kvCommon.isDup(sbnWho, sbnText))
                    return;
                sbnText = utils.strShorten(sbnPackageNick, sbnText);
                head = "[" + sbnPackageNick + "🖐️"+ sbnGroup + "🖐️"+ sbnWho +"] ";
                logUpdate.addQue(head, sbnText);
                notificationBar.update(head, sbnText, true);
                if (IsWhoNine.in(nineIgnores, sbnPackageNick))
                    sbnText = new Numbers().deduct(sbnText);
                sbnText = head + " 로부터 " + sbnText;
                sounds.speakAfterBeep(utils.makeEtc(sbnText, 200));
                break;

            case YYY:

                if (IgnoreThis.contains(sbnText, textIgnores))
                    return;
                sbnText = utils.text2OneLine(sbnText);
                if (kvCommon.isDup(sbnWho, sbnText))
                    return;
                sbnText = utils.strShorten(sbnWho, sbnText);
                head = sbnPackageNick + "👍" + sbnGroup + "👍"+ sbnWho;
                logUpdate.addQue("[" + sbnPackageNick + "] "+head, sbnText);
                notificationBar.update(head, sbnText, true);
//                utils.logW(sbnPackageNick, head+sbnText);
                if (IsWhoNine.in(nineIgnores, sbnPackageNick))
                    sbnText = new Numbers().deduct(sbnText);
                sbnText = head + " 로부터 " + sbnText;
                sounds.speakAfterBeep(utils.makeEtc(sbnText, 200));
                break;

            case YNN: // talk only

                if (IgnoreThis.contains(sbnText, textIgnores))
                    return;
                sbnText = utils.strShorten(sbnPackageNick, utils.text2OneLine(sbnText));
                if (IsWhoNine.in(nineIgnores, sbnPackageNick))
                    sbnText = new Numbers().deduct(sbnText);
                sounds.speakAfterBeep(sbnPackageNick + " 로 부터 " + sbnText);
                break;

            case BAND:

                // groupNames : null, who : 분당사랑케어,

                if (sbnText.contains("지금 확인하세요") || IgnoreThis.contains(sbnText, textIgnores))
                    return;
                if (kvCommon.isDup(sbnWho, sbnText))
                    return;
                sbnText = utils.strShorten(sbnWho, utils.text2OneLine(sbnText));
                head = sbnGroup + "🗼"+ sbnWho +"🗼";
                notificationBar.update(sbnGroup + "🗼"+ sbnWho, sbnText, true);
                sbnText = head + " 로부터 "+ sbnText;
                sounds.speakAfterBeep(sbnPackageNick + " " + sbnText);
                break;

            default:

                if (kvCommon.isDup("none", sbnText))
                    return;
                sbnText = "새로운 앱이 설치됨,  groupNames:" + sbnGroup + ", who:" + sbnWho +
                        ", text:" + utils.text2OneLine(sbnText);
                notificationBar.update("[새 앱]", sbnText, true);
                logUpdate.addQue("[ " + sbnAppFullName + " ]", sbnText);
                sounds.speakAfterBeep(sbnText);
                break;
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) { }
}