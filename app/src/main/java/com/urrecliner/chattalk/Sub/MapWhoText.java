package com.urrecliner.chattalk.Sub;

import java.util.HashMap;
import java.util.Objects;

public class MapWhoText {

    public static boolean repeated(HashMap<String, String> whoSaysWhat, String mWho, String mText) {
        if (whoSaysWhat == null || whoSaysWhat.isEmpty()) {
            whoSaysWhat = new HashMap<>();
            whoSaysWhat.put(mWho, mText);
            return false;
        }

        if (whoSaysWhat.containsKey(mWho)) {
            if (Objects.equals(whoSaysWhat.get(mWho), mText))
                return true;
            else
                whoSaysWhat.replace(mWho, mText);
            return false;
        }
        whoSaysWhat.put(mWho, mText);
        return false;
    }

}