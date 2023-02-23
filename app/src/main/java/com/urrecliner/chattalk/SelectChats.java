package com.urrecliner.chattalk;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
import static com.urrecliner.chattalk.SubFunc.utils;
import static com.urrecliner.chattalk.Vars.aAlertLineIdx;
import static com.urrecliner.chattalk.Vars.aGroupSaid;
import static com.urrecliner.chattalk.Vars.aGroupWhoKey1;
import static com.urrecliner.chattalk.Vars.aGroupWhoKey2;
import static com.urrecliner.chattalk.Vars.aGroupWhoSkip;
import static com.urrecliner.chattalk.Vars.aGroups;
import static com.urrecliner.chattalk.Vars.alertWhoIndex;
import static com.urrecliner.chattalk.Vars.alertLines;
import static com.urrecliner.chattalk.Vars.chatGroup;
import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.Vars.replGroup;
import static com.urrecliner.chattalk.Vars.replGroupCnt;
import static com.urrecliner.chattalk.Vars.replLong;
import static com.urrecliner.chattalk.Vars.replShort;
import static com.urrecliner.chattalk.Vars.tableListFile;

import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.UnderlineSpan;

import com.urrecliner.chattalk.Sub.AlertLine;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class SelectChats {

    String[] whose;
    String[] whoKeys;
    String[] keywords, keyWhose, keyword1, keyword2;
    String groupInfo;
    boolean upload;
    ArrayList<String> msgLines;
    final String repeated = new String(new char[70]).replace("\0", ".")+"\n";

    SpannableString generate(File chatFile, boolean upload) {
        this.upload = upload;
        String[] chatLines = tableListFile.readRaw(chatFile);
        if (chatLines == null)
            return null;
        StringBuilder headStr = new StringBuilder();
        String[] head = chatLines[0].replaceAll("[\uFEFC-\uFEFF]","").trim().split(" "); // 조선 nnn 카카오톡 대화
        chatGroup = head[0];
        int gIdx = Collections.binarySearch(aGroups, chatGroup);
        getWhoList();
        headStr.append("그룹 : ").append(chatGroup).append(" ").append(groupInfo).append("\n");
        for (String w: whoKeys)
            headStr.append(w).append("\n");
        headStr.append(repeated).append("strReplaces :\n");
        for (int i = 0; i < replGroupCnt; i++) {
            int compared = chatGroup.compareTo(replGroup[i]);
            if (compared == 0) {
                for (int j = 0; j < replLong[i].length; j++)
                    headStr.append(replShort[i][j]).append(" > ").append(replLong[i][j]).append("\n");
            }
        }
        headStr.append(repeated);

        msgLines = new ArrayList<>();     // message lines chosen
        StringBuilder mSb = new StringBuilder();
        for (String ln : chatLines) {
            if (ln.startsWith("202")) {
                String txt = mSb.toString();
                if (txt.contains(" : ") && txt.length() > 11)
                    msgLines.add(txt.substring(6));
                mSb = new StringBuilder(ln);
            } else {
                mSb.append("|").append(ln);
            }
        }

        String prvTxt = "";
        SpannableString matchedSS = new SpannableString("\nMatched Lines ---\n\n");
        SpannableString selectedSS = new SpannableString("\nSelected Lines ---\n\n");

        if (gIdx < 0)
            return new SpannableString(headStr+"\nSelected Lines ---\n\n");

        for (String txt: msgLines) {
            if (txt.equals("") || txt.equals(prvTxt) || canIgnore(txt))
                continue;
            prvTxt = txt;
            int p = txt.indexOf(", ");
            if (p < 0)
                continue;
            String time = txt.substring(0,p);
            String tmp = txt.substring(p+2);
            p = tmp.indexOf(":")-1;
            if (p < 0)
                continue;
            String who = tmp.substring(0, p).trim();
            String body = utils.removeSpecialChars(tmp.substring(3+who.length()));
            if (body.length() < 16)      // 너무 짧으면 대상 아닐 것임
                continue;

            boolean found = false;
            int gwIdx = alertWhoIndex.get(gIdx, who, txt);
            if (gwIdx >=0) {
                for (int i = 0; i < aGroupWhoKey1[gIdx][gwIdx].length; i++) {
                    if ((txt.contains(aGroupWhoKey1[gIdx][gwIdx][i])) &&
                            (txt.contains(aGroupWhoKey2[gIdx][gwIdx][i])) &&
                            (!txt.contains(aGroupWhoSkip[gIdx][gwIdx][i]))) {
                        found = true;
                        break;
                    }
                }
            }

            if (found) { //  || inWhoList(txt)) {
                SpannableString ss = key2Matched(time, who, body);
                if (ss.length() > 1) {
                    selectedSS = appendSS(selectedSS, ss);
                    matchedSS = appendSS(matchedSS, ss);
                } else
                    selectedSS = appendSS(selectedSS, checkKeywords(time+", "+who+" , "+body));
            } else if (inWhoList(who)) {
                selectedSS = appendSS(selectedSS, new SpannableString("▣ "+time+" , "+who+" , "+makeDot(body)+"\n\n"));
            } else if (hasKeywords(txt)) {
                selectedSS = appendSS(selectedSS, checkKeywords(time+", "+who+" , "+body));
            }
        }
        return new SpannableString(TextUtils.concat(new SpannableString(headStr+"\n"),
                matchedSS, selectedSS));
    }

    private String makeDot(String txt) {
        if (txt.length() > 150)
            return txt.substring(0, 145) + " ⋙";
        return txt;
    }

    final String [] chatIgnores = {"http", "항셍", "나스닥", "무료", "반갑", "발동", "사진", "상담", "선물",
            "입장", "파생", "프로필", "현황"};

    boolean canIgnore(String mergedLine) {
        for (String ig : chatIgnores) {
            if (mergedLine.contains(ig))
                return true;
        }
        return false;
    }

    boolean inWhoList (String ln) {
        for (String whoList : whose) {
            if (ln.contains(whoList))
                return true;
        }
        return false;
    }

    boolean hasKeywords (String ln) {
        for (String k : keywords) {
            if (ln.contains(k))
                return true;
        }
        return false;
    }

    SpannableString checkKeywords(String text) {
        SpannableString s = new SpannableString(text+"\n\n");
        for (String keyword : keywords) {
            int p = text.indexOf(keyword);
            if (p > 0) {
                s.setSpan(new BackgroundColorSpan(mContext.getResources().getColor(R.color.keyMatchedBack, null)), p, p + keyword.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return s;
    }

    SpannableString key2Matched(String time, String who, String body) {
        int p1, p2;
        for (int k = 0; k < keyword1.length; k++) {
            p1 = body.indexOf(keyword1[k]);
            if (p1 >= 0) {
                p2 = body.indexOf(keyword2[k]);
                if (p2 >= 0) {
                    String str = time+", "+who+" , "+utils.strReplace(chatGroup, body)+"\n\n";
                    SpannableString s = new SpannableString(str);
                    s.setSpan(new BackgroundColorSpan(mContext.getResources().getColor(R.color.keyMatchedBack, null)), 0, s.length()-1, SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (str.contains(keyWhose[k]))
                        s.setSpan(new UnderlineSpan(), 0, s.length()-1, SPAN_EXCLUSIVE_EXCLUSIVE);
                    p1 = str.indexOf(keyword1[k]);
                    if (p1 >= 0) {
                        s.setSpan(new BackgroundColorSpan(mContext.getResources().getColor(R.color.keyMatchedWord, null)), p1, p1 + keyword1[k].length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    p2 = str.indexOf(keyword2[k]);
                    if (p2 >= 0) {
                        s.setSpan(new BackgroundColorSpan(mContext.getResources().getColor(R.color.keyMatchedWord, null)), p2, p2 + keyword2[k].length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    if(upload)
                        FileIO.uploadStock(chatGroup, who, "", "selChats",
                                body, "["+keyword1[k]+"/"+keyword2[k]+"]", time);
                    return s;
                }
            }
        }
        return new SpannableString("");
    }

    SpannableString appendSS(SpannableString s1, SpannableString s2) {
        return new SpannableString(TextUtils.concat(s1, s2));
    }

    void getWhoList() {
        String svWho = "x";
        ArrayList<String> aWhose = new ArrayList<>();
        ArrayList<String> aWhoKeys = new ArrayList<>();
        ArrayList<String> aKeywords = new ArrayList<>();
        ArrayList<String> aKeyWhose = new ArrayList<>();
        ArrayList<String> aKeyword1 = new ArrayList<>();
        ArrayList<String> aKeyword2 = new ArrayList<>();
        int alertSize = alertLines.size();
        for (int i = 0; i < alertSize; i++) {
            AlertLine al = alertLines.get(i);
            if (al.group.equals(chatGroup)) {
                if (!al.who.equals(svWho)) {
                    if (al.matched == -1) { // head
                        groupInfo = "("+al.who + ") skip |"+ al.key1 + "|"+ al.key2 + "|"
                                + al.talk+"|" + al.skip+"|\n" + al.memo;
                    } else {
                        svWho = al.who;
                        aWhose.add(svWho);
                    }
                }
                if (!(al.matched == -1)) {
                    aWhoKeys.add(svWho + ", [" + al.key1 + "," + al.key2 + "]"
                            + ((al.skip.length() > 1) ? ", skip[" + al.skip + "]" : "")
                            + (" ("+al.matched+") ")
                            + ((al.talk.length() > 1) ? ", talk[" + al.talk + "]" : ""));
                    if (al.key1.length()> 1) aKeywords.add(al.key1);
                    if (al.key2.length()> 1) aKeywords.add(al.key2);
                    if (al.key1.length()> 1) {
                        aKeyWhose.add(al.who);
                        aKeyword1.add(al.key1);
                        aKeyword2.add(al.key2);
                    }
                }
            }
        }

        aKeywords.add("매도"); aKeywords.add("매수");
        aKeywords.add("자율");aKeywords.add("종목");

        Collections.sort(aKeywords);
        for (int i = 1; i < aKeywords.size();) {
            if (aKeywords.get(i).equals(aKeywords.get(i-1))) {
                aKeywords.remove(i);
            } else
                i++;
        }
        whose = aWhose.toArray(new String[0]);
        whoKeys = aWhoKeys.toArray(new String[0]);
        keywords = aKeywords.toArray(new String[0]);
        keyWhose = aKeyWhose.toArray(new String[0]);
        keyword1 = aKeyword1.toArray(new String[0]);
        keyword2 = aKeyword2.toArray(new String[0]);
    }
}