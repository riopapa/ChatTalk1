package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.NotificationListener.sounds;
import static com.urrecliner.chattalk.Vars.kGroupWhoIgnores;
import static com.urrecliner.chattalk.Vars.kkTxtIgnores;
import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.Vars.nineIgnores;
import static com.urrecliner.chattalk.Vars.packageIgnores;
import static com.urrecliner.chattalk.Vars.pkgFullNames;
import static com.urrecliner.chattalk.Vars.pkgNickNames;
import static com.urrecliner.chattalk.Vars.pkgTypes;
import static com.urrecliner.chattalk.Vars.replGroup;
import static com.urrecliner.chattalk.Vars.replGroupCnt;
import static com.urrecliner.chattalk.Vars.replLong;
import static com.urrecliner.chattalk.Vars.replShort;
import static com.urrecliner.chattalk.Vars.smsTextIgnores;
import static com.urrecliner.chattalk.Vars.smsWhoIgnores;
import static com.urrecliner.chattalk.Vars.systemIgnores;
import static com.urrecliner.chattalk.Vars.tableListFile;
import static com.urrecliner.chattalk.Vars.teleChannels;
import static com.urrecliner.chattalk.Vars.teleGroups;
import static com.urrecliner.chattalk.Vars.textIgnores;
import static com.urrecliner.chattalk.Vars.tossIgnores;

import android.widget.Toast;

import com.urrecliner.chattalk.Sub.SnackBar;

import java.util.ArrayList;

class OptionTables {

    void readAll() {

        if (tableListFile == null)
            tableListFile = new TableListFile();
        kGroupWhoIgnores = tableListFile.read("kGroupWhoIgnores");
        kkTxtIgnores = tableListFile.read("kkTxtIgnores");
        smsWhoIgnores =  tableListFile.read("smsWhoIgnores");
        smsTextIgnores =  tableListFile.read("smsTextIgnores");
        systemIgnores = tableListFile.read("systemIgnores");
        textIgnores = tableListFile.read("textIgnores");
        nineIgnores = tableListFile.read("nineIgnores");
        tossIgnores = tableListFile.read("tossIgnores");
        if (kkTxtIgnores == null || smsWhoIgnores == null || systemIgnores == null) {
            sounds.beepOnce(Vars.soundType.ERR.ordinal());
            Toast.makeText(mContext, "\nsome tables is null\n", Toast.LENGTH_LONG).show();
//            sounds.beepOnce(Vars.soundType.ERR.ordinal());
        }
        readReplacesFile();
        readPackageTable();
        readTelegramTable();

    }

    private void readPackageTable() {
        /*
         * full package name       ^ nickName^ type ^ comment
         * com.kakao.talk          ^   카톡   ^  kk  ^
         */
        String [] packages =  tableListFile.read("packageNames");
        String [] strings;

        /* separate ignoring package vs target package */
        pkgFullNames = new ArrayList<>();
        pkgNickNames = new ArrayList<>();
        pkgTypes = new ArrayList<>();
        packageIgnores = new ArrayList<>();

        for (String pLine:packages) {
            strings = pLine.split("\\^");
            if (strings.length < 3) {
                new SnackBar().show("Package Table Error ", pLine);
            } else {
                String fullName = strings[0].trim();
                String nickName = strings[1].trim();
                if (nickName.equals("@")) {
                    packageIgnores.add(fullName);
                } else {
                    String type = strings[2].trim();
                    pkgFullNames.add(fullName);
                    pkgNickNames.add(nickName);
                    pkgTypes.add(type);
                }
            }
        }
    }

    private void readTelegramTable() {
        /*
         * group ^ channel name
         * 부자   ^ 부자 프로젝트
         */
        String [] lists =  tableListFile.read("teleGroup");

        teleGroups = new String[lists.length];
        teleChannels = new String[lists.length];

        for (int i = 0; i < lists.length; i++) {
            String pLine = lists[i];
            String [] strings = pLine.split("\\^");
            if (strings.length < 2) {
                new SnackBar().show("Telegram Table Error ", pLine);
            } else {
                teleGroups[i] = strings[0].trim();
                teleChannels[i] = strings[1].trim();
            }
        }
    }

    void readReplacesFile() {
        /*
         * 0          1       2         3
         * priority ^ group ^ repl To ^ repl from
         * 20       ^  퍼플  ^ pp1   ^ $매수 하신분들 【 매수 】
         */
        class StrLong2Short {
            final String grpName;
            final ArrayList<String> grpLong;
            final ArrayList<String> grpShort;

            StrLong2Short(String grpName, ArrayList<String> grpLong, ArrayList<String> grpShort) {
                this.grpName = grpName;
                this.grpLong = grpLong;
                this.grpShort = grpShort;
            }
        }
        ArrayList<StrLong2Short> strLong2Shorts = new ArrayList<>();
        String[] lines = tableListFile.read("strReplaces");
        String svGroup = "";
        ArrayList<String> gLong = new ArrayList<>();
        ArrayList<String> gShort = new ArrayList<>();
        String prvLine = "";
        for (String oneLine : lines) {
            String[] ones = oneLine.split("\\^");
            if (ones.length < 3) {
                if (sounds == null)
                    sounds = new Sounds();
                sounds.beepOnce(Vars.soundType.ERR.ordinal());
                Toast.makeText(mContext,"Caret missing : "+oneLine+"\nprv line : "+prvLine,
                        Toast.LENGTH_LONG).show();
                continue;
            }
            if (!svGroup.equals(ones[0])) {
                if (!svGroup.equals(""))
                    strLong2Shorts.add(new StrLong2Short(svGroup, gLong, gShort));
                svGroup = ones[0];
                gLong = new ArrayList<>();
                gShort = new ArrayList<>();
            }
            gLong.add(ones[2]);
            gShort.add(ones[1]);
            prvLine = oneLine;
        }
        if (gLong.size() > 0)
            strLong2Shorts.add(new StrLong2Short(svGroup, gLong, gShort));

        replGroupCnt = strLong2Shorts.size();
        replGroup = new String[replGroupCnt];
        replLong = new String[replGroupCnt][];
        replShort = new String[replGroupCnt][];

        for (int i = 0; i < replGroupCnt; i++) {
            StrLong2Short strLong2Short = strLong2Shorts.get(i);
            replGroup[i] = strLong2Short.grpName;
            String[] sLong = new String[strLong2Short.grpLong.size()];
            String[] sShort = new String[strLong2Short.grpLong.size()];
            for (int j = 0; j < strLong2Short.grpLong.size(); j++) {
                sLong[j] = strLong2Short.grpLong.get(j);
                sShort[j] = strLong2Short.grpShort.get(j);
            }
            replLong[i] = sLong;
            replShort[i] = sShort;
        }
    }
}