package com.urrecliner.chattalk;

import static android.content.Context.MODE_PRIVATE;
import static com.urrecliner.chattalk.Upload2Google.sheetQues;
import static com.urrecliner.chattalk.Vars.alertIndex;
import static com.urrecliner.chattalk.Vars.downloadFolder;
import static com.urrecliner.chattalk.Vars.logQueUpdate;
import static com.urrecliner.chattalk.Vars.mActivity;
import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.Vars.mLayoutView;
import static com.urrecliner.chattalk.Vars.msgAndroid;
import static com.urrecliner.chattalk.Vars.packageDirectory;
import static com.urrecliner.chattalk.Vars.sharePref;
import static com.urrecliner.chattalk.Vars.sharedEditor;
import static com.urrecliner.chattalk.Vars.sharedFinish;
import static com.urrecliner.chattalk.Vars.sharedStart;
import static com.urrecliner.chattalk.Vars.sounds;
import static com.urrecliner.chattalk.Vars.tableFolder;
import static com.urrecliner.chattalk.Vars.tableListFile;
import static com.urrecliner.chattalk.Vars.utils;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

public class VarInit {

    void set(String msg) {

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
        utils.logW("VarInit", "/ / "+msg+" / / \n");
        if (sharedStart == 0)
            utils.setTimeBoundary();

        sheetQues = new ArrayList<>();
        AlertTable.readFile();
        AlertTable.makeArrays();
//        utils.showSnackBar("sayMessage", msg);

//        new SlowSwipe().set();
    }

}