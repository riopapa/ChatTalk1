package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.kGroupWhoIgnores;
import static com.urrecliner.chattalk.Vars.kkTxtIgnores;
import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.Vars.sbnAppFullName;
import static com.urrecliner.chattalk.Vars.sbnGroup;
import static com.urrecliner.chattalk.Vars.sbnPackageNick;
import static com.urrecliner.chattalk.Vars.sbnPackageType;
import static com.urrecliner.chattalk.Vars.sbnText;
import static com.urrecliner.chattalk.Vars.sbnWho;
import static com.urrecliner.chattalk.Vars.smsTextIgnores;
import static com.urrecliner.chattalk.Vars.smsWhoIgnores;
import static com.urrecliner.chattalk.Vars.textIgnores;
import static com.urrecliner.chattalk.MainActivity.utils;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.urrecliner.chattalk.Sub.IgnoreThis;
import com.urrecliner.chattalk.Sub.MapWhoText;

import java.util.HashMap;

public class NotificationListener extends NotificationListenerService {

    final String SMS = "sms";
    final String KATALK = "kk";
    final String NAHMOO = "nh";
    final String TOSS = "toss";
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

    static HashMap<String, String> kkWhoTexts = new HashMap<>();
    static HashMap<String, String> smsWhoTexts = new HashMap<>();
    static HashMap<String, String> whoAndTexts = new HashMap<>();

    String head;

    static Vars vars = null;
    static SubFunc subFunc = null;
    static MsgKaTalk msgKaTalk = null;
    static MsgSMS msgSMS = null;
    static SbnBundle sbnBundle = null;

    @Override
    public void onCreate() {
        if (mContext == null || subFunc == null) {
            vars = new Vars();
            vars.set(this, "noti Create");
            subFunc = new SubFunc();
            Log.w("notilisten", "onCreate()");
        }
        super.onCreate();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        if (subFunc == null) {
            vars = new Vars();
            vars.set(this, "noti Post");
            subFunc = new SubFunc();
        }
        if (utils == null)
            utils = new Utils();
        if (sbnBundle == null)
            sbnBundle = new SbnBundle();
        if (sbnBundle.bypassSbn(sbn))
            return;

        switch (sbnPackageType) {

            case KATALK:

                if (IgnoreThis.contains(sbnText, kkTxtIgnores))
                    return;
                if (sbnGroup.equals("")) {
                    if (sbnWho.equals(""))
                        return;
                    if (IgnoreThis.contains(sbnWho, kGroupWhoIgnores)||
                        MapWhoText.repeated(kkWhoTexts, sbnWho, sbnText))
                        return;
                    sbnText = utils.strReplace(sbnWho, utils.text2OneLine(sbnText));
                    String head = "{카톡!"+ sbnWho + "} ";
                    NotificationBar.update("카톡!"+sbnWho, sbnText);
                    subFunc.logQueUpdate.add( head, sbnText);
                    subFunc.sounds.speakAfterBeep(" 카톡왔음 " + sbnWho + " 님이 " + utils.replaceKKHH(utils.makeEtc(sbnText, 150)));
                } else {
                    if ((IgnoreThis.contains(sbnGroup, kGroupWhoIgnores)) ||
                        (!sbnWho.equals("") && IgnoreThis.contains(sbnWho, kGroupWhoIgnores)))
                        return;
                    if (msgKaTalk == null)
                        msgKaTalk = new MsgKaTalk();
                    msgKaTalk.say(sbnGroup, sbnWho, utils.text2OneLine(sbnText));
                }
                break;

            case SMS:

                if (sbnWho.replaceAll(mContext.getString(R.string.regex_number_only), "").length() < 5) {
                    if (!sbnText.contains("스마트폰 배우고"))
                        break;
                }
                if (IgnoreThis.contains(sbnWho, smsWhoIgnores) || IgnoreThis.contains(sbnText, smsTextIgnores))
                    return;
                if (MapWhoText.repeated(smsWhoTexts, sbnWho, sbnText))
                    return;
                if (msgSMS == null)
                    msgSMS = new MsgSMS();
                msgSMS.say(sbnWho, utils.text2OneLine(sbnText));
                break;

            case NAHMOO:

                new MsgNamoo().say(utils.text2OneLine(sbnText));
                break;

            case TELEGRAM:

                if (sbnText.contains("곳에서 보냄"))
                    return;
                sbnText = utils.text2OneLine(sbnText);
                final String [] stocks = { "바른"};
                for (String s: stocks) {
                    if (sbnWho.contains(s)) {
                        if (msgKaTalk == null)
                            msgKaTalk = new MsgKaTalk();
                        msgKaTalk.say("텔레", sbnWho, sbnText);
                        return;
                    }
                }
                head = "[텔레 "+ sbnGroup + "📞" + sbnWho + "]";
                subFunc.logQueUpdate.add(head, sbnText);
                NotificationBar.update(sbnGroup + "📞" + sbnWho, sbnText);
                sbnText = head + " 로 부터. " + sbnText;
                subFunc.sounds.speakAfterBeep(utils.makeEtc(sbnText, 200));
                break;

            case TESLA:

                final String [] ignoreTesla = { "연결 중", "연결 해제됨", "핸드폰을 키로"};
                for (String s: ignoreTesla) {
                    if (sbnText.contains(s))
                        return;
                }
                if (sbnText.contains("연결됨")) {
                    long nowTime = System.currentTimeMillis();
                    if ((nowTime - tesla_time) > 30 * 60 * 1000)    // 30 min.
                        subFunc.sounds.beepOnce(Vars.soundType.TESLY.ordinal());
                    tesla_time = nowTime;
                    break;
                }
                if (MapWhoText.repeated(whoAndTexts, sbnWho, sbnText))
                    break;
                subFunc.logQueUpdate.add("[ 테스리 ]", sbnText);
                NotificationBar.update(sbnPackageNick, sbnText);
//                FileIO.append2Today("Tesla.txt", sbnText);
                subFunc.sounds.speakAfterBeep("테스리로 부터 " + sbnText);
                break;

            case TOSS:

                final String [] ignoreToss = { "원 적립", "퀴즈 정답" };
                for (String s: ignoreToss) {
                    if (sbnText.contains(s) || sbnWho.contains(s))
                        return;
                }
                head = "[" + sbnPackageNick + "]";
                subFunc.logQueUpdate.add(head , sbnWho+"🖐"+ utils.text2OneLine(sbnText));
                NotificationBar.update(sbnPackageNick, sbnText);
                sbnText = "토스 로부터 " + sbnText;
                subFunc.sounds.speakAfterBeep(utils.makeEtc(sbnText, 200));
                break;

            case YYX:     // exclude Group e.g. bank app

                if (MapWhoText.repeated(whoAndTexts, sbnPackageNick, sbnText) ||
                        IgnoreThis.contains(sbnText, textIgnores))
                    break;
                sbnText = utils.strReplace(sbnWho, utils.text2OneLine(sbnText));
                head = "[" + sbnPackageNick + "🖐️"+ sbnWho +"] ";
                subFunc.logQueUpdate.add(head , sbnText);
                NotificationBar.update(sbnPackageNick + ":"+ sbnWho, sbnText);
                sbnText = sbnPackageNick + " 로부터 " + head + sbnText;
                subFunc.sounds.speakAfterBeep(utils.makeEtc(sbnText, 200));
                break;

            case YNX: // no who, log Yes, say Yes

                if (IgnoreThis.contains(sbnText, textIgnores))
                    return;
                sbnText = sbnGroup + "✓" + utils.text2OneLine(sbnText);
                subFunc.logQueUpdate.add("["+sbnPackageNick+"]", sbnText);
                subFunc.sounds.speakAfterBeep(sbnPackageNick + " 로 부터 " + sbnText);
                break;

            case YWX: // treat who as text

                if (IgnoreThis.contains(sbnText, textIgnores))
                    return;
                sbnText = sbnWho + "✓" + utils.text2OneLine(sbnText);
                subFunc.logQueUpdate.add(sbnPackageNick, sbnText);
                NotificationBar.update(sbnPackageNick, sbnText);
                subFunc.sounds.speakAfterBeep(sbnPackageNick + " 로 부터 " + sbnText);
                break;

            case YYN:   //

                if (IgnoreThis.contains(sbnText, textIgnores) ||
                    MapWhoText.repeated(whoAndTexts, sbnWho, sbnText))
                    return;
                sbnText = utils.text2OneLine(sbnText);
                sbnText = utils.strReplace(sbnGroup.equals("")? sbnWho:sbnGroup, sbnText);
                head = "[" + sbnPackageNick + "🖐️"+ sbnGroup + "🖐️"+ sbnWho +"] ";
                subFunc.logQueUpdate.add(head , sbnText);
                NotificationBar.update((sbnGroup.equals("")) ? sbnPackageNick + "🖐️"+ sbnWho : sbnGroup + "🖐️"+ sbnWho, sbnText);
                sbnText = sbnPackageNick + " 로부터 " + head + sbnText;
                subFunc.sounds.speakAfterBeep(utils.makeEtc(sbnText, 230));
                break;

            case YYY:

                if (MapWhoText.repeated(whoAndTexts, sbnPackageNick, sbnText) ||
                        IgnoreThis.contains(sbnText, textIgnores))
                    break;
                sbnText = utils.strReplace(sbnWho, utils.text2OneLine(sbnText));
                head = sbnGroup + "👍"+ sbnWho +"👍";
                subFunc.logQueUpdate.add("[" + sbnPackageNick + "] "+head, sbnText);
                NotificationBar.update(sbnGroup + "👍"+ sbnWho, sbnText);
                utils.logW(sbnPackageNick, head+sbnText);
                sbnText = sbnPackageNick + " 로부터 " + head + sbnText;
                subFunc.sounds.speakAfterBeep(utils.makeEtc(sbnText, 200));
                break;

            case YNN: // no who

                if (IgnoreThis.contains(sbnText, textIgnores))
                    return;
                sbnText = sbnGroup + "§" + sbnWho + "§" + utils.text2OneLine(sbnText);
                subFunc.sounds.speakAfterBeep(sbnPackageNick + " 로 부터 " + sbnText);
                break;

            case BAND:

                // group : null, who : 분당사랑케어,

                if (sbnText.contains("지금 확인하세요") || MapWhoText.repeated(whoAndTexts, sbnWho, sbnText)
                        || IgnoreThis.contains(sbnText, textIgnores))
                    return;
                sbnText = utils.strReplace(sbnWho, utils.text2OneLine(sbnText));
                head = sbnGroup + "🗼"+ sbnWho +"🗼";
                NotificationBar.update(sbnGroup + "🗼"+ sbnWho, sbnText);
                sbnText = head + " 로부터 "+ sbnText;
                subFunc.sounds.speakAfterBeep(sbnPackageNick + " " + sbnText);
                break;

            default:

                if (MapWhoText.repeated(whoAndTexts, sbnWho, sbnText))
                    return;
                sbnText = utils.text2OneLine(sbnText);
                sbnText = "새로운 앱이 설치됨,  group:" + sbnGroup + " who:" + sbnWho + " text:" + sbnText;
                NotificationBar.update("[새 앱]", sbnText);
                subFunc.logQueUpdate.add("[ " + sbnAppFullName + " ]", sbnText);
                utils.logW("new App "+ sbnGroup, sbnAppFullName +" "+ sbnText);
                subFunc.sounds.speakAfterBeep(sbnText);
                break;
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) { }
}