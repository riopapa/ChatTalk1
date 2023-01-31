package com.urrecliner.chattalk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.view.View;
import android.widget.ScrollView;

import androidx.appcompat.app.ActionBar;

import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.ArrayList;

public class Vars {
    static File packageDirectory = null;
    static File tableFolder = null;
    static File downloadFolder = null;
    static File todayFolder = null;

    static String toDay = "ToDay";

    static String packageIgnoreStr = null;    // @com.ignoring.package @com.ignored.package ...
    static ArrayList<String> pkgFullNames, pkgNickNames, pkgTypes;
    static String sbnGroup, sbnWho, sbnText, sbnAppFullName, sbnPackageType, sbnPackageNick;

    static TableListFile tableListFile = null;

    static String kGroupIgnores = null;
    static String[] kkTxtIgnores = null;
    static String[] kGroupWho = null;
    static String[] kKey1 = null;
    static String[] kKey2 = null;
    static String[] kTalk = null;
    static String[] kSkip = null;

    static String kGroupDot = null;       // 1000.고선 1001.그룹
    static String kGroupWhoDot = null;    // 1000group1.who 1001group1.who2 1002group2.who3
    static String[] kGroupSay = null;
    static String[] kGroupSkip1 = null;
    static String[] kGroupSkip2 = null;
    static String[] kGroupSkip3 = null;
    static String[] kGroupSkip4 = null;
    static String[] kGroupWhoSaved = null;
    static Integer[] kGroupWhoS;
    static Integer[] kGroupWhoF;

    static String[] smsWhoIgnores = null;
    static String[] smsTextIgnores = null;
    static String[] systemIgnores = null;
    static String[] textIgnores = null;
    static String nowFileName;

    static ScrollView scrollView1;

    static int replGroupCnt = 0;
    static String [] replGroup;
    static String [][] replLong, replShort;

    static SharedPreferences sharePref;
    static SharedPreferences.Editor sharedEditor;

    @SuppressLint("StaticFieldLeak")
    static Context mContext = null;
    @SuppressLint("StaticFieldLeak")
    static Activity mActivity = null;
    @SuppressLint("StaticFieldLeak")
    static View mLayoutView;

    static final int SHOW_MESSAGE = 1234;
    static String logQue = "", logSave = "";
    static ActionBar aBar = null;
    static AudioManager mAudioManager = null;
    static AudioFocusRequest mFocusGain = null;
    static boolean speakSwitchOn = false;

    /* module list */
    static AlertIndex alertIndex = null;
    static LogQueUpdate logQueUpdate = null;
    static MsgAndroid msgAndroid = null;
    static MsgKaTalk msgKaTalk = null;
    static Sounds sounds = null;
    static Utils utils = null;
    static VarInit varInit = null;

    static long sharedStart, sharedFinish;
    static boolean isPhoneBusy = false;
    static boolean isRotate = false;

    static AlertsAdapter alertsAdapter = null;
    static TabLayout topTabs;
    static ArrayList<AlertLine> alertLines;
    public static class AlertLine {
        String group, who, key1, key2, talk, skip, memo, more;
        int matched;
        AlertLine(String group, String who, String key1, String key2, String talk, int matched, String skip,     String memo, String more) {
            this.group = group;this.who = who;
            this.key1 = key1;this.key2 = key2;this.talk = talk;
            this.matched = matched; this.skip = skip; this.memo = memo; this.more = more;
        }
    }

    public static class WhoText {
        public String who, text;
        public WhoText(String who, String text) {
            this.who = who;
            this.text = text;
        }
    }

    static String chatGroup;
    static int linePos = 999;
    static final String lastChar = "힝";

    enum soundType { PRE, POST, ERR, TESLY, ONLY}
    static final int[] beepRawIds = { R.raw.a0_pre_sound, R.raw.a1_post_sound, R.raw.a2_alert, R.raw.a3_tesly, R.raw.a4_only};

}