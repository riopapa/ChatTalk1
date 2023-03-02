package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.SubFunc.sounds;
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
import static com.urrecliner.chattalk.Vars.textIgnores;

import android.widget.Toast;

import java.util.ArrayList;

class OptionTables {

    void readAll() {

        kGroupWhoIgnores = tableListFile.read("kGroupWhoIgnores");
        kkTxtIgnores = tableListFile.read("kkTxtIgnores");
        smsWhoIgnores =  tableListFile.read("smsWhoIgnores");
        smsTextIgnores =  tableListFile.read("smsTextIgnores");
        systemIgnores = tableListFile.read("systemIgnores");
        textIgnores = tableListFile.read("textIgnores");
        nineIgnores = tableListFile.read("nineIgnores");
        if (kkTxtIgnores == null || smsWhoIgnores == null || systemIgnores == null) {
            sounds.beepOnce(Vars.soundType.ERR.ordinal());
            Toast.makeText(mContext, "\nsome tables is null\n", Toast.LENGTH_LONG).show();
            sounds.beepOnce(Vars.soundType.ERR.ordinal());
        }
        readReplacesFile();
        readPackageTable();
//        String[] groupIgs =  tableListFile.read("kGroupIgnores");
//        StringBuilder sb = new StringBuilder();
//        for (String groupIg : groupIgs) sb.append("!").append(groupIg).append("!");
//        kGroupWhoIgnores = sb.toString();
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
                new Utils().showSnackBar("Package Table Error ", pLine);
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

    void readReplacesFile() {
        /*
         * 0          1       2         3
         * priority ^ group ^ repl To ^ repl from
         * 20       ^  퍼플  ^ pp1   ^ $매수 하신분들 【 매수 】
         */
        class GrpReplace {
            final String grpName;
            final ArrayList<String> grpLong;
            final ArrayList<String> grpShort;

            GrpReplace(String grpName, ArrayList<String> grpLong, ArrayList<String> grpShort) {
                this.grpName = grpName;
                this.grpLong = grpLong;
                this.grpShort = grpShort;
            }
        }
        ArrayList<GrpReplace> grpReplaces = new ArrayList<>();
        String[] lines = tableListFile.read("strReplaces");
        String svGroup = "";
        ArrayList<String> gLong = new ArrayList<>();
        ArrayList<String> gShort = new ArrayList<>();
        for (String oneLine : lines) {
            String[] ones = oneLine.split("\\^");
            if (ones.length < 3) {
                sounds.beepOnce(Vars.soundType.ERR.ordinal());
                Toast.makeText(mContext, "Caret missing "+oneLine, Toast.LENGTH_LONG).show();
                continue;
            }
            if (!svGroup.equals(ones[0])) {
                if (!svGroup.equals(""))
                    grpReplaces.add(new GrpReplace(svGroup, gLong, gShort));
                svGroup = ones[0];
                gLong = new ArrayList<>();
                gShort = new ArrayList<>();
            }
            gLong.add(ones[2]);
            gShort.add(ones[1]);
        }
        if (gLong.size() > 0)
            grpReplaces.add(new GrpReplace(svGroup, gLong, gShort));

        replGroupCnt = grpReplaces.size();
        replGroup = new String[replGroupCnt];
        replLong = new String[replGroupCnt][];
        replShort = new String[replGroupCnt][];

        for (int i = 0; i < replGroupCnt; i++) {
            GrpReplace grpReplace = grpReplaces.get(i);
            replGroup[i] = grpReplace.grpName;
            String[] sLong = new String[grpReplace.grpLong.size()];
            String[] sShort = new String[grpReplace.grpLong.size()];
            for (int j = 0; j < grpReplace.grpLong.size(); j++) {
                sLong[j] = grpReplace.grpLong.get(j);
                sShort[j] = grpReplace.grpShort.get(j);
            }
            replLong[i] = sLong;
            replShort[i] = sShort;
        }
    }
}