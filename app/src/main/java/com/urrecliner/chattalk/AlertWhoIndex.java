package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.aGSkip1;
import static com.urrecliner.chattalk.Vars.aGSkip2;
import static com.urrecliner.chattalk.Vars.aGSkip3;
import static com.urrecliner.chattalk.Vars.aGSkip4;
import static com.urrecliner.chattalk.Vars.aGroupWhos;

public class AlertWhoIndex {

    int get(int gIdx, String iWho, String iText) {
        if (iText.contains(aGSkip1[gIdx]) || iText.contains(aGSkip2[gIdx])
                || iText.contains(aGSkip3[gIdx]) || iText.contains(aGSkip4[gIdx]))
            return -1;
        for (int i = 0; i < aGroupWhos[gIdx].length; i++) {
            if (aGroupWhos[gIdx][i].equals(iWho))
                return i;
        }
        return -1;
    }

}