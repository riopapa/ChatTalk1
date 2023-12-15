package com.urrecliner.chattalk.alerts;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class StockName {

    // returns stockname, and dot added iText
    final String shorten = "[\\d,%:|#+()/]";
    public String[] get(String prev, String next, String iText) {
        String str = iText;
        int p1 = iText.indexOf(prev);
        if (p1 >= 0) {
            p1 += prev.length();
            int p2 = str.indexOf(next, p1);
            String sName;
            if (p2 > 0) {
                sName = str.substring(p1, p2).replaceAll(shorten, "").trim();
                if (sName.length() > 8)
                    sName = sName.substring(0,8);
                str = str.substring(0, p1) + " " +
                        new StringBuffer(sName).insert(1, ".") + " " +
                        str.substring(p2);
            } else {    // if second keyword not found then just before blank found

                p1 = p1 + 1;
                while (true) {  // skip white
                    char ch = str.charAt(p1);
                    if (ch >= 0xAC00 && ch <= 0xD7A3)
                        break;
                    if (ch >= 'A' && ch <= 'Z')
                        break;
                    p1++;
                }
                p2 = p1 + 2;
                while (true) {  // until valid chars
                    char ch = str.charAt(p2);
                    if ((ch >= 0xAC00 && ch <= 0xD7A3) ||(ch >= 'A' && ch <= 'Z')) {
                        p2++;
                        continue;
                    }
                    break;
                }

                sName = str.substring(p1,p2);
                if (sName.length()> 2)
                    sName = new StringBuffer(sName).insert(1, ".").toString();
                str = str.substring(0, p1) + " " + sName + " " +
                        str.substring(p1+10);
            }
            return new String[]{sName, str};
        }
        return new String[]{"noPrv", str};
    }
}
