package com.urrecliner.chattalk.Sub;

public class IgnoreNumber {

    public static boolean in(String[] nineWho, String mWho) {

        for (String s: nineWho) {
            if (s.equals(mWho))
                return true;
        }
        return false;
    }

}