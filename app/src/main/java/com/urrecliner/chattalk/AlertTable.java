package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.SubFunc.sounds;
import static com.urrecliner.chattalk.SubFunc.utils;
import static com.urrecliner.chattalk.Vars.alertLines;
import static com.urrecliner.chattalk.Vars.aGroupDot;
import static com.urrecliner.chattalk.Vars.aGroupSay;
import static com.urrecliner.chattalk.Vars.aGroupSkip1;
import static com.urrecliner.chattalk.Vars.aGroupSkip2;
import static com.urrecliner.chattalk.Vars.aGroupSkip3;
import static com.urrecliner.chattalk.Vars.aGroupSkip4;
import static com.urrecliner.chattalk.Vars.kGroupWho;
import static com.urrecliner.chattalk.Vars.aGroupWhoDot;
import static com.urrecliner.chattalk.Vars.kKey1;
import static com.urrecliner.chattalk.Vars.kKey2;
import static com.urrecliner.chattalk.Vars.kSkip;
import static com.urrecliner.chattalk.Vars.kTalk;
import static com.urrecliner.chattalk.Vars.lastChar;
import static com.urrecliner.chattalk.Vars.tableFolder;
import static com.urrecliner.chattalk.Vars.tableListFile;
import static com.urrecliner.chattalk.Vars.todayFolder;

import com.urrecliner.chattalk.Sub.AlertLine;

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

//   group^  who        ^  keyword1 ^ keyword2 ^ talk ^ count ^ skip     ^ memo
//    고선 ^ 고선생       ^    매수    ^   목표가  ^      ^  101  ^          ^ 01/11

    static void readFile() {

        String[] lines = tableListFile.read("kTalkAlerts");

        alertLines = new ArrayList<>();
        for (String line : lines) {
            String[] strings = (line+" ").split("\\^");
            if (strings.length < 8) {
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
            alertLines.add(new AlertLine(tGroup, tWho, tKey1, tKey2, tTalk, matched, tSkip, tMemo, tMore));
        }
    }

    static void makeArrays() {
        ArrayList<String> aGroupWho = new ArrayList<>();
        ArrayList<String> aGroupWhoSaved = new ArrayList<>();
        List<Integer> aGroupWhoS = new ArrayList<>();
        List<Integer> aGroupWhoF = new ArrayList<>();
        ArrayList<String> aKey1 = new ArrayList<>();
        ArrayList<String> aKey2 = new ArrayList<>();
        ArrayList<String> aTalk = new ArrayList<>();
        ArrayList<String> aSkip = new ArrayList<>();
        ArrayList<String> aGGroup = new ArrayList<>();
        ArrayList<String> aGSay = new ArrayList<>();
        ArrayList<String> aGSkip1 = new ArrayList<>();
        ArrayList<String> aGSkip2 = new ArrayList<>();
        ArrayList<String> aGSkip3 = new ArrayList<>();
        ArrayList<String> aGSkip4 = new ArrayList<>();

        StringBuilder sbA = new StringBuilder();
        StringBuilder sbB = new StringBuilder();
        String savedGroup = "";
        String savedWho = "";
        final String blank = "~blank";  // any special characters
        int groupIdx = 0;

        int alertSize = alertLines.size();
        for (int aIdx = 0; aIdx < alertSize; aIdx++) {
            AlertLine al =alertLines.get(aIdx);
            if (al.group.startsWith(lastChar))
                break;
            if (!al.group.equals(savedGroup)) {
                savedGroup = al.group;
                aGGroup.add(savedGroup);
                String []say = al.memo.split("\\|");
                aGSay.add(say[0]);
                aGSkip1.add(al.key1.equals("") ? blank:al.key1);
                aGSkip2.add(al.key2.equals("") ? blank:al.key2);
                aGSkip3.add(al.talk.equals("") ? blank:al.talk);
                aGSkip4.add(al.skip.equals("") ? blank:al.skip);
                sbA.append(" ").append(groupIdx++ +1000).append(savedGroup).append("!");
            }

            if (!al.who.equals(savedWho)) {
                if (!savedWho.equals(""))
                    aGroupWhoF.add(aIdx);
                aGroupWhoS.add(aIdx);
                savedWho = al.who;
                sbB.append(" ").append(aGroupWhoS.size()+999).append(savedGroup).append("!").append(savedWho).append("!");
            }
            aGroupWho.add(savedGroup + "!" + savedWho);
            aGroupWhoSaved.add("x");
            aKey1.add(al.key1);
            aKey2.add(al.key2);
            aTalk.add(al.talk);
            aSkip.add(al.skip.equals("") ? blank:al.skip);
        }
        sbB.append(" ");
        aGroupWhoF.add(alertLines.size()-1);
        int sz = aGroupWho.size();
        aGroupDot = sbA.toString();
        aGroupWhoDot = sbB.toString();
        kGroupWho = aGroupWho.toArray(new String [sz]);
        Vars.aGroupWhoS = aGroupWhoS.toArray(new Integer [0]);
        Vars.aGroupWhoF = aGroupWhoF.toArray(new Integer [0]);
        kKey1 = aKey1.toArray(new String [sz]);   // 인식 문자 1
        kKey2 = aKey2.toArray(new String [sz]);   // 인식 문자 2
        kTalk = aTalk.toArray(new String [sz]);   // 뭐 있으면 무조건 떠들기
        kSkip = aSkip.toArray(new String [sz]);   // 무시할 문자

        int groupCnt = aGGroup.size();    // skip or say if this group should be talked
        aGroupSay = aGSay.toArray(new String[groupCnt]);
        aGroupSkip1 = aGSkip1.toArray(new String[groupCnt]);
        aGroupSkip2 = aGSkip2.toArray(new String[groupCnt]);
        aGroupSkip3 = aGSkip3.toArray(new String[groupCnt]);
        aGroupSkip4 = aGSkip4.toArray(new String[groupCnt]);
        Vars.aGroupWhoSaved = aGroupWhoSaved.toArray(new String [0]);
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
            s.append("~").append(al.more);
            s.append("\n");
        }
        FileIO.writeTextFile( tableFolder,"kTalkAlerts",s.toString());
        SimpleDateFormat timeStamp = new SimpleDateFormat("_HHmmss", Locale.KOREA);
        FileIO.writeTextFile( todayFolder,"kTalkAlerts"+timeStamp.format(new Date()),s.toString());
        AlertTable.makeArrays();
        utils.showSnackBar("Alert Table", "Saved..");
    }

    static final String blank = StringUtils.repeat(" ", 20);
    static final String del = String.copyValueOf(new char[]{(char) Byte.parseByte("7F", 16)});

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
        int [] maxLen = new int[6];
        int bl;
        for (AlertLine al : alertLines){
            bl = calcBytes(al.group); if (bl > maxLen[0]) maxLen[0] = bl;
            bl = calcBytes(al.who); if (bl > maxLen[1]) maxLen[1] = bl;
            bl = calcBytes(al.key1); if (bl > maxLen[2]) maxLen[2] = bl;
            bl = calcBytes(al.key2); if (bl > maxLen[3]) maxLen[3] = bl;
            bl = calcBytes(al.talk); if (bl > maxLen[4]) maxLen[4] = bl;
            bl = calcBytes(al.skip); if (bl > maxLen[5]) maxLen[5] = bl;
        }
        for (int i = 0; i < 6; i++)
            maxLen[i] = maxLen[i] + maxLen[i] % 2 + 2;
        return maxLen;
    }

    static int calcBytes(String s) {
        int chars = 4;
        for (int i = 0; i < s.length(); i++) {
            String bite = s.substring(i,i+1);
            chars += (bite.compareTo(del)>0)? 2:1;
        }
        return chars;
    }
}