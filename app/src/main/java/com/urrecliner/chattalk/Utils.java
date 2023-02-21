package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.SubFunc.logQueUpdate;
import static com.urrecliner.chattalk.SubFunc.sounds;
import static com.urrecliner.chattalk.Vars.mActivity;
import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.Vars.mLayoutView;
import static com.urrecliner.chattalk.Vars.packageDirectory;
import static com.urrecliner.chattalk.Vars.replGroup;
import static com.urrecliner.chattalk.Vars.replGroupCnt;
import static com.urrecliner.chattalk.Vars.replLong;
import static com.urrecliner.chattalk.Vars.replShort;
import static com.urrecliner.chattalk.Vars.toDay;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.Locale;

class Utils {

    /*
        logW writes log on that day, and removed after a few days
        logE writes to download folder, removing by manual operation
     */
    void logW(String tag, String text) {
        logQueUpdate.readyTodayFolderIfNewDay();
        StackTraceElement[] traces;
        traces = Thread.currentThread().getStackTrace();
        String logText = excludeName(traces[5].getMethodName()) + excludeName(traces[4].getMethodName()) + excludeName(traceClassName(traces[3].getClassName()))+"> "+traces[3].getMethodName() + "#" + traces[3].getLineNumber() + " {"+ tag + "} " + text;
        Log.w(tag, logText);
        FileIO.append2Today("zLog"+toDay+tag+".txt", logText);
    }

    void logE(String tag, String text) {
        StackTraceElement[] traces;
        traces = Thread.currentThread().getStackTrace();
        String logText = excludeName(traces[5].getMethodName()) + excludeName(traces[4].getMethodName()) + traceClassName(traces[3].getClassName())+"> "+traces[3].getMethodName() + "#" + traces[3].getLineNumber() + " |err:"+ tag + "| " + text;
        Log.e("<" + tag + ">" , logText);
        FileIO.append2File(new File(packageDirectory, "zChatTalk.txt"), tag, logText);
        sounds.beepOnce(Vars.soundType.ERR.ordinal());   // error sound
    }

    private String excludeName(String s) {
        String [] omits = { "performResume", "performCreate", "callActivityOnResume", "access$",
                "onCreate", "onNotificationPosted", "NotificationListener", "performCreate", "log",
                "handleReceiver", "handleMessage", "dispatchKeyEvent", "onBindViewHolder"};
        for (String o : omits) {
            if (s.contains(o)) return ". ";
        }
        return s + "> ";
    }
    private String traceClassName(String s) {
        return s.substring(s.lastIndexOf(".")+1);
    }

    void showToast(String text) {    // 0: short 1:long
        Toast toast = Toast.makeText(mContext,"\n"+text+"\n", Toast.LENGTH_LONG);
        toast.show();
    }

    void showSnackBar(String title, String text) {
        Snackbar snackbar = Snackbar.make(mLayoutView, "", Snackbar.LENGTH_SHORT);
        View sView = mActivity.getLayoutInflater().inflate(R.layout.snack_message, null);

        TextView tv1 = sView.findViewById(R.id.text_header);
        TextView tv2 = sView.findViewById(R.id.text_body);

        tv1.setText(title);
        tv2.setText(text);

        // now change the layout of the Snackbar
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
//        snackbarLayout.setPadding(8, 8, 8, 8);
        snackbarLayout.setBackgroundColor(0x00FFFFFF);  // remove background
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)snackbarLayout.getLayoutParams();
        params.gravity = Gravity.CENTER_VERTICAL;
        sView.setLayoutParams(params);
        // register the button from the custom_snackbar_view layout file

        // add the custom snack bar layout to snackbar layout
        snackbarLayout.addView(sView, 0);

        snackbar.show();
    }

    /* delete old packageDirectory / files if storage is less than x days */
    void deleteOldFiles() {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd", Locale.KOREA);
        String weekAgo = dateFormat.format(System.currentTimeMillis() - 5*24*60*60*1000L);
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

    String removeSpecialChars(String text) {
        return text.replace("──", "").replace("==", "-")
                .replace("=", "ￚ").replace("--", "-")
        .replaceAll("[^0-9a-zA-Z:|#().@,%/~ㄱ-ㅎ가-힣\\s\\-]+", "")
        ;
    }

    String strReplace(String group, String text) {
        for (int i = 0; i < replGroupCnt; i++) {
            int compared = group.compareTo(replGroup[i]);
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
                .replace("ㅋ", " 크 ")
                .replace("^^", " 흐 ")
                .replace("ㅎ", " 하 ");
    }

    String text2OneLine(String mText) {
        return mText.replace("\n", "|").replace("\r", "").replace("||", "|").replace("||", "|");
    }
}