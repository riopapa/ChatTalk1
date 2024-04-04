package biz.riopapa.chattalk;

import static biz.riopapa.chattalk.Vars.aGSkip1;
import static biz.riopapa.chattalk.Vars.aGSkip2;
import static biz.riopapa.chattalk.Vars.aGSkip3;
import static biz.riopapa.chattalk.Vars.aGSkip4;
import static biz.riopapa.chattalk.Vars.aGroupWhos;

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