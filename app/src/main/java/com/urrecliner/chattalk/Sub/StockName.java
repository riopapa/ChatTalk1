package com.urrecliner.chattalk.Sub;

public class StockName {
    public String parse(String prev, String next, String iText) {
        String str = iText;
        int p1 = str.indexOf(prev);
        if (p1 >= 0) {
            str = str.substring(p1+prev.length());
            p1 = str.indexOf(next);
            if (p1 > 0)
                return str.substring(0,p1).replaceAll("[\\d,%:|#+()\\s]","").trim();
            return "No Next";
        }
        return "No Prev";
    }
}
