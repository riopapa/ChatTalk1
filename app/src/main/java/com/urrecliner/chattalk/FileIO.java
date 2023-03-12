package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.SubFunc.logUpdate;
import static com.urrecliner.chattalk.Vars.packageDirectory;
import static com.urrecliner.chattalk.Vars.todayFolder;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
                            String text, String key12, String timeStamp) {
        if (text.length() > 120)
            text = text.substring(0, 120);
        Upload2Google.add2Que(group, timeStamp, who, percent, talk, text, key12);
    }

    static void append2Today(String filename, String textLine) {
        final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
        File file = new File(todayFolder, filename);
        String timeInfo = timeFormat.format(new Date()) + " ";
        append2File(file, timeInfo, textLine);
    }

    static void append2File(File file, String timeInfo, String textLine) {
        logUpdate.readyTodayFolderIfNewDay();
        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    String s = "create file Error " + file;
                    new Utils().showSnackBar("append2File", s);
                    Log.e("file " + file, s);
                }
            }
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            bw.write("\n" + timeInfo + textLine + "\n");
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

    static void xwriteKR(File file, String textLine) {
        BufferedWriter bw = null;
        FileWriter fw = null;
        file.delete();
        try {
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            bw.write(textLine + "\n");
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
    String[] readKR(String filename) {
        final int BUFFER_SIZE = 81920;
        String code = "EUC-KR";
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(filename)), code), BUFFER_SIZE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<String> lines = new ArrayList<>();
        String line;
        while (true) {
            try {
                if ((line = bufferedReader.readLine()) == null) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            lines.add(line);
        }
        try {
            bufferedReader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return lines.toArray(new String[0]);
    }


    static void writeKR(File file, String textLine) {
        FileOutputStream fileOutputStream = null;
        BufferedWriter bufferedWriter = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            OutputStreamWriter OutputStreamWriter =
                    new OutputStreamWriter(fileOutputStream, "EUC-KR");
            bufferedWriter = new BufferedWriter(OutputStreamWriter);
            bufferedWriter.write(textLine);
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}