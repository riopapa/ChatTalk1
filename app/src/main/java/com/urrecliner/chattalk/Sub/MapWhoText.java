package com.urrecliner.chattalk.Sub;

import java.util.HashMap;
import java.util.Objects;

public class MapWhoText {

    public boolean repeated(HashMap<String, String> whoSaysWhat, String mWho, String mText) {

        if (whoSaysWhat == null) {
            build(whoSaysWhat, mWho, mText);
            return false;
        } else if (whoSaysWhat.containsKey(mWho)) {
            if (Objects.equals(whoSaysWhat.get(mWho), mText))
                return true;
            else
                whoSaysWhat.replace(mWho, mText);
            return false;
        }
        whoSaysWhat.put(mWho, mText);
        return false;
    }

    public void build(HashMap<String, String> whoSaysWhat, String mWho, String mText) {
        whoSaysWhat = new HashMap<>();
        whoSaysWhat.put(mWho, mText);
    }
}