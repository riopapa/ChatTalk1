package com.urrecliner.chattalk.Sub;

import static com.urrecliner.chattalk.Vars.appNameIdx;
import static com.urrecliner.chattalk.Vars.apps;
import static com.urrecliner.chattalk.Vars.downloadFolder;
import static com.urrecliner.chattalk.Vars.appIgnores;
import static com.urrecliner.chattalk.Vars.appFullNames;
import static com.urrecliner.chattalk.Vars.tableFolder;

import android.content.Context;
import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.urrecliner.chattalk.FileIO;
import com.urrecliner.chattalk.TableListFile;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AppsTable {

    public ArrayList<App> get() {
        if (tableFolder ==  null) {
            downloadFolder = new File(Environment.getExternalStorageDirectory(), "download");
            tableFolder = new File(downloadFolder, "_ChatTalk");
        }

        ArrayList<App> list;
        Gson gson = new Gson();
        String json = FileIO.readKRFile(new File(tableFolder ,"appTable.json").toString());
        if (json.isEmpty()) {
            list = readPackageTable();

        } else {
            Type type = new TypeToken<List<App>>() {
            }.getType();
            list = gson.fromJson(json, type);
        }
        makeTable(list);
        return list;
    }

    public void put(Context context) {
        if (tableFolder ==  null) {
            downloadFolder = new File(Environment.getExternalStorageDirectory(), "download");
            tableFolder = new File(downloadFolder, "_ChatTalk");
        }

        apps.sort(Comparator.comparing(obj -> (obj.fullName)));

        Gson gson = new Gson();
        String json = gson.toJson(apps);

        FileIO.writeKR(new File(tableFolder, "appTable.json"), json);
    }

    public void makeTable(ArrayList<App> list) {
        appFullNames = new ArrayList<>();
        appNameIdx = new ArrayList<>();
        appIgnores = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            App app = list.get(i);
            if (app.nickName.equals("@")) {
                appIgnores.add(app.fullName);
            } else {
                appFullNames.add(app.fullName);
                appNameIdx.add(i);
            }
        }

    }

    private ArrayList<App> readPackageTable() {
        /*
         * full package name       ^ nickName^ type ^ comment
         * com.kakao.talk          ^   카톡   ^  kk  ^
         */
        ArrayList<App> list = new ArrayList<>();

        String [] packages =  new TableListFile().read("appNames");
        String [] strings;


        for (String pLine:packages) {
            App app = new App();
            strings = pLine.split("\\^");
            if (strings.length < 3) {
                new SnackBar().show("Package Table Error ", pLine);
            } else {
                app.fullName = strings[0].trim();
                app.nickName = strings[1].trim();
                String type = strings[2].trim() + "   ";
                app.memo = strings[3].trim();
                app.say = true;
                app.log = !type.contains("yx");
                app.grp = true;
                app.who = true;
                app.addWho = false;
                app.num = true;
                if (type.contains("ynn")) {
                    app.who = false;
                } else if (type.contains("yyx")) {
                    app.grp = false;
                } else if (type.contains("ynx")) {
                    app.who = false;
                } else if (type.contains("ywx")) {
                    app.addWho = true;
                }
                list.add(app);
            }
        }
        return list;
    }

}