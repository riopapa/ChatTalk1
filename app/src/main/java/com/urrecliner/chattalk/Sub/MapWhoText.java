package com.urrecliner.chattalk.Sub;

import java.util.HashMap;

public class MapWhoText {

    public static boolean repeated(final HashMap whoSaysWhat, String mWho, String mText) {
        if (whoSaysWhat.isEmpty()) {
            whoSaysWhat.put(mWho, mText);
            return false;
        }

        if (whoSaysWhat.containsKey(mWho)) {
            if (whoSaysWhat.get(mWho).equals(mText))
                return true;
            else
                whoSaysWhat.replace(mWho, mText);
            return false;
        }
        whoSaysWhat.put(mWho, mText);
        return false;
    }

}