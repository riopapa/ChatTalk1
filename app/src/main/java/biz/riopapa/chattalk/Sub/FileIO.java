package biz.riopapa.chattalk.Sub;

import static biz.riopapa.chattalk.Vars.packageDirectory;
import static biz.riopapa.chattalk.Vars.toDay;
import static biz.riopapa.chattalk.Vars.todayFolder;

import android.os.Environment;
import android.util.Log;

import biz.riopapa.chattalk.ReadyToday;
import biz.riopapa.chattalk.Sub.SnackBar;
import biz.riopapa.chattalk.Upload2Google;
import biz.riopapa.chattalk.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
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

    public static void readyPackageFolder() {
        if (packageDirectory == null)
            packageDirectory = new File(Environment.getExternalStorageDirectory(), "_ChatTalkLog");

        if (!packageDirectory.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                packageDirectory.mkdirs();
            } catch (Exception e) {
                Log.e("Exception", "Package Folder "+ packageDirectory.toString() + "_" + e);
            }
        }
    }

    public static void uploadStock(String group, String who, String percent, String talk,
                                   String text, String key12, String timeStamp) {
        if (text.length() > 120)
            text = text.substring(0, 120);
        Upload2Google.add2Que(group, timeStamp, who, percent, talk, text, key12);
    }

    public static void append2Today(String filename, String textLine) {
        if (todayFolder == null) {
            todayFolder = new File(packageDirectory, toDay);
        }
        final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
        File file = new File(todayFolder, filename);
        String timeInfo = timeFormat.format(new Date()) + " ";
        append2File(file, timeInfo, textLine);
    }

    public static void append2File(File file, String timeInfo, String textLine) {
        new ReadyToday();
        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    String s = "create file Error " + file;
                    new SnackBar().show("append2File", s);
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

    public static void writeFile(File targetFolder, String fileName, String outText) {
        try {
            File targetFile = new File(targetFolder, fileName);
            FileWriter fileWriter = new FileWriter(targetFile, false);

            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(outText);
            bufferedWriter.close();
        } catch (IOException ex) {
            new Utils().logE("editor", fileName + "'\n" + ex);
            new SnackBar().show("writeTextFile", "Write table error " + fileName);
        }
    }

    public static String readFile(File targetFolder, String fileName) {
        File jFile = new File(targetFolder, fileName);
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fis = new FileInputStream(jFile);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                sb.append(strLine);
//                myData = myData + strLine;
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String[] readKR(String filename) {
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

    public static String readKRFile(String filename) {

        String [] lines = readKR(filename);
        StringBuilder sb = new StringBuilder();
        for (String s: lines) {
            sb.append(s);
        }
        return sb.toString();
    }


    public static void writeKR(File file, String textLine) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            OutputStreamWriter OutputStreamWriter =
                    new OutputStreamWriter(fileOutputStream, "EUC-KR");
            BufferedWriter bufferedWriter = new BufferedWriter(OutputStreamWriter);
            bufferedWriter.write(textLine);
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}