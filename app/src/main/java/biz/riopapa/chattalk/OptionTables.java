package biz.riopapa.chattalk;

import static biz.riopapa.chattalk.NotificationListener.sounds;
import static biz.riopapa.chattalk.NotificationListener.utils;
import static biz.riopapa.chattalk.Vars.ktGroupIgnores;
import static biz.riopapa.chattalk.Vars.ktWhoIgnores;
import static biz.riopapa.chattalk.Vars.ktNoNumbers;
import static biz.riopapa.chattalk.Vars.ktTxtIgnores;
import static biz.riopapa.chattalk.Vars.mContext;
import static biz.riopapa.chattalk.Vars.replGroup;
import static biz.riopapa.chattalk.Vars.replGroupCnt;
import static biz.riopapa.chattalk.Vars.replLong;
import static biz.riopapa.chattalk.Vars.replShort;
import static biz.riopapa.chattalk.Vars.smsNoNumbers;
import static biz.riopapa.chattalk.Vars.smsReplFrom;
import static biz.riopapa.chattalk.Vars.smsReplTo;
import static biz.riopapa.chattalk.Vars.smsTxtIgnores;
import static biz.riopapa.chattalk.Vars.smsWhoIgnores;
import static biz.riopapa.chattalk.Vars.tableListFile;
import static biz.riopapa.chattalk.Vars.longGroupNames;
import static biz.riopapa.chattalk.Vars.shortGroupNames;
import static biz.riopapa.chattalk.Vars.whoNameFrom;
import static biz.riopapa.chattalk.Vars.whoNameTo;

import android.widget.Toast;

import biz.riopapa.chattalk.Sub.AppsTable;
import biz.riopapa.chattalk.Sub.SnackBar;

import java.util.ArrayList;

class OptionTables {

    void readAll() {

        if (tableListFile == null)
            tableListFile = new TableListFile();
        ktGroupIgnores = tableListFile.read("ktGrpIg");
        ktWhoIgnores = tableListFile.read("ktWhoIg");
        ktTxtIgnores = tableListFile.read("ktTxtIg");
        ktNoNumbers = tableListFile.read("ktNoNum");

        smsWhoIgnores =  tableListFile.read("smsWhoIg");
        smsTxtIgnores =  tableListFile.read("smsTxtIg");
        smsNoNumbers = tableListFile.read("smsNoNum");
        smsNoNumbers = tableListFile.read("smsNoNum");

        if (ktTxtIgnores == null || smsWhoIgnores == null) {
            sounds.beepOnce(Vars.soundType.ERR.ordinal());
            String s = "ktTxtIgnores || smsWhoIgnores is null";
            Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
            utils.logW("readAll",s);
        }
        readStrReplFile();
        readSmsReplFile();
        readTelegramGroup();
        readWhoName();
        new AppsTable().get();

    }

    private void readTelegramGroup() {
        /*
         * group ^ channel name
         * 부자   ^ 부자 프로젝트
         */
        String [] lists =  tableListFile.read("teleGrp");

        shortGroupNames = new String[lists.length];
        longGroupNames = new String[lists.length];

        for (int i = 0; i < lists.length; i++) {
            String pLine = lists[i];
            String [] strings = pLine.split("\\^");
            if (strings.length < 2) {
                new SnackBar().show("Telegram Table Error ", pLine);
            } else {
                shortGroupNames[i] = strings[0].trim();
                longGroupNames[i] = strings[1].trim();
            }
        }
    }

    private void readWhoName() {
        /*
         * group ^ channel name
         * 부자   ^ 부자 프로젝트
         */
        String [] lists =  tableListFile.read("whoName");

        whoNameFrom = new String[lists.length];
        whoNameTo = new String[lists.length];

        for (int i = 0; i < lists.length; i++) {
            String pLine = lists[i];
            String [] strings = pLine.split("\\^");
            if (strings.length < 2) {
                new SnackBar().show("Who Name Table Error ", pLine);
            } else {
                whoNameFrom[i] = strings[0].trim();
                whoNameTo[i] = strings[1].trim();
            }
        }
    }

    void readSmsReplFile() {
        /*
         * 0   ^  1
         * 짧은 ^ 아주 긴 문장 ; comment
         */
        String[] lines = tableListFile.read("smsRepl");
        ArrayList<String> sShort = new ArrayList<>();
        ArrayList<String> sLong = new ArrayList<>();
        for (String oneLine : lines) {
            if (!oneLine.isEmpty()) {
                String[] ones = oneLine.split("\\^");
                if (ones.length < 2) {
                    if (sounds == null)
                        sounds = new Sounds();
                    sounds.beepOnce(Vars.soundType.ERR.ordinal());
                    String s = "SMS Repl ^^ Error : " + oneLine;
                    Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
                    utils.logW("readSMS", s);
                } else {
                    sShort.add(ones[0].trim());
                    sLong.add(ones[1].trim());
                }
            }
        }
        if (!sShort.isEmpty()) {
            smsReplFrom = new String[sShort.size()];
            smsReplTo = new String[sShort.size()];
            for (int i = 0; i < sShort.size(); i++) {
                smsReplFrom[i] = sLong.get(i);
                smsReplTo[i] = sShort.get(i);
            }
        }
    }

    void readStrReplFile() {
        /*
         * 0          1       2         3
         * group ^ repl To ^ repl from
         * 퍼플  ^ pp1   ^ $매수 하신분들 【 매수 】
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
        String[] lines = tableListFile.read("strRepl");
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
                String s = "StrRepl Caret missing : "+oneLine+"\nprv line : "+prvLine;
                Toast.makeText(mContext,s,
                        Toast.LENGTH_LONG).show();
                utils.logW("strRead", s);
                continue;
            }
            if (!svGroup.equals(ones[0])) {
                if (!svGroup.isEmpty())
                    strLong2Shorts.add(new StrLong2Short(svGroup, gLong, gShort));
                svGroup = ones[0];
                gShort = new ArrayList<>();
                gLong = new ArrayList<>();
            }
            gShort.add(ones[1]);
            gLong.add(ones[2]);
            prvLine = oneLine;
        }
        if (!gLong.isEmpty())
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