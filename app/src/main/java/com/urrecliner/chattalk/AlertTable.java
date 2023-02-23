package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.SubFunc.sounds;
import static com.urrecliner.chattalk.SubFunc.utils;
import static com.urrecliner.chattalk.Vars.aAlertLineIdx;
import static com.urrecliner.chattalk.Vars.aGSkip1;
import static com.urrecliner.chattalk.Vars.aGSkip2;
import static com.urrecliner.chattalk.Vars.aGSkip3;
import static com.urrecliner.chattalk.Vars.aGSkip4;
import static com.urrecliner.chattalk.Vars.aGroupSaid;
import static com.urrecliner.chattalk.Vars.aGroupWhoKey1;
import static com.urrecliner.chattalk.Vars.aGroupWhoKey2;
import static com.urrecliner.chattalk.Vars.aGroupWhoNext;
import static com.urrecliner.chattalk.Vars.aGroupWhoPrev;
import static com.urrecliner.chattalk.Vars.aGroupWhoSkip;
import static com.urrecliner.chattalk.Vars.aGroupWhos;
import static com.urrecliner.chattalk.Vars.aGroups;
import static com.urrecliner.chattalk.Vars.alertLines;
import static com.urrecliner.chattalk.Vars.tableFolder;
import static com.urrecliner.chattalk.Vars.tableListFile;
import static com.urrecliner.chattalk.Vars.todayFolder;

import com.urrecliner.chattalk.Sub.AlertLine;
import com.urrecliner.chattalk.Sub.ByteLength;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class AlertTable {

//   group^ group name  ^     skip1  ^  skip2    ^ skip3 ^  -1  ^ skip4    ^ sayMore
//    고선 ^ VIP리딩방 CA ^     !!     ^     해외  ^  BTC  ^       ^    0%    ^ 개장전

//   group^  who        ^  keyword1 ^ keyword2 ^ talk ^ count ^ skip     ^ memo ^ prev ^ next
//    고선 ^ 고선생       ^    매수    ^   목표가  ^      ^  101  ^          ^ 01/11

    static void readFile() {

        String[] lines = tableListFile.read("kTalkAlerts");

        alertLines = new ArrayList<>();

        for (String line : lines) {
            String[] strings = (line+" ").split("\\^");
            if (strings.length < 10) {
                String s = "Caret for Alert missing, "+line;
                utils.showSnackBar("kTalkAlerts", s);
                utils.logW("Alert Table "," Error "+line);
                sounds.speakAfterBeep(s);
                sounds.beepOnce(Vars.soundType.ERR.ordinal());
            }
            String tGroup = strings[0].trim();
            String tWho = strings[1].trim();
            String tKey1 = strings[2].trim();
            String tKey2 = strings[3].trim();    // leave spaces between two ^^
            String tTalk = strings[4].trim();
            int matched = Integer.parseInt(strings[5].trim());
            String tSkip = strings[6].trim();
            String [] tMemos = strings[7].split("~");
            String tMemo = tMemos[0].trim();
            String tMore = (tMemos.length>1) ? tMemos[1].trim():"";
            String prev = strings[8].trim(); if (prev.equals("")) prev = tKey1;
            String next = strings[9].trim(); if (next.equals("")) next = tKey2;
            alertLines.add(new AlertLine(tGroup, tWho, tKey1, tKey2, tTalk, matched, tSkip, tMemo, tMore, prev, next));
        }
    }

    static ArrayList<String> gSkip1 = new ArrayList<>();
    static ArrayList<String> gSkip2 = new ArrayList<>();
    static ArrayList<String> gSkip3 = new ArrayList<>();
    static ArrayList<String> gSkip4 = new ArrayList<>();
    static List<String> key1 = new ArrayList<>();
    static List<String> key2 = new ArrayList<>();
    static List<String> skip = new ArrayList<>();
    static List<String> prev = new ArrayList<>();
    static List<String> next = new ArrayList<>();
    static int gIdx, gwIdx, svIdx;

    static void makeArrays() {

        String svGroup = "", svWho = "";
        String none = "nOnE";
        int alertSize = alertLines.size();

        aGroups = new ArrayList<>();
        for (AlertLine al: alertLines) {
            if (!svGroup.equals(al.group)) {
                aGroups.add(al.group);
                gSkip1.add(al.key1.equals("") ? none : al.key1);
                gSkip2.add(al.key2.equals("") ? none : al.key2);
                gSkip3.add(al.talk.equals("") ? none : al.talk);
                gSkip4.add(al.skip.equals("") ? none : al.skip);
                svGroup = al.group;
            }
        }
        int groupCnt = aGroups.size();
        aGSkip1 = gSkip1.toArray(new String[groupCnt]);
        aGSkip2 = gSkip2.toArray(new String[groupCnt]);
        aGSkip3 = gSkip3.toArray(new String[groupCnt]);
        aGSkip4 = gSkip4.toArray(new String[groupCnt]);
        aGroupWhos = new String[groupCnt][];
        aGroupWhoKey1 = new String[groupCnt][][];
        aGroupWhoKey2 = new String[groupCnt][][];
        aGroupWhoSkip = new String[groupCnt][][];
        aGroupWhoPrev = new String[groupCnt][][];
        aGroupWhoNext = new String[groupCnt][][];
        aAlertLineIdx = new int[groupCnt][][];
        aGroupSaid = new String[groupCnt];
        for (int i = 0; i < groupCnt; i++)
            aGroupSaid[i] = "x";

        gIdx = 0; gwIdx = 0;
        ArrayList<String> whoList = new ArrayList<>();
        for (AlertLine alt: alertLines) {
            if (alt.matched == -1) {    // this means group
                int sz = whoList.size();
                if (sz > 0) {    // save Prev Group
                    aGroupWhos[gIdx] = whoList.toArray(new String[whoList.size()]);
                    aGroupWhoKey1[gIdx] = new String[sz][];
                    aGroupWhoKey2[gIdx] = new String[sz][];
                    aGroupWhoSkip[gIdx] = new String[sz][];
                    aGroupWhoPrev[gIdx] = new String[sz][];
                    aGroupWhoNext[gIdx] = new String[sz][];
                    aAlertLineIdx[gIdx] = new int[sz][];
                    whoList = new ArrayList<>();
                    gIdx++;
                }
            } else {
                if (!svWho.equals(alt.who))
                    whoList.add(alt.who);
                svWho = alt.who;
            }
        }

        clearArrays();
        gIdx = 0;
        svIdx = 2;
        for (int i = 0; i < alertSize; i++) {
            AlertLine al = alertLines.get(i);
            if (al.matched == -1) {    // this means group
                if (key1.size()> 0) {
                    makeAGroupWho();
                    svIdx = i;
                    gIdx++;
                }
                clearArrays();
                gwIdx = 0;
            } else {
                if (!svWho.equals(al.who)) {
                    if (key1.size()> 0) {
                        makeAGroupWho();
                        gwIdx++;
                    }
                    svIdx = i;
                    svWho = al.who;
                    clearArrays();
                }
                key1.add(al.key1);
                key2.add(al.key2);
                skip.add(al.skip.equals("") ? none : al.skip);
            }
        }
    }

    static void makeAGroupWho() {
        int sz = key1.size();
        aGroupWhoKey1[gIdx][gwIdx] = key1.toArray(new String[sz]);
        aGroupWhoKey2[gIdx][gwIdx] = key2.toArray(new String[sz]);
        aGroupWhoSkip[gIdx][gwIdx] = skip.toArray(new String[sz]);
        aGroupWhoPrev[gIdx][gwIdx] = skip.toArray(new String[sz]);
        aGroupWhoNext[gIdx][gwIdx] = skip.toArray(new String[sz]);
        aAlertLineIdx[gIdx][gwIdx] = new int [sz];
        for (int j = 0; j < sz; j++)
            aAlertLineIdx[gIdx][gwIdx][j] = svIdx+j;
    }

    static void clearArrays() {
        key1 = new ArrayList<>();
        key2 = new ArrayList<>();
        skip = new ArrayList<>();
        prev = new ArrayList<>();
        next = new ArrayList<>();
    }
    static void sort() {
        // group asc, who asc, matched desc
        alertLines.sort(Comparator.comparing(obj -> (obj.group + " " + ((obj.matched == -1)? " ": obj.who + (999-obj.matched)))));
    }
    static void saveFile() {
        String sv = "sv";
        int[] padLen = getMaxLengths();
        StringBuilder s = new StringBuilder();
        for (AlertLine al : alertLines) {
            if (!(al.group+al.who).equals(sv)) {
                sv = al.group+al.who;
                s.append("\n");
            }
            s.append(strPad(al.group, padLen[0])).append("^");
            s.append(strPad(al.who, padLen[1])).append("^");
            s.append(strPad(al.key1, padLen[2])).append("^");
            s.append(strPad(al.key2, padLen[3])).append("^");
            s.append(strPad(al.talk, (padLen[4]))).append("^");
            String m = "     "+al.matched+"  ";
            s.append(m.substring(m.length()-7)).append("^");
            s.append(strPad(al.skip, (padLen[5]))).append("^");
            s.append(" ").append(al.memo);
            s.append("~").append(al.more).append("^");
            s.append(strPad(al.prev, (padLen[6]))).append("^");
            s.append(strPad(al.next, (padLen[7]))).append("^");
            s.append("\n");
        }
        FileIO.writeTextFile( tableFolder,"kTalkAlerts",s.toString());
        SimpleDateFormat timeStamp = new SimpleDateFormat("_HHmmss", Locale.KOREA);
        FileIO.writeTextFile( todayFolder,"kTalkAlerts"+timeStamp.format(new Date()),s.toString());
        AlertTable.makeArrays();
        utils.showSnackBar("Alert Table", "Saved..");
    }

    static final String blank = StringUtils.repeat(" ", 20);
//    static final String del = String.copyValueOf(new char[]{(char) Byte.parseByte("7F", 16)});

    private static String strPad(String s, int size) {
        s = s.trim();
        int chars = calcBytes(s);
        if (chars >= size)
            return s;
        int padL = (size - chars) / 2;
        int padR = size - chars - padL;
        return blank.substring(0, padL) + s + blank.substring(0, padR);
    }

    static int [] getMaxLengths() {
        int [] maxLen = new int[8];
        int bl;
        for (AlertLine al : alertLines){
            bl = calcBytes(al.group); if (bl > maxLen[0]) maxLen[0] = bl;
            bl = calcBytes(al.who); if (bl > maxLen[1]) maxLen[1] = bl;
            bl = calcBytes(al.key1); if (bl > maxLen[2]) maxLen[2] = bl;
            bl = calcBytes(al.key2); if (bl > maxLen[3]) maxLen[3] = bl;
            bl = calcBytes(al.talk); if (bl > maxLen[4]) maxLen[4] = bl;
            bl = calcBytes(al.skip); if (bl > maxLen[5]) maxLen[5] = bl;
            bl = calcBytes(al.prev); if (bl > maxLen[6]) maxLen[6] = bl;
            bl = calcBytes(al.next); if (bl > maxLen[7]) maxLen[7] = bl;
        }
        for (int i = 0; i < 8; i++)
            maxLen[i] = maxLen[i] + maxLen[i] % 2 + 2;
        return maxLen;
    }

    static int calcBytes(String s) {
            return ByteLength.get(s) + 4;
    }
}