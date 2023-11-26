package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.apps;
import static com.urrecliner.chattalk.Vars.kGroupWhoIgnores;
import static com.urrecliner.chattalk.Vars.kkTxtIgnores;
import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.Vars.nineIgnores;
import static com.urrecliner.chattalk.Vars.sbnAppFullName;
import static com.urrecliner.chattalk.Vars.sbnGroup;
import static com.urrecliner.chattalk.Vars.sbnAppIdx;
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

import com.urrecliner.chattalk.Sub.App;
import com.urrecliner.chattalk.Sub.IgnoreThis;
import com.urrecliner.chattalk.Sub.IsWhoNine;
import com.urrecliner.chattalk.Sub.KeyVal;
import com.urrecliner.chattalk.Sub.Numbers;
import com.urrecliner.chattalk.Sub.PhoneVibrate;
import com.urrecliner.chattalk.Sub.StockName;

public class NotificationListener extends NotificationListenerService {
    final String SMS = "sms";
    final String KATALK = "kk";
    final String TOSS = "tos";

    final String TESLA = "ts";             // tesla only
    final String TELEGRAM = "tG";
    final String APP = "app";   // general application
    long tesla_time = 0;

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

                if (sbnGroup.contains("곳에서 보냄") || sbnText.contains("곳에서 보냄"))
                    return;
                sbnText = utils.text2OneLine(sbnText);
                if (kvTelegram.isDup(sbnGroup, sbnText))
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
                String head = "[텔레 "+ sbnGroup + "|" + sbnWho + "]";
                logUpdate.addQue(head, sbnText);
                notificationBar.update(sbnGroup + "|" + sbnWho, sbnText, true);
                sbnText = head + " 로 부터. " + sbnText;
                sounds.speakAfterBeep(utils.makeEtc(sbnText, 200));
                break;

            case TOSS:

                for (String s: tossIgnores) {
                    if (sbnWho.contains(s) || sbnText.contains(s))
                        return;
                }
                sbnText = utils.strShorten(sbnPackageNick, utils.text2OneLine(sbnWho+"|"+ sbnText));
                head = "[" + sbnPackageNick + "]";
                logUpdate.addQue(head, sbnText);
                notificationBar.update(sbnPackageNick, sbnText, true);
                sbnText = "토스 로부터 " + new Numbers().deduct(sbnText);
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
                        sounds.beepOnce(Vars.soundType.TESLA.ordinal());
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

            case APP:

                if (IgnoreThis.contains(sbnText, textIgnores))
                    break;

                if (sbnGroup.equals("") && kvCommon.isDup(sbnWho, sbnText))
                    return;
                if (sbnWho.equals("") && kvCommon.isDup(sbnGroup, sbnText))
                    return;
                sbnText = utils.text2OneLine(sbnText);

                App app = apps.get(sbnAppIdx);

                if (app.nickName.equals("NH나무")) {
                    new MsgNamoo().say(utils.text2OneLine(sbnText));
                    break;
                }

                sbnText = utils.strShorten(sbnWho, sbnText);
                sbnText = utils.strShorten(app.nickName, sbnText);

                if (app.addWho)
                    sbnText = sbnWho + " " + sbnText;

                if (app.say) {
                    String say = app.nickName;
                    if (app.grp)
                        say = say + " "+ sbnGroup;
                    if (app.who)
                        say = say + " " + sbnWho;
                    say = say + " 로부터 ";
                    say = say + ((app.num) ? sbnText : new Numbers().deduct(sbnText));
                    sounds.speakAfterBeep(utils.makeEtc(say, 200));
                }

                if (app.log) {
                    head = "[" + app.nickName;
                    if (app.grp)
                        head = head + " " + sbnGroup;
                    if (app.who)
                        head = head + " " + sbnWho;
                    head = head + "]";
                    logUpdate.addQue(head , sbnText);
                }
                notificationBar.update(app.nickName + ":"+ sbnWho, sbnText, true);
                break;

            default:

                if (kvCommon.isDup("none", sbnText))
                    return;
                sbnText = utils.text2OneLine(sbnText);
                sounds.speakAfterBeep("새 앱 설치됨 " + sbnText);
                sbnText = "새로운 앱이 설치됨,  group : " + sbnGroup + ", who : " + sbnWho +
                        ", text : " + sbnText;
                notificationBar.update(sbnAppFullName, sbnText, true);
                logUpdate.addQue("[ " + sbnAppFullName + " ]", sbnText);
                break;
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) { }
}