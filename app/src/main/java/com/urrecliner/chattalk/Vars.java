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
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.ActionBar;

import com.google.android.material.tabs.TabLayout;
import com.urrecliner.chattalk.Sub.AlertLine;
import com.urrecliner.chattalk.Sub.AlertLinesGetPut;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Vars {
    static File packageDirectory = null;
    static File tableFolder = null;
    static File downloadFolder = null;
    static File todayFolder = null;

    public static String toDay = "ToDay";

    static ArrayList<String> packageIgnores;
    static ArrayList<String> pkgFullNames, pkgNickNames, pkgTypes;
    static String sbnGroup, sbnWho, sbnText, sbnAppFullName, sbnPackageType, sbnPackageNick;

    static TableListFile tableListFile = null;

    static String[] kGroupWhoIgnores = null;
    static String[] kkTxtIgnores = null;

    static String[] aGroupSaid = null;
    static int [][][] aAlertLineIdx;

    static List<String> aGroups;    // {고선, 텔레, 힐}
    static String[] aGSkip1, aGSkip2, aGSkip3, aGSkip4;
    static String[][] aGroupWhos;     // [2] 이진홍, 김선수
    static String[][][] aGroupWhoKey1, aGroupWhoKey2, aGroupWhoSkip, aGroupWhoPrev, aGroupWhoNext;

    static String[] smsWhoIgnores = null;
    static String[] smsTextIgnores = null;
    static String[] systemIgnores = null;
    static String[] textIgnores = null;
    static String[] nineIgnores = null;
    static String nowFileName;

    static int replGroupCnt = 0;
    static String [] replGroup;
    static String [][] replLong, replShort;

    public static SharedPreferences sharePref;
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

    /* module list */
    static AlertWhoIndex alertWhoIndex = null;

    static boolean isPhoneBusy = false;

    static AlertsAdapter alertsAdapter = null;
    static TabLayout topTabs;
    static ArrayList<AlertLine> alertLines;

    static String chatGroup;
    static final String lastChar = "힝";

    enum soundType { PRE, POST, ERR, TESLY, ONLY}
    static final int[] beepRawIds = { R.raw.a0_pre_sound, R.raw.a1_post_sound, R.raw.a2_alert, R.raw.a3_hello_tesry, R.raw.a4_only};

    void set(Context context, String msg) {
        Log.w("vars","vars set msg="+msg);
        mContext = context;
        sharePref = mContext.getSharedPreferences("sayText", MODE_PRIVATE);
        sharedEditor = sharePref.edit();
        if (mActivity != null)
            mLayoutView = mActivity.findViewById(R.id.main_layout);
        downloadFolder = new File(Environment.getExternalStorageDirectory(), "download");
        tableFolder = new File(downloadFolder, "_ChatTalk");

        alertWhoIndex = new AlertWhoIndex();
        tableListFile = new TableListFile();
        new OptionTables().readAll();

        FileIO.readyPackageFolder();
        sheetQues = new ArrayList<>();
        alertLines = new AlertLinesGetPut().get(context);
        if (alertLines.size() == 0) {
            AlertTable.readFile("var");
            new AlertLinesGetPut().put(alertLines, context);
        } else
            AlertTable.makeArrays();
    }
}