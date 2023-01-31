package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.kGroupDot;
import static com.urrecliner.chattalk.Vars.kGroupSkip1;
import static com.urrecliner.chattalk.Vars.kGroupSkip2;
import static com.urrecliner.chattalk.Vars.kGroupSkip3;
import static com.urrecliner.chattalk.Vars.kGroupSkip4;
import static com.urrecliner.chattalk.Vars.kGroupWhoDot;
import static com.urrecliner.chattalk.Vars.kGroupWhoF;
import static com.urrecliner.chattalk.Vars.kGroupWhoS;
import static com.urrecliner.chattalk.Vars.kGroupWhoSaved;
import static com.urrecliner.chattalk.Vars.kKey1;
import static com.urrecliner.chattalk.Vars.kKey2;
import static com.urrecliner.chattalk.Vars.kSkip;

public class AlertIndex {

    int get(int groupPos, String iGroup, String iWho, String iText) {
        int gIdx = Integer.parseInt(kGroupDot.substring(groupPos - 2, groupPos)); // max 99
        if (iText.contains(kGroupSkip1[gIdx]) || iText.contains(kGroupSkip2[gIdx])
                || iText.contains(kGroupSkip3[gIdx]) || iText.contains(kGroupSkip4[gIdx]))
            return -1;
        String groupNWho = iGroup + "!" + iWho + "!";
        int groupWhoPos = kGroupWhoDot.indexOf(groupNWho);
        if (groupWhoPos >= 0) {
            int whoIdx = Integer.parseInt(kGroupWhoDot.substring(groupWhoPos - 3, groupWhoPos)); // max 999
            if (whoIdx < 0)
                return -1;
            if (iText.equals(kGroupWhoSaved[whoIdx])) // 같은 소리 계속 하는 건 빼자
                return -1;
            kGroupWhoSaved[whoIdx] = iText;
            int idxS = kGroupWhoS[whoIdx]; int idxF = kGroupWhoF[whoIdx];
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