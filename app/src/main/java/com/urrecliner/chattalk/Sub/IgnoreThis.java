package com.urrecliner.chattalk.Sub;

public class IgnoreThis {

    public static boolean contains(String text, String[] lists) {
        if (text != null && lists != null) {
            for (String s : lists) {
                if (text.contains(s)) return true;
            }
        }
        return false;
    }
}