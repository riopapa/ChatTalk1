package com.urrecliner.chattalk.Sub;

public class StockName {
    public String parse(String prev, String next, String iText) {
        String str = iText, sName;
        int p1 = str.indexOf(prev);
        if (p1 >= 0) {
            str = str.substring(p1+prev.length());
            p1 = str.indexOf(next);
            if (p1 > 0) {
                sName = str.substring(0, p1).replaceAll("[\\d,%:|#+()\\s]", "").trim();
                if (sName.length() > 10)
                    sName = sName.substring(0,8);
            }
            else
                sName = str.substring(0,8);
            return sName;
        }
        return "No Prev";
    }
}
