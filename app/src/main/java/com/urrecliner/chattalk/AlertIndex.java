package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.aGroupDot;
import static com.urrecliner.chattalk.Vars.aGroupSkip1;
import static com.urrecliner.chattalk.Vars.aGroupSkip2;
import static com.urrecliner.chattalk.Vars.aGroupSkip3;
import static com.urrecliner.chattalk.Vars.aGroupSkip4;
import static com.urrecliner.chattalk.Vars.aGroupWhoDot;
import static com.urrecliner.chattalk.Vars.aGroupWhoF;
import static com.urrecliner.chattalk.Vars.aGroupWhoS;
import static com.urrecliner.chattalk.Vars.aGroupWhoSaved;
import static com.urrecliner.chattalk.Vars.kKey1;
import static com.urrecliner.chattalk.Vars.kKey2;
import static com.urrecliner.chattalk.Vars.kSkip;

public class AlertIndex {

    int get(int groupPos, String iGroup, String iWho, String iText) {
        int gIdx = Integer.parseInt(aGroupDot.substring(groupPos - 2, groupPos)); // max 99
        if (iText.contains(aGroupSkip1[gIdx]) || iText.contains(aGroupSkip2[gIdx])
                || iText.contains(aGroupSkip3[gIdx]) || iText.contains(aGroupSkip4[gIdx]))
            return -1;
        String groupNWho = iGroup + "!" + iWho + "!";
        int groupWhoPos = aGroupWhoDot.indexOf(groupNWho);
        if (groupWhoPos >= 0) {
            int whoIdx = Integer.parseInt(aGroupWhoDot.substring(groupWhoPos - 3, groupWhoPos)); // max 999
            if (whoIdx < 0)
                return -1;
            if (iText.equals(aGroupWhoSaved[whoIdx])) // 같은 소리 계속 하는 건 빼자
                return -1;
            aGroupWhoSaved[whoIdx] = iText;
            int idxS = aGroupWhoS[whoIdx]; int idxF = aGroupWhoF[whoIdx];
            for (int idx = idxS; idx < idxF; idx++) {
                if (iText.contains(kKey1[idx]) && iText.contains(kKey2[idx]) &&
                    !iText.contains(kSkip[idx]))
                    return idx;
            }
            return -1;
        }
        return -1;
    }

}