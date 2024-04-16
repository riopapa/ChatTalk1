package biz.riopapa.chattalk.alerts;

import static biz.riopapa.chattalk.Vars.aAlertLineIdx;
import static biz.riopapa.chattalk.Vars.aGSkip1;
import static biz.riopapa.chattalk.Vars.aGSkip2;
import static biz.riopapa.chattalk.Vars.aGSkip3;
import static biz.riopapa.chattalk.Vars.aGSkip4;
import static biz.riopapa.chattalk.Vars.aGroupSaid;
import static biz.riopapa.chattalk.Vars.aGroupWhoKey1;
import static biz.riopapa.chattalk.Vars.aGroupWhoKey2;
import static biz.riopapa.chattalk.Vars.aGroupWhoNext;
import static biz.riopapa.chattalk.Vars.aGroupWhoPrev;
import static biz.riopapa.chattalk.Vars.aGroupWhoSkip;
import static biz.riopapa.chattalk.Vars.aGroupWhos;
import static biz.riopapa.chattalk.Vars.aGroups;
import static biz.riopapa.chattalk.Vars.aGroupsPass;
import static biz.riopapa.chattalk.Vars.alertLines;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import biz.riopapa.chattalk.model.AlertLine;

public class AlertTable {

//   group^ group name  ^     skip1  ^  skip2    ^ skip3 ^  -1  ^ skip4    ^ sayMore
//    고선 ^ 리딩방 CA ^     !!     ^     해외  ^  BTC  ^       ^    0%    ^ 개장전

//   group^  who        ^  keyword1 ^ keyword2 ^ talk ^ count ^ skip     ^ more ^ prev ^ next
//    고선 ^ 고선생       ^    매수    ^   목표가  ^      ^  101  ^          ^ 중지

    static ArrayList<String> gSkip1 = new ArrayList<>(), gSkip2 = new ArrayList<>(),
            gSkip3 = new ArrayList<>(), gSkip4 = new ArrayList<>();
    static List<String> chkKey1 = new ArrayList<>();
    static List<String> chkKey2 = new ArrayList<>();
    static List<String> chkSkip = new ArrayList<>();
    static List<String> prvKey = new ArrayList<>();
    static List<String> nxtKey = new ArrayList<>();
    static int gIdx, gwIdx, svIdx;

    public static void makeArrays() {

        String svGroup = "x", svWho = "x";
        int alertSize = alertLines.size();
        aGroups = new ArrayList<>();
        aGroupsPass = new ArrayList<>();

        for (AlertLine al: alertLines) {
            if (!svGroup.equals(al.group)) {
                aGroups.add(al.group);
                aGroupsPass.add(!al.more.isEmpty());
                String key;
                key = al.key1; if (key.isEmpty()) key = "NotFnd"; gSkip1.add(key);
                key = al.key2; if (key.isEmpty()) key = "NotFnd"; gSkip2.add(key);
                key = al.talk; if (key.isEmpty()) key = "NotFnd"; gSkip3.add(key);
                key = al.skip; if (key.isEmpty()) key = "NotFnd"; gSkip4.add(key);
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
        for (AlertLine al: alertLines) {
            if (al.matched == -1) {    // this means group
                int sz = whoList.size();
                if (sz > 0) {    // save Prev Group
                    aGroupWhos[gIdx] = whoList.toArray(new String[0]);
                    aGroupWhoKey1[gIdx] = new String[sz][];
                    aGroupWhoKey2[gIdx] = new String[sz][];
                    aGroupWhoSkip[gIdx] = new String[sz][];
                    aGroupWhoPrev[gIdx] = new String[sz][];
                    aGroupWhoNext[gIdx] = new String[sz][];
                    aAlertLineIdx[gIdx] = new int[sz][];
                    whoList = new ArrayList<>();
                    gIdx++;
                }
                svWho = "x";
            } else {
                if (!svWho.equals(al.who))
                    whoList.add(al.who);
                svWho = al.who;
            }
        }

        clearArrays();
        gIdx = 0; gwIdx = 0;
        svIdx = 2;
        svWho = "x";
        for (int i = 0; i < alertSize; i++) {
            AlertLine al = alertLines.get(i);
            if (al.matched == -1) {    // this means group
                if (!chkKey1.isEmpty()) {
                    makeAGroupWho();
                    svIdx = i;
                    gIdx++;
                }
                svWho = "x";
                clearArrays();
                gwIdx = 0;
            } else {
                if (!svWho.equals(al.who)) {
                    if (!chkKey1.isEmpty()) {
                        makeAGroupWho();
                        gwIdx++;
                    }
                    svIdx = i;
                    svWho = al.who;
                    clearArrays();
                }
                chkKey1.add(al.key1);
                chkKey2.add(al.key2);
                chkSkip.add(al.skip.isEmpty() ? "N0sKi" : al.skip);
                prvKey.add(al.prev);
                nxtKey.add(al.next);
            }
        }
    }

    static void makeAGroupWho() {
        int sz = chkKey1.size();
        aGroupWhoKey1[gIdx][gwIdx] = chkKey1.toArray(new String[sz]);
        aGroupWhoKey2[gIdx][gwIdx] = chkKey2.toArray(new String[sz]);
        aGroupWhoSkip[gIdx][gwIdx] = chkSkip.toArray(new String[sz]);
        aGroupWhoPrev[gIdx][gwIdx] = prvKey.toArray(new String[sz]);
        aGroupWhoNext[gIdx][gwIdx] = nxtKey.toArray(new String[sz]);
        aAlertLineIdx[gIdx][gwIdx] = new int [sz];
        for (int j = 0; j < sz; j++)
            aAlertLineIdx[gIdx][gwIdx][j] = svIdx+j;
    }

    static void clearArrays() {
        chkKey1 = new ArrayList<>();
        chkKey2 = new ArrayList<>();
        chkSkip = new ArrayList<>();
        prvKey = new ArrayList<>();
        nxtKey = new ArrayList<>();
    }
    static void sort() {
        // group asc, who asc, matched desc
        alertLines.sort(Comparator.comparing(obj -> (obj.group + " " + ((obj.matched == -1)? " ": obj.who + (9999-obj.matched)))));
    }

//    static final String del = String.copyValueOf(new char[]{(char) Byte.parseByte("7F", 16)});


}