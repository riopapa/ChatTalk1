package com.urrecliner.chattalk.Sub;

public class Dot {
    public String add(String s) {
//        String stockDot = "";
//        for (int i = 0; i < s.length() ; i++)
//            stockDot += s.charAt(i)+".";
//        return stockDot;
        return new StringBuffer(s).insert(1, ".").toString();
    }
}
