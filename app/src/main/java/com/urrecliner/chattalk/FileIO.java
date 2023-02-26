package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.SubFunc.logQueUpdate;
import static com.urrecliner.chattalk.Vars.packageDirectory;
import static com.urrecliner.chattalk.Vars.todayFolder;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileIO {

    static void readyPackageFolder() {
        if (packageDirectory == null)
            packageDirectory = new File(Environment.getExternalStorageDirectory(), "_ChatTalkLog");

        if (packageDirectory.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                packageDirectory.mkdirs();
            } catch (Exception e) {
                Log.e("Package Folder", packageDirectory.toString() + "_" + e);
            }
        }
    }

    static void uploadStock(String group, String who, String percent, String talk,
                            String text,    String key12, String timeStamp) {
        Upload2Google.add2Que(group, timeStamp, who, percent, talk, text, key12);
    }

    static void append2Today(String filename, String textLine) {
        final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
        File file = new File(todayFolder, filename);
        String timeInfo = timeFormat.format(new Date())+" ";
        append2File (file, timeInfo, textLine);
    }

    static void append2File(File file, String timeInfo, String textLine) {
        logQueUpdate.readyTodayFolderIfNewDay();
        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    String s = "create file Error "+file;
                    new Utils().showSnackBar("append2File",s);
                    Log.e("file "+file,s);
                }
            }
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            bw.write("\n" + timeInfo + textLine+"\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) bw.close();
                if (fw != null) fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static void writeTextFile(File targetFolder, String fileName, String outText) {

        try {
            File targetFile = new File(targetFolder, fileName + ".txt");
            FileWriter fileWriter = new FileWriter(targetFile, false);

            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(outText);
            bufferedWriter.close();
        } catch (IOException ex) {
            new Utils().logE("editor", fileName + "'\n" + ex);
            new Utils().showSnackBar("writeTextFile", "Write table error " + fileName);
        }
    }
}