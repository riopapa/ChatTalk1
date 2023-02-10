package com.urrecliner.chattalk.Sub;


import java.io.UnsupportedEncodingException;

public class ByteLength {

    public static int get(String str) {
        try {
            return str.getBytes("euc-kr").length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str.length();
    }

}