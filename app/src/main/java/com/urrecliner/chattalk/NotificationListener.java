package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.kGroupIgnores;
import static com.urrecliner.chattalk.Vars.kkTxtIgnores;
import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.Vars.packageIgnoreStr;
import static com.urrecliner.chattalk.Vars.sbnAppFullName;
import static com.urrecliner.chattalk.Vars.sbnGroup;
import static com.urrecliner.chattalk.Vars.sbnPackageNick;
import static com.urrecliner.chattalk.Vars.sbnPackageType;
import static com.urrecliner.chattalk.Vars.sbnText;
import static com.urrecliner.chattalk.Vars.sbnWho;
import static com.urrecliner.chattalk.Vars.smsTextIgnores;
import static com.urrecliner.chattalk.Vars.smsWhoIgnores;
import static com.urrecliner.chattalk.Vars.textIgnores;

import android.content.Context;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.urrecliner.chattalk.Sub.IgnoreText;
import com.urrecliner.chattalk.Sub.IsWhoText;
import com.urrecliner.chattalk.Sub.MapWhoText;
import com.urrecliner.chattalk.Sub.WhoText;

import java.util.ArrayList;
import java.util.HashMap;

public class NotificationListener extends NotificationListenerService {

    final String SMS = "sms";
    final String KATALK = "kk";
    final String NAHMOO = "nh";
    final String TOSS = "toss";
    //                            Say  Que Log
    final String YYY = "yyy"; //   Y    Y   Y
    final String YYN = "yyn"; //   Y    Y   N
    final String YY9 = "yy9"; //   Y    Y   N
    final String YYX = "yyx"; //   Y    Y   X exclude Group
    final String YNX = "ynx"; //   Y    N   X  no Who
    final String YWX = "ywx"; //   Y    N   X  who should be in text
    final String YNN = "ynn"; //   Y    N   X  no Who, speak only
    final String TESLA = "ts";             // tesla only
    final String TELEGRAM = "tG";
    final String BAND = "bd";                    // band

    long tesla_time = 0;

    static HashMap<String, String> kkWhoTexts = new HashMap<>();
//    static ArrayList<WhoText> kkWhoTexts = new ArrayList<>();
    static ArrayList<WhoText> smsWhoTexts = new ArrayList<>();
    static ArrayList<WhoText> tgWhoTexts = new ArrayList<>();
    static ArrayList<WhoText> whoTexts = new ArrayList<>();
    String head;

    LogQueUpdate logQueUpdate;
    MsgKaTalk msgKaTalk;
    MsgSMS msgSMS;
    Sounds sounds;
    Utils utils;
    SbnBundle sbnBundle;
    Context context;
    Vars vars;

    @Override
    public void onCreate() {
        Log.w("notificationlistner", "onCreate()");
        context = this;
        super.onCreate();
        init();
    }

    void init() {
        mContext = this;
        vars = new Vars();
        vars.set(this, "noti Listener");
        logQueUpdate = new LogQueUpdate(this);
        msgKaTalk = new MsgKaTalk();
        msgSMS = new MsgSMS();
        sounds = new Sounds();
        utils = new Utils();
        sbnBundle = new SbnBundle();
        Upload2Google.initSheetQue();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        if (sbnBundle.bypassSbn(sbn))
            return;

        if (packageIgnoreStr == null)
            init();

        switch (sbnPackageType) {

            case KATALK:

                if (MapWhoText.repeated(kkWhoTexts, sbnWho, sbnText))
                    return;
                if (sbnGroup.equals("")) {
                    Log.w("WhoText "+kkWhoTexts.size(),"mWho "+sbnWho);
                    if (kGroupIgnores.contains("!"+ sbnWho +"!") ||
                        IgnoreText.contains(sbnText, kkTxtIgnores))
                        return;
                    String head = "{카톡!"+ sbnWho + "} ";
                    sbnText = utils.strReplace(sbnWho, utils.text2OneLine(sbnText));
                    NotificationBar.update("카톡!"+sbnWho, sbnText);
                    logQueUpdate.add( head, sbnText);
                    sounds.speakAfterBeep(" 카톡왔음 " + sbnWho + " 님이 " + utils.replaceKKHH(utils.makeEtc(sbnText, 150)));
                } else {
                    String gs = "!"+ sbnGroup +"!";
                    if (kGroupIgnores.contains(gs))
                        return;
                    else if (IgnoreText.contains(sbnText, kkTxtIgnores))
                        return;
                    msgKaTalk.say(sbnGroup, sbnWho, utils.text2OneLine(sbnText));
                }
                break;

            case SMS:

                if (sbnWho.replaceAll(mContext.getString(R.string.regex_number_only), "").length() < 5) {
                    if (!sbnText.contains("스마트폰 배우고"))
                        break;
                }
                if (IgnoreText.contains(sbnWho, smsWhoIgnores) || IgnoreText.contains(sbnText, smsTextIgnores))
                    return;
                if (IsWhoText.repeated(smsWhoTexts, sbnWho, sbnText))
                    return;
                msgSMS.say(sbnWho, utils.text2OneLine(sbnText));
                break;

            case NAHMOO:

                new MsgNamoo().say(utils.text2OneLine(sbnText));
                break;

            case BAND:

                // group : null, who : 분당사랑케어,

                if (sbnText.contains("지금 확인하세요") || IsWhoText.repeated(whoTexts, sbnWho, sbnText)
                        || IgnoreText.contains(sbnText, textIgnores))
                    return;
                sbnText = utils.strReplace(sbnWho, utils.text2OneLine(sbnText));
                head = sbnGroup + "🗼"+ sbnWho +"🗼";
                NotificationBar.update(sbnGroup + "🗼"+ sbnWho, sbnText);
                sbnText = head + " 로부터 "+ sbnText;
                sounds.speakAfterBeep(sbnPackageNick + " " + sbnText);
                break;

            case TELEGRAM:

                if (sbnText.contains("곳에서 보냄"))
                    return;
                if (IsWhoText.repeated(tgWhoTexts, sbnWho, sbnText))
                    return;

                sbnText = utils.text2OneLine(sbnText);
                final String [] stocks = { "바른"};
                for (String s: stocks) {
                    if (sbnWho.contains(s)) {
                        msgKaTalk.say("텔레", sbnWho, sbnText);
                        return;
                    }
                }
                head = "[텔레 "+ sbnGroup + "📞" + sbnWho + "]";
                logQueUpdate.add(head, sbnText);
                NotificationBar.update(sbnGroup + "📞" + sbnWho, sbnText);
                sbnText = head + " 로 부터. " + sbnText;
                sounds.speakAfterBeep(utils.makeEtc(sbnText, 200));
                break;

            case TESLA:

                final String [] ignoreTesla = { "연결 중", "연결 해제됨", "핸드폰을 키로"};
                for (String s: ignoreTesla) {
                    if (sbnText.contains(s))
                        return;
                }
                if (sbnText.contains("연결됨")) {
                    long nowTime = System.currentTimeMillis();
                    if ((nowTime - tesla_time) > 30 * 60 * 1000)    // 20 min.
                        sounds.beepOnce(Vars.soundType.TESLY.ordinal());
                    tesla_time = nowTime;
                    break;
                }
                if (IsWhoText.repeated(whoTexts, sbnWho, sbnText))
                    break;
                logQueUpdate.add("[ 테스리 ]", sbnText);
                NotificationBar.update(sbnPackageNick, sbnText);
//                FileIO.append2Today("Tesla.txt", sbnText);
                sounds.speakAfterBeep("테스리로 부터 " + sbnText);
                break;

            case TOSS:

                final String [] ignoreToss = { "원 적립", "퀴즈 정답" };
                for (String s: ignoreToss) {
                    if (sbnText.contains(s) || sbnWho.contains(s))
                        return;
                }
                sbnText = utils.text2OneLine(sbnText);  // 토스는 아직 줄일게 없음
                head = "[" + sbnPackageNick + "🖐️" +"] ";
                logQueUpdate.add(head , sbnWho+" "+sbnText);
                NotificationBar.update(sbnPackageNick, sbnText);
                sbnText = "토스 로부터 " + sbnText;
                sounds.speakAfterBeep(utils.makeEtc(sbnText, 200));
                break;

            case YYY:

                if (IsWhoText.repeated(whoTexts, sbnPackageNick, sbnText) ||
                        IgnoreText.contains(sbnText, textIgnores))
                    break;
                sbnText = utils.text2OneLine(sbnText);
                sbnText = utils.strReplace(sbnWho, sbnText);
                head = sbnGroup + "👍"+ sbnWho +"👍";
                logQueUpdate.add("[" + sbnPackageNick + "] "+head, sbnText);
                NotificationBar.update(sbnGroup + "👍"+ sbnWho, sbnText);
                utils.logW(sbnPackageNick, head+sbnText);
                sbnText = sbnPackageNick + " 로부터 " + head + sbnText;
                sounds.speakAfterBeep(utils.makeEtc(sbnText, 200));
                break;

            case YYX:     // exclude Group e.g. bank app

                sbnText = utils.text2OneLine(sbnText);
                if (IsWhoText.repeated(whoTexts, sbnPackageNick, sbnText) ||
                        IgnoreText.contains(sbnText, textIgnores))
                    break;
                sbnText = utils.strReplace(sbnWho, sbnText);
                head = "[" + sbnPackageNick + "🖐️"+ sbnWho +"] ";
                logQueUpdate.add(head , sbnText);
                NotificationBar.update(sbnPackageNick + "🖐️"+ sbnWho, sbnText);
                sbnText = sbnPackageNick + " 로부터 " + head + sbnText;
                sounds.speakAfterBeep(utils.makeEtc(sbnText, 200));
                break;

            case YNX: // no who, log Yes, say Yes

                if (IgnoreText.contains(sbnText, textIgnores))
                    return;
                sbnText = utils.text2OneLine(sbnText);
                sbnText = sbnGroup + "✓" + sbnText;
                logQueUpdate.add(sbnPackageNick, sbnText);
                sounds.speakAfterBeep(sbnPackageNick + " 로 부터 " + sbnText);
                break;

            case YWX: // treat who as text

                if (IgnoreText.contains(sbnText, textIgnores))
                    return;
                sbnText = utils.text2OneLine(sbnText);
                sbnText = sbnWho + "✓" + sbnText;
                logQueUpdate.add(sbnPackageNick, sbnText);
                NotificationBar.update(sbnPackageNick, sbnText);
                sounds.speakAfterBeep(sbnPackageNick + " 로 부터 " + sbnText);
                break;

            case YYN:   //

                if (IgnoreText.contains(sbnText, textIgnores) ||
                    IsWhoText.repeated(whoTexts, sbnWho, sbnText))
                    return;
                sbnText = utils.text2OneLine(sbnText);
                sbnText = utils.strReplace(sbnWho, utils.text2OneLine(sbnText));
                head = "[" + sbnPackageNick + "🖐️"+ sbnGroup + "🖐️"+ sbnWho +"] ";
                logQueUpdate.add(head , sbnText);
                NotificationBar.update(sbnGroup + "🖐️"+ sbnWho, sbnText);
                sbnText = sbnPackageNick + " 로부터 " + head + sbnText;
                sounds.speakAfterBeep(utils.makeEtc(sbnText, 230));
                break;

            case YY9:   //

                if (IgnoreText.contains(sbnText, textIgnores) ||
                        IsWhoText.repeated(whoTexts, sbnWho, sbnText))
                    return;
                sbnText = utils.text2OneLine(sbnText);
                sbnText = utils.strReplace(sbnWho, utils.text2OneLine(sbnText));
                head = "[" + sbnPackageNick + "¦️" + sbnGroup + "¦️"  + sbnWho +"] ";
                logQueUpdate.add(head , sbnText);
                NotificationBar.update(sbnGroup + "¦️"  + sbnWho, sbnText);
                sbnText = sbnPackageNick + " 로부터 " + head +
                        sbnText.replaceAll("[0-9]", "");
                sounds.speakAfterBeep(sbnText);
                break;

            case YNN: // no who

                if (IgnoreText.contains(sbnText, textIgnores))
                    return;
                sbnText = utils.text2OneLine(sbnText);
                sbnText = sbnGroup + "§" + sbnWho + "§" + sbnText;
                sounds.speakAfterBeep(sbnPackageNick + " 로 부터 " + sbnText);
                break;

            default:

                if (IsWhoText.repeated(whoTexts, sbnWho, sbnText))
                    return;
                sbnText = utils.text2OneLine(sbnText);
                sbnText = "새로운 앱이 설치됨,  group:" + sbnGroup + " who:" + sbnWho + " text:" + sbnText;
                NotificationBar.update("[새 앱]", sbnText);
                logQueUpdate.add("[ " + sbnAppFullName + " ]", sbnText);
                utils.logW("new App "+ sbnGroup, sbnAppFullName +" "+ sbnText);
                sounds.speakAfterBeep(sbnText);
                break;
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) { }
}