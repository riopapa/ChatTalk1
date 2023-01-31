package com.urrecliner.chattalk;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
import static com.urrecliner.chattalk.Vars.alertIndex;
import static com.urrecliner.chattalk.Vars.alertLines;
import static com.urrecliner.chattalk.Vars.chatGroup;
import static com.urrecliner.chattalk.Vars.kGroupDot;
import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.Vars.replGroup;
import static com.urrecliner.chattalk.Vars.replGroupCnt;
import static com.urrecliner.chattalk.Vars.replLong;
import static com.urrecliner.chattalk.Vars.replShort;
import static com.urrecliner.chattalk.Vars.tableListFile;
import static com.urrecliner.chattalk.Vars.utils;

import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.UnderlineSpan;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class SelectChats {

    String[] whose;
    String[] whoKeys;
    String[] keywords, keyWhose, keyword1, keyword2;
    String groupInfo;

    ArrayList<String> msgLines;
    final String repeated = new String(new char[70]).replace("\0", ".")+"\n";

    SpannableString generate(File chatFile) {

        String[] chatLines = tableListFile.readRaw(chatFile);
        if (chatLines == null)
            return null;
        StringBuilder headStr = new StringBuilder();
        String[] head = chatLines[0].replaceAll("[\uFEFC-\uFEFF]","").trim().split(" "); // 조선 nnn 카카오톡 대화
        chatGroup = head[0];
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
        int groupPos = kGroupDot.indexOf(chatGroup+"!");
        if (groupPos < 0)
            return new SpannableString(headStr+"\nSelected Lines ---\n\n");
        for (String txt: msgLines) {
            if (txt.equals("") || txt.equals(prvTxt) || canIgnore(txt))
                continue;
            prvTxt = txt;
            int p = txt.indexOf(", ");
            if (p < 0)
                continue;
            String time = txt.substring(0,p+1);
            String tmp = txt.substring(p);
            p = tmp.indexOf(":")-1;
            if (p < 0)
                continue;
            String who = tmp.substring(2, p).trim();
            String body = utils.removeSpecialChars(tmp.substring(3+who.length()));
            if (body.length() < 18)      // 너무 짧으면 대상 아닐 것임
                continue;
            int aIdx = alertIndex.get(groupPos, chatGroup, who, txt);
            if (aIdx > 0) { //  || inWhoList(txt)) {
                SpannableString ss = key2Matched(time, who, body);
                if (ss.length() > 1) {
                    selectedSS = appendSS(selectedSS, key2Matched(time, who, body));
                    matchedSS = appendSS(matchedSS, ss);
                } else
                    selectedSS = appendSS(selectedSS, checkKeywords(time+" "+who+" "+body));
            } else if (inWhoList(who)) {
                selectedSS = appendSS(selectedSS, new SpannableString("▣ "+time+" "+who+" "+makeDot(body)+"\n\n"));
            } else if (hasKeywords(txt)) {
                selectedSS = appendSS(selectedSS, checkKeywords(time+" "+who+" "+body));
            }
        }
        CharSequence cs = TextUtils.concat(new SpannableString(headStr+"\n"),
                matchedSS, selectedSS);
        return new SpannableString(cs);
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
            if (p1 > 0) {
                p2 = body.indexOf(keyword2[k]);
                if (p2 > 0) {
                    String squeezed = utils.strReplace(chatGroup, body);
                    p1 = squeezed.indexOf(keyword1[k]);
                    p2 = squeezed.indexOf(keyword2[k]);
                    SpannableString s = new SpannableString(time+" "+who+" "+squeezed+"\n\n");
                    s.setSpan(new BackgroundColorSpan(mContext.getResources().getColor(R.color.keyMatchedBack, null)), 0, s.length()-1, SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (squeezed.contains(keyWhose[k]))
                        s.setSpan(new UnderlineSpan(), 0, s.length()-1, SPAN_EXCLUSIVE_EXCLUSIVE);
                    int offset = time.length()+who.length()+2;
                    if (p1 > 0) {
                        p1 += offset;
                        s.setSpan(new BackgroundColorSpan(mContext.getResources().getColor(R.color.keyMatchedWord, null)), p1, p1 + keyword1[k].length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    if (p2 > 0) {
                        p2 += offset;
                        s.setSpan(new BackgroundColorSpan(mContext.getResources().getColor(R.color.keyMatchedWord, null)), p2, p2 + keyword2[k].length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    return s;
                }
            }
        }
        return new SpannableString("");
    }

    SpannableString appendSS(SpannableString s1, SpannableString s2) {
        CharSequence as = TextUtils.concat(s1, s2);
        return new SpannableString(as);
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
            Vars.AlertLine al = alertLines.get(i);
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