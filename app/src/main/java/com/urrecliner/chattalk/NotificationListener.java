package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.ActivityMain.kkWhoTexts;
import static com.urrecliner.chattalk.ActivityMain.msgKaTalk;
import static com.urrecliner.chattalk.ActivityMain.msgSMS;
import static com.urrecliner.chattalk.ActivityMain.notificationBar;
import static com.urrecliner.chattalk.ActivityMain.sbnBundle;
import static com.urrecliner.chattalk.ActivityMain.smsWhoTexts;
import static com.urrecliner.chattalk.ActivityMain.subFunc;
import static com.urrecliner.chattalk.ActivityMain.vars;
import static com.urrecliner.chattalk.ActivityMain.whoAndTexts;
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
import static com.urrecliner.chattalk.ActivityMain.utils;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.urrecliner.chattalk.Sub.IgnoreThis;
import com.urrecliner.chattalk.Sub.MapWhoText;

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

    static MapWhoText mapWhoText = null;
    String head;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        if (vars == null) {
            vars = new Vars();
            vars.set(this, "noti Post");
        }
        if (subFunc == null)
            subFunc = new SubFunc();
        if (utils == null)
            utils = new Utils();

        if (sbnBundle == null)
            sbnBundle = new SbnBundle();

        if (notificationBar == null)
            notificationBar = new NotificationBar();

        if (mapWhoText == null)
            mapWhoText = new MapWhoText();

        if (sbnBundle.bypassSbn(sbn))
            return;

        switch (sbnPackageType) {

            case KATALK:

                if (IgnoreThis.contains(sbnText, kkTxtIgnores))
                    return;
                if (sbnGroup.equals("")) {  // no groupNames
                    if (sbnWho.equals(""))  // nothing
                        return;
                    if (IgnoreThis.contains(sbnWho, kGroupWhoIgnores)||
                            mapWhoText.repeated(kkWhoTexts, sbnWho, sbnText))
                        return;
                    sbnText = utils.strReplace(sbnWho, utils.text2OneLine(sbnText));
                    String head = "{카톡!"+ sbnWho + "} ";
                    notificationBar.update("카톡!"+sbnWho, sbnText, true);
                    subFunc.logUpdate.addQue( head, sbnText);
                    subFunc.sounds.speakAfterBeep(" 카톡왔음 " + sbnWho + " 님이 " + utils.replaceKKHH(utils.makeEtc(sbnText, 150)));
                } else {
                    if ((IgnoreThis.contains(sbnGroup, kGroupWhoIgnores)) ||
                        (!sbnWho.equals("") && IgnoreThis.contains(sbnWho, kGroupWhoIgnores)) ||
                            mapWhoText.repeated(kkWhoTexts, sbnWho, sbnText))
                        return;
                    if (msgKaTalk == null)
                        msgKaTalk = new MsgKaTalk();
                    msgKaTalk.say(sbnGroup, sbnWho, utils.text2OneLine(sbnText));
                }
                break;

            case SMS:

                if (sbnWho.replaceAll(mContext.getString(R.string.regex_number_only), "").length() < 4 &&
                    !sbnText.contains("스마트폰 배우고"))
                        return;
                if (IgnoreThis.contains(sbnWho, smsWhoIgnores) || IgnoreThis.contains(sbnText, smsTextIgnores))
                    return;
                if (mapWhoText.repeated(smsWhoTexts, sbnWho, sbnText))
                    return;
                if (msgSMS == null)
                    msgSMS = new MsgSMS();
                msgSMS.say(sbnWho, utils.text2OneLine(sbnText));
                break;

            case NAHMOO:

                new MsgNamoo().say(utils.text2OneLine(sbnText));
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
                if (mapWhoText.repeated(whoAndTexts, sbnWho, sbnText))
                    break;
                subFunc.logUpdate.addQue("[ 테스리 ]", sbnText);
                notificationBar.update(sbnPackageNick, sbnText, true);
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
                sbnText = sbnWho+"🖐"+ utils.text2OneLine(sbnText);
                subFunc.logUpdate.addQue(head , sbnText);
                notificationBar.update(sbnPackageNick, sbnText, true);
                sbnText = "토스 로부터 " + sbnText;
                subFunc.sounds.speakAfterBeep(utils.makeEtc(sbnText, 200));
                break;

            case YYX:     // exclude Group e.g. bank app

                if (mapWhoText.repeated(whoAndTexts, sbnPackageNick, sbnText) ||
                        IgnoreThis.contains(sbnText, textIgnores))
                    break;
                sbnText = utils.strReplace(sbnWho, utils.text2OneLine(sbnText));
                head = "[" + sbnPackageNick + "🖐️"+ sbnWho +"] ";
                subFunc.logUpdate.addQue(head , sbnText);
                notificationBar.update(sbnPackageNick + ":"+ sbnWho, sbnText, true);
                sbnText = sbnPackageNick + " 로부터 " + head + sbnText;
                subFunc.sounds.speakAfterBeep(utils.makeEtc(sbnText, 200));
                break;

            case YNX: // no who, log Yes, say Yes

                if (IgnoreThis.contains(sbnText, textIgnores))
                    return;
                sbnText = sbnGroup + "✓" + utils.text2OneLine(sbnText);
                subFunc.logUpdate.addQue("["+sbnPackageNick+"]", sbnText);
                subFunc.sounds.speakAfterBeep(sbnPackageNick + " 로 부터 " + sbnText);
                break;

            case YWX: // treat who as text

                if (IgnoreThis.contains(sbnText, textIgnores))
                    return;
                sbnText = sbnWho + "✓" + utils.text2OneLine(sbnText);
                subFunc.logUpdate.addQue(sbnPackageNick, sbnText);
                notificationBar.update(sbnPackageNick, sbnText, true);
                subFunc.sounds.speakAfterBeep(sbnPackageNick + " 로 부터 " + sbnText);
                break;

            case YYN:   //

                if (IgnoreThis.contains(sbnText, textIgnores) || IgnoreThis.contains(sbnWho, textIgnores) ||
                        mapWhoText.repeated(whoAndTexts, sbnWho, sbnText))
                    return;
                sbnText = utils.text2OneLine(sbnText);
                sbnText = utils.strReplace(sbnGroup.equals("")? sbnWho:sbnGroup, sbnText);
                head = "[" + sbnPackageNick + "🖐️"+ sbnGroup + "🖐️"+ sbnWho +"] ";
                subFunc.logUpdate.addQue(head, sbnText);
                notificationBar.update((sbnGroup.equals("")) ? sbnPackageNick + "🖐️"+ sbnWho
                        : sbnGroup + "🖐️"+ sbnWho, sbnText, true);
                sbnText = sbnPackageNick + " 로부터 " + head + sbnText;
                subFunc.sounds.speakAfterBeep(utils.makeEtc(sbnText, 200));
                break;

            case YYY:

                if (mapWhoText.repeated(whoAndTexts, sbnPackageNick, sbnText) ||
                        IgnoreThis.contains(sbnText, textIgnores))
                    break;
                sbnText = utils.strReplace(sbnWho, utils.text2OneLine(sbnText));
                head = sbnGroup + "👍"+ sbnWho +"👍";
                subFunc.logUpdate.addQue("[" + sbnPackageNick + "] "+head, sbnText);
                notificationBar.update(sbnGroup + "👍"+ sbnWho, sbnText, true);
                utils.logW(sbnPackageNick, head+sbnText);
                sbnText = sbnPackageNick + " 로부터 " + head + sbnText;
                subFunc.sounds.speakAfterBeep(utils.makeEtc(sbnText, 200));
                break;

            case YNN: // talk only

                if (IgnoreThis.contains(sbnText, textIgnores))
                    return;
                Log.w(sbnGroup+" ynn "+sbnWho, sbnText);
                sbnText = utils.strReplace(sbnWho, utils.text2OneLine(sbnText));
                subFunc.sounds.speakAfterBeep(sbnPackageNick + " 로 부터 " + sbnText);
                break;

            case BAND:

                // groupNames : null, who : 분당사랑케어,

                if (sbnText.contains("지금 확인하세요") || mapWhoText.repeated(whoAndTexts, sbnWho, sbnText)
                        || IgnoreThis.contains(sbnText, textIgnores))
                    return;
                sbnText = utils.strReplace(sbnWho, utils.text2OneLine(sbnText));
                head = sbnGroup + "🗼"+ sbnWho +"🗼";
                notificationBar.update(sbnGroup + "🗼"+ sbnWho, sbnText, true);
                sbnText = head + " 로부터 "+ sbnText;
                subFunc.sounds.speakAfterBeep(sbnPackageNick + " " + sbnText);
                break;

            case TELEGRAM:

                if (sbnText.contains("곳에서 보냄"))
                    return;
                sbnText = utils.text2OneLine(sbnText);
                final String [] groupChats = {"부자 프로", "경제적 자유를", "단타의 귀재" };
                final String [] groupNames = {"부자",        "경자",    "단귀"};
                for (int i = 0; i < groupChats.length; i++) {
                    if (sbnWho.contains(groupChats[i])) {
                        if (sbnWho.contains(":"))   // 부자 인 겅우 group : who 로 구성됨
                            sbnWho = sbnWho.substring(sbnWho.indexOf(":")+2).trim();
                        if (msgKaTalk == null)
                            msgKaTalk = new MsgKaTalk();
                        msgKaTalk.say(groupNames[i], sbnWho, sbnGroup+sbnText);
                        return;
                    }
                }
                head = "[텔레 "+ sbnGroup + "|" + sbnWho + "]";
                subFunc.logUpdate.addQue(head, sbnText);
                notificationBar.update(sbnGroup + "|" + sbnWho, sbnText, true);
                sbnText = head + " 로 부터. " + sbnText;
                subFunc.sounds.speakAfterBeep(utils.makeEtc(sbnText, 200));
                break;

            default:

                if (mapWhoText.repeated(whoAndTexts, sbnWho, sbnText))
                    return;
                sbnText = "새로운 앱이 설치됨,  groupNames:" + sbnGroup + ", who:" + sbnWho +
                        ", text:" + utils.text2OneLine(sbnText);
                notificationBar.update("[새 앱]", sbnText, true);
                subFunc.logUpdate.addQue("[ " + sbnAppFullName + " ]", sbnText);
                subFunc.sounds.speakAfterBeep(sbnText);
                break;
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) { }
}