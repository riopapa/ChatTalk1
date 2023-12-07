package com.urrecliner.chattalk.Sub;

import com.urrecliner.chattalk.model.ZWhoText_UnUsed;

import java.util.ArrayList;
import java.util.Collections;

public class ZIsWhoText_UnUsed {

    public static boolean repeated(final ArrayList<ZWhoText_UnUsed> whoSaysWhat, String mWho, String mText) {
        if (whoSaysWhat.isEmpty()) {
            whoSaysWhat.add(new ZWhoText_UnUsed(mWho, mText));
            return false;
        }
        int whoSaySize = whoSaysWhat.size();
        for (int i = 0; i < whoSaySize; i++) {
            int compared = whoSaysWhat.get(i).who.compareTo(mWho);
            if (compared == 0) {
                if (whoSaysWhat.get(i).text.equals(mText))
                    return true;
                whoSaysWhat.set(i, new ZWhoText_UnUsed(mWho, mText));
            } else if (compared > 0) {
                whoSaysWhat.add(new ZWhoText_UnUsed(mWho, mText));
                whoSaysWhat.sort((o1, o2) -> o1.who.compareTo(o2.who));
                return false;
            }
        }
        return false;
    }

}