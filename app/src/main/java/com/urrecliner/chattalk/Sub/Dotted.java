package com.urrecliner.chattalk.Sub;

public class Dotted {
    public String make(String s) {
        String stockDot = "";
        for (int i = 0; i < s.length() ; i++)
            stockDot += s.charAt(i)+".";
        return stockDot;
    }
}
