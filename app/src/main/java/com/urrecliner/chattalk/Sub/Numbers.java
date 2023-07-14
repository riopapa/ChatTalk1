package com.urrecliner.chattalk.Sub;

public class Numbers {
    public String out(String str) {
        return str.replaceAll("[\\d,:/]", "");
    }
}
