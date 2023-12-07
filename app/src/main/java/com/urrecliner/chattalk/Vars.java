package com.urrecliner.chattalk;

import static android.content.Context.MODE_PRIVATE;
import static com.urrecliner.chattalk.NotificationListener.kvCommon;
import static com.urrecliner.chattalk.NotificationListener.kvKakao;
import static com.urrecliner.chattalk.NotificationListener.kvSMS;
import static com.urrecliner.chattalk.NotificationListener.kvStock;
import static com.urrecliner.chattalk.NotificationListener.kvTelegram;
import static com.urrecliner.chattalk.Upload2Google.sheetQues;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioFocusRequest;
import android.os.Environment;
import android.text.SpannableString;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.urrecliner.chattalk.model.AlertLine;
import com.urrecliner.chattalk.Sub.AlertTableIO;
import com.urrecliner.chattalk.model.App;
import com.urrecliner.chattalk.Sub.KeyVal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Vars {
    static File packageDirectory = null;
    public static File tableFolder = null;
    public static File downloadFolder = null;
    static File todayFolder = null;

    public static String toDay = "To";
    public static long timeBegin = 0, timeEnd = 0;

    public static ArrayList<String> appIgnores;
    public static ArrayList<String> appFullNames;
    public static ArrayList<Integer> appNameIdx;
    static String sbnGroup, sbnWho, sbnText, sbnAppName, sbnAppType, sbnAppNick;
    static int sbnAppIdx;

    static App sbnApp;

    static TableListFile tableListFile = null;

    static String[] kGroupWhoIgnores = null;
    static String[] kkTxtIgnores = null;

    static String[] aGroupSaid = null;
    static int [][][] aAlertLineIdx;

    static List<String> aGroups;    // {고선, 텔레, 힐}
    static List<Boolean> aGroupsPass;
    static String[] aGSkip1, aGSkip2, aGSkip3, aGSkip4, aGSkip5, aGSkip6;
    static String[][] aGroupWhos;     // [2] 이진홍, 김선수
    static String[][][] aGroupWhoKey1, aGroupWhoKey2, aGroupWhoSkip, aGroupWhoPrev, aGroupWhoNext;

    static String[] smsWhoIgnores = null;
    static String[] smsTextIgnores = null;
    static String[] systemIgnores = null;
    static String[] textIgnores = null;
    static String[] nineIgnores = null;
    static String[] tossIgnores = null;

    static String[] teleGroups = null;
    static String[] teleChannels = null;
    static String nowFileName;

    static int replGroupCnt = 0;
    static String [] replGroup;
    static String [][] replLong, replShort;

    public static SharedPreferences sharePref;
    static SharedPreferences.Editor sharedEditor;

    @SuppressLint("StaticFieldLeak")
    public static Context mContext = null;
    @SuppressLint("StaticFieldLeak")
    public static Activity mActivity = null;
    @SuppressLint("StaticFieldLeak")
    public static View mLayoutView;

    static final int SHOW_MESSAGE = 1234;
    static final int HIDE_STOP = 5678;
    static final int LOAD_NH_STOCK = 4321;
    static String logQue = "", logSave = "", logStock = "";
    static ActionBar aBar = null;
    static AudioFocusRequest mFocusGain = null;

    /* module list */
    static AlertWhoIndex alertWhoIndex = null;

    static boolean isPhoneBusy = false;

    static AlertsAdapter alertsAdapter = null;
    static TabLayout topTabs = null;
    static ViewPager2 viewPager2 = null;
    static ArrayList<AlertLine> alertLines;

    public static ArrayList<App> apps;
    public static AppAdapter appAdapter;

    static String chatGroup;
    static final String lastChar = "힝";
    public static int alertPos = -1, appPos = -1;  // updated or duplicated recycler position

    enum soundType { PRE, POST, ERR, TESLA, ONLY, STOCK, INFO}
    static final int[] beepRawIds = { R.raw.pre, R.raw.post, R.raw.err,
            R.raw.hi_tesla, R.raw.only, R.raw.stock_check, R.raw.inform};

    public static boolean audioReady = true;
    public static class DelItem {
        String logNow;
        int ps, pf;
        SpannableString ss;
        public DelItem(String logNow, int ps, int pf, SpannableString ss) {
            this.logNow = logNow;
            this.ps = ps; this.pf = pf;
            this.ss = ss;
        }
    }
    public Vars(Context context) {
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
        alertLines = new AlertTableIO().get();
        AlertTable.updateMatched();
        AlertTable.makeArrays();

        kvKakao = new KeyVal();
        kvTelegram = new KeyVal();
        kvCommon = new KeyVal();
        kvSMS = new KeyVal();
        kvStock = new KeyVal();
    }
}