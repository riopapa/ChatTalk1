package com.urrecliner.chattalk.Sub;

import com.urrecliner.chattalk.Vars;

import java.util.ArrayList;
import java.util.Collections;

public class IsWhoText {

    public static boolean repeated(ArrayList<Vars.WhoText> whoSaysWhat, String mWho, String mText) {
        if (whoSaysWhat.isEmpty()) {
            whoSaysWhat.add(new Vars.WhoText(mWho, mText));
            return false;
        }
        int whoSaySize = whoSaysWhat.size();
        for (int i = 0; i < whoSaySize; i++) {
            int compared = whoSaysWhat.get(i).who.compareTo(mWho);
            if (compared == 0) {
                if (whoSaysWhat.get(i).text.equals(mText))
                    return true;
                whoSaysWhat.set(i, new Vars.WhoText(mWho, mText));
            } else if (compared > 0) {
                whoSaysWhat.add(new Vars.WhoText(mWho, mText));
                Collections.sort(whoSaysWhat,
                        (o1, o2) -> o1.who.compareTo(o2.who));
                return false;
            }
        }
        return false;
    }

}