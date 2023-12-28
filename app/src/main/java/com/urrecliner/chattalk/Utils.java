package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.NotificationListener.sounds;
import static com.urrecliner.chattalk.Vars.packageDirectory;
import static com.urrecliner.chattalk.Vars.replGroup;
import static com.urrecliner.chattalk.Vars.replGroupCnt;
import static com.urrecliner.chattalk.Vars.replLong;
import static com.urrecliner.chattalk.Vars.replShort;
import static com.urrecliner.chattalk.Vars.toDay;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.Locale;

class Utils {

    public Utils() {
        if (packageDirectory == null)
            packageDirectory = new File(Environment.getExternalStorageDirectory(), "_ChatTalkLog");
    }
    /*
        logW writes log on that day, and removed after a few days
        logE writes to download folder, removing by manual operation
     */
    void logW(String tag, String text) {
        new ReadyToday();
        StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        String logText  =(traces.length>6) ? excludeName(traces[6].getMethodName()):"";
        logText += excludeName(traces[5].getMethodName()) + excludeName(traces[4].getMethodName()) + excludeName(traceClassName(traces[3].getClassName()))+"> "+traces[3].getMethodName() + "#" + traces[3].getLineNumber() + " {"+ tag + "} " + text;
        Log.w(tag, logText);
        FileIO.append2Today("zLog"+toDay+tag+".txt", logText);
    }

    void logE(String tag, String text) {
        StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        String logText  =(traces.length>6) ? excludeName(traces[6].getMethodName()):"";
        logText += excludeName(traces[5].getMethodName()) + excludeName(traces[4].getMethodName()) + excludeName(traceClassName(traces[3].getClassName()))+"> "+traces[3].getMethodName() + "#" + traces[3].getLineNumber() + " {"+ tag + "} " + text;
        Log.e("<" + tag + ">" , logText);
        FileIO.append2File(new File(packageDirectory, "zChatTalk.txt"), tag, logText);
        sounds.beepOnce(Vars.soundType.ERR.ordinal());   // error sound
    }

    private String excludeName(String s) {
        String [] omits = { "performResume", "performCreate", "callActivityOnResume", "access$",
                "onNotificationPosted", "NotificationListener", "performCreate", "log",
                "handleReceiver", "handleMessage", "dispatchKeyEvent", "onBindViewHolder"};
        for (String o : omits) {
            if (s.contains(o)) return ". ";
        }
        return s + "> ";
    }
    private String traceClassName(String s) {
        return s.substring(s.lastIndexOf(".")+1);
    }


    /* delete old packageDirectory / files if storage is less than x days */
    void deleteOldFiles() {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd", Locale.KOREA);
        String weekAgo = dateFormat.format(System.currentTimeMillis() - 3*24*60*60*1000L);
        File[] files = packageDirectory.listFiles();
        if (files == null)
            return;
        Collator myCollator = Collator.getInstance();
        for (File file : files) {
            String shortFileName = file.getName();
            if (myCollator.compare(shortFileName, weekAgo) < 0) {
                deleteFolder(file);
            }
        }
    }

    private void deleteFolder(File file) {
        String deleteCmd = "rm -r " + file.toString();
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(deleteCmd);
        } catch (IOException e) {
            //
        }
    }

    public String removeSpecialChars(String text) {
        return text.replace("──", "").replace("==", "-")
                .replace("=", "ￚ").replace("--", "-")
        .replaceAll("[^\\da-zA-Z:|#().@,%/~ㄱ-ㅎ가-힣\\s\\-+]", "")
        ;
    }

    public String strShorten(String groupOrWho, String text) {
        for (int i = 0; i < replGroupCnt; i++) {
            int compared = groupOrWho.compareTo(replGroup[i]);
            if (compared == 0) {
                for (int j = 0; j < replLong[i].length; j++)
                    text = text.replace(replLong[i][j], replShort[i][j]);
                return text;
            }
            if (compared < 0) {
                return text;
            }
        }
        return text;
    }

    String makeEtc (String s, int len) {
        return (s.length() < len)? s : s.substring(0, len) + " ˚ 등등";
    }

    String replaceKKHH(String text) {
        return text.replace("ㅇㅋ", " 오케이 ")
                .replace("ㅋㅋ", " 크 ")
                .replace("ㅋ", " 크 ")
                .replace("^^", " 크 ")
                .replace("ㅊㅋ", " 축하 ")
                .replace("ㅠㅠ", " 흑 ")
                .replace("ㅠ", " 흑 ")
                .replace("ㅎㅎ", " 하 ")
                .replace("ㅎ", " 하 ");
    }

    String text2OneLine(String mText) {
        return mText.replace("\n", "|").replace("\r", "").replace("||", "|").replace("||", "|");
    }
}