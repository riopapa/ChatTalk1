package com.urrecliner.chattalk;

import static android.content.Context.MODE_PRIVATE;
import static com.urrecliner.chattalk.Upload2Google.sheetQues;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Environment;
import android.view.View;
import android.widget.ScrollView;

import androidx.appcompat.app.ActionBar;

import com.google.android.material.tabs.TabLayout;
import com.urrecliner.chattalk.Sub.AlertLine;

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

    static long sharedStart, sharedFinish;
    static boolean isPhoneBusy = false;
    static boolean isRotate = false;

    static AlertsAdapter alertsAdapter = null;
    static TabLayout topTabs;
    static ArrayList<AlertLine> alertLines;


    static String chatGroup;
    static int linePos = 999;
    static final String lastChar = "힝";

    enum soundType { PRE, POST, ERR, TESLY, ONLY}
    static final int[] beepRawIds = { R.raw.a0_pre_sound, R.raw.a1_post_sound, R.raw.a2_alert, R.raw.a3_tesly, R.raw.a4_only};

    void set(Context context, String msg) {
        mContext = context;
        sharePref = mContext.getSharedPreferences("sayText", MODE_PRIVATE);
        sharedEditor = sharePref.edit();
        if (mActivity != null)
            mLayoutView = mActivity.findViewById(R.id.main_layout);
        sharedStart = sharePref.getLong("start",0);
        sharedFinish = sharePref.getLong("finish",0);
        packageDirectory = new File(Environment.getExternalStorageDirectory(), "_ChatTalkLog");
        downloadFolder = new File(Environment.getExternalStorageDirectory(), "download");
        tableFolder = new File(downloadFolder, "_ChatTalk");

        alertIndex = new AlertIndex();
        logQueUpdate = new LogQueUpdate(mContext);
        msgAndroid = new MsgAndroid();
        sounds = new Sounds(); sounds.init();
        utils = new Utils();
        tableListFile = new TableListFile();
        new OptionTables().readAll();

        FileIO.readyPackageFolder();
        utils.logW("Vars", "/ / "+msg+" / / \n");
        if (sharedStart == 0)
            utils.setTimeBoundary();

        sheetQues = new ArrayList<>();
        AlertTable.readFile();
        AlertTable.makeArrays();
    }


}