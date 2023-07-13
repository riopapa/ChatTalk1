package com.urrecliner.chattalk.Sub;

public class StockName {

    // returns stockname, and dot added iText
    final String shorten = "[\\d,%:|#+()]";
    public String[] parse(String prev, String next, String iText) {
        String str = iText;
        String sName;
        int p1 = iText.indexOf(prev);
        if (p1 >= 0) {
            p1 += prev.length() + 1;
            int p2 = str.indexOf(next, p1);
            if (p2 > 0) {
                sName = str.substring(p1, p2).replaceAll(shorten, "").trim();
                if (sName.length() > 8)
                    sName = sName.substring(0,8);
                str = str.substring(0, p1) + new StringBuffer(sName).insert(1, ".")
                        + str.substring(p2);
            } else {
                sName = str.substring(p1, p1+10).replaceAll(shorten, "").trim();
                str = str.substring(0, p1) + new StringBuffer(sName).insert(1, ".")
                        + str.substring(p1+10);
            }
            return new String[]{sName, str};
        }
        return new String[]{"noPrv", str};
    }
}
