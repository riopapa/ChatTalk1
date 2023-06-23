package com.urrecliner.chattalk;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
import static com.urrecliner.chattalk.NotificationListener.utils;
import static com.urrecliner.chattalk.Vars.aGroupWhoKey1;
import static com.urrecliner.chattalk.Vars.aGroupWhoKey2;
import static com.urrecliner.chattalk.Vars.aGroupWhoSkip;
import static com.urrecliner.chattalk.Vars.aGroups;
import static com.urrecliner.chattalk.Vars.alertLines;
import static com.urrecliner.chattalk.Vars.alertWhoIndex;
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
import android.widget.Toast;

import com.urrecliner.chattalk.Sub.AlertLine;
import com.urrecliner.chattalk.Sub.Dot;
import com.urrecliner.chattalk.Sub.StockName;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class SelectChats {

    String[] whose;
    String[] whoKeys;
    String[] keywords, keyWhose, keyword1, keyword2, prev, next;
    String groupInfo, gSkip1, gSkip2, gSkip3, gSkip4;
    boolean upload;
    ArrayList<String> msgLines;

    SpannableString generate(File chatFile, boolean upload) {
        this.upload = upload;
        String[] chatLines = tableListFile.readRaw(chatFile);
        if (chatLines == null)
            return null;
        StringBuilder headStr = new StringBuilder();
        String[] head = chatLines[0].replaceAll("[\uFEFC-\uFEFF]","").trim().split(" "); // 조선 nnn 카카오톡 대화
        chatGroup = head[0];
        int gIdx = Collections.binarySearch(aGroups, chatGroup);
        getWhoKeyList();
        headStr.append("그룹 : ").append(chatGroup).append(" ").append(groupInfo).append("\n");
        for (String w: whoKeys)
            headStr.append(w).append("\n");
        headStr.append("\nstrReplaces ---\n\n");
        for (int i = 0; i < replGroupCnt; i++) {
            int compared = chatGroup.compareTo(replGroup[i]);
            if (compared == 0) {
                for (int j = 0; j < replLong[i].length; j++)
                    headStr.append(replShort[i][j]).append(" > ").append(replLong[i][j]).append("\n");
            }
        }

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
            if (txt.contains(gSkip1) || txt.contains(gSkip2) ||
                    txt.contains(gSkip3) || txt.contains(gSkip4))
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
            int gwIdx = alertWhoIndex.get(gIdx, who, body);
            if (gwIdx >=0) {
                for (int i = 0; i < aGroupWhoKey1[gIdx][gwIdx].length; i++) {
                    if ((body.contains(aGroupWhoKey1[gIdx][gwIdx][i])) &&
                            (body.contains(aGroupWhoKey2[gIdx][gwIdx][i])) &&
                            (!body.contains(aGroupWhoSkip[gIdx][gwIdx][i]))) {
                        found = true;
                        break;
                    }
                }
            }

            if (found) { //  || inWhoList(txt)) {
                SpannableString ss = key2Matched(time, who, body, upload);
                if (ss.length() > 10) {
                    matchedSS = concatSS(matchedSS, ss);
                    selectedSS = new SpannableString(TextUtils.concat(selectedSS,
                            key2Matched(time, who, body, false)));
                } else
                    selectedSS = concatSS(selectedSS, checkKeywords(time+", "+who+" , "+body));
            } else if (inWhoList(who)) {
                selectedSS = concatSS(selectedSS, new SpannableString("▣ "+time+" , "+who+" , "+makeDot(body)+"\n\n"));
            } else if (hasKeywords(txt)) {
                selectedSS = concatSS(selectedSS, checkKeywords(time+", "+who+" , "+body));
            }
        }
        if (upload)
            Toast.makeText(mContext, " Uploaded to google", Toast.LENGTH_SHORT).show();
        return new SpannableString(TextUtils.concat(new SpannableString(headStr+"\n"),
                matchedSS, selectedSS));
    }
    private String makeDot(String txt) {
        if (txt.length() > 100)
            return txt.substring(0, 95) + " ⋙";
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

    SpannableString key2Matched(String time, String who, String body, boolean upload) {
        int p1, p2;
        for (int k = 0; k < keyword1.length; k++) {
            p1 = body.indexOf(keyword1[k]);
            if (p1 > 0) {
                p2 = body.indexOf(keyword2[k], p1+1);
                if (p2 >= 0) {      // both matched
                    body = utils.strReplace(chatGroup, body);
                    String keys = "<"+keyword1[k]+"~"+keyword2[k]+">";
                    String str = time+", "+who+" , "+ body + " " + keys;
                    SpannableString s = new SpannableString(str+"\n\n");
                    s.setSpan(new BackgroundColorSpan(mContext.getResources().getColor(R.color.keyMatchedBack, null)), 0, s.length()-1, SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (str.contains(keyWhose[k]))
                        s.setSpan(new UnderlineSpan(), 0, s.length()-1, SPAN_EXCLUSIVE_EXCLUSIVE);
                    p1 = str.indexOf(prev[k]);
                    if (p1 >= 0) {
                        s.setSpan(new BackgroundColorSpan(mContext.getResources().getColor(R.color.keyMatchedWord, null)), p1, p1 + prev[k].length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    p2 = str.indexOf(next[k], p1+1);
                    if (p2 >= 0) {
                        s.setSpan(new BackgroundColorSpan(mContext.getResources().getColor(R.color.keyMatchedWord, null)), p2, p2 + next[k].length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    if(upload) {
                        String stockName = new StockName().parse(prev[k], next[k], body);
                        body = body.replace(stockName, new Dot().add(stockName));
                        FileIO.uploadStock(chatGroup, who, "chats", stockName,
                                body, keys, time);
                    }
                    return s;
                }
            }
        }
        return new SpannableString("");
    }

    SpannableString concatSS(SpannableString s1, SpannableString s2) {
        return new SpannableString(TextUtils.concat(s1, s2));
    }

    void getWhoKeyList() {
        String svWho = "x";
        ArrayList<String> aWhose = new ArrayList<>();
        ArrayList<String> aWhoKeys = new ArrayList<>();
        ArrayList<String> aKeywords = new ArrayList<>();
        ArrayList<String> aKeyWhose = new ArrayList<>();
        ArrayList<String> aKey1 = new ArrayList<>();
        ArrayList<String> aKey2 = new ArrayList<>();
        ArrayList<String> aPrev = new ArrayList<>();
        ArrayList<String> aNext = new ArrayList<>();
        int alertSize = alertLines.size();
        for (int i = 0; i < alertSize; i++) {
            AlertLine al = alertLines.get(i);
            if (al.group.equals(chatGroup)) {
                if (!al.who.equals(svWho)) {
                    if (al.matched == -1) { // head
                        gSkip1 = al.key1; gSkip2 = al.key2; gSkip3 = al.talk; gSkip4 = al.skip;
                        groupInfo = "("+al.who + ") skip |"+ gSkip1 + "|"+ gSkip2 + "|"
                                + gSkip3 +"|" + gSkip4 +"|\n" + al.memo;
                        if (gSkip1.equals("")) gSkip1 = "업슴";
                        if (gSkip2.equals("")) gSkip2 = "업슴";
                        if (gSkip3.equals("")) gSkip3 = "업슴";
                        if (gSkip4.equals("")) gSkip4 = "업슴";
                    } else {
                        svWho = al.who;
                        aWhose.add(svWho);
                    }
                }
                if (!(al.matched == -1)) {
                    aWhoKeys.add(svWho + ", [" + al.key1 + "," + al.key2 + "]"
                        + ((al.skip.length() > 1) ? ", skip[" + al.skip + "]" : "")
                        + (" ("+al.matched+") ")
                        + ((al.talk.length() > 1) ? ", talk[" + al.talk + "]" : "")
                        + " <" +al.prev+"x"+al.next+">")
                    ;
                    if (al.key1.length()> 1) aKeywords.add(al.key1);
                    if (al.key2.length()> 1) aKeywords.add(al.key2);
                    if (al.key1.length()> 1) {
                        aKeyWhose.add(al.who);
                        aKey1.add(al.key1);
                        aKey2.add(al.key2);
                        aPrev.add(al.prev);
                        aNext.add(al.next);
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
        keyword1 = aKey1.toArray(new String[0]);
        keyword2 = aKey2.toArray(new String[0]);
        prev = aPrev.toArray(new String[0]);
        next = aNext.toArray(new String[0]);
    }
}
