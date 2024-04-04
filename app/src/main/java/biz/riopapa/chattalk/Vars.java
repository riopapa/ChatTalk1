package biz.riopapa.chattalk;

import static android.content.Context.MODE_PRIVATE;
import static biz.riopapa.chattalk.NotificationListener.kvCommon;
import static biz.riopapa.chattalk.NotificationListener.kvKakao;
import static biz.riopapa.chattalk.NotificationListener.kvSMS;
import static biz.riopapa.chattalk.NotificationListener.kvStock;
import static biz.riopapa.chattalk.NotificationListener.kvTelegram;
import static biz.riopapa.chattalk.Upload2Google.sheetQues;

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
import biz.riopapa.chattalk.model.AlertLine;
import biz.riopapa.chattalk.alerts.AlertTableIO;
import biz.riopapa.chattalk.model.App;
import biz.riopapa.chattalk.Sub.KeyVal;

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

    static String[] ktGroupIgnores = null;
    static String[] ktWhoIgnores = null;

    static String[] aGroupSaid = null;
    static int [][][] aAlertLineIdx;

    static List<String> aGroups;    // {고선, 텔레, 힐}
    static List<Boolean> aGroupsPass;
    static String[] aGSkip1, aGSkip2, aGSkip3, aGSkip4;
    static String[][] aGroupWhos;     // [2] 이진홍, 김선수
    static String[][][] aGroupWhoKey1, aGroupWhoKey2, aGroupWhoSkip, aGroupWhoPrev, aGroupWhoNext;

    static String[] smsWhoIgnores = null;
    static String[] smsTxtIgnores = null;
    static String[] smsNoNumbers = null;
    static String[] smsReplFrom = null;
    static String[] smsReplTo = null;

    static String[] ktNoNumbers = null;
    static String[] ktTxtIgnores = null;

    static String[] teleGroups = null;
    static String[] teleChannels = null;

    static String[] whoNameFrom = null;
    static String[] whoNameTo = null;

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
    static final int STOP_SAY1 = 10011;
    static final int RELOAD_APP = 2022;

    static String logQue = "", logSave = "", logStock = "", logWork = "";
    static ActionBar aBar = null;
    static AudioFocusRequest mFocusGain = null;

    /* module list */
    static AlertWhoIndex alertWhoIndex = null;

    static boolean isPhoneBusy = false;

    public static AlertsAdapter alertsAdapter = null;
    public static TabLayout topTabs = null;
    public static ViewPager2 viewPager2 = null;
    public static ArrayList<AlertLine> alertLines;

    public static ArrayList<App> apps;
    public static AppAdapter appAdapter;

    static String chatGroup;
    static final String lastChar = "힝";
    public static int alertPos = -1, appPos = -1;  // updated or duplicated recycler position

    enum soundType { PRE, POST, ERR, HI_TESLA, ONLY, STOCK, INFO, KAKAO}
    static final int[] beepRawIds = { R.raw.pre, R.raw.post, R.raw.err,
            R.raw.hi_tesla, R.raw.only, R.raw.stock_check, R.raw.inform, R.raw.kakao_talk};

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
        new AlertTableIO().get();

        kvKakao = new KeyVal();
        kvTelegram = new KeyVal();
        kvCommon = new KeyVal();
        kvSMS = new KeyVal();
        kvStock = new KeyVal();
    }
}