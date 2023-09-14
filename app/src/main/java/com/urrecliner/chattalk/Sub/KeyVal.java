package com.urrecliner.chattalk.Sub;

import java.util.HashMap;
import java.util.Map;

public class KeyVal {

    Map<String, String> maps = null;
    boolean exist;
    public KeyVal () {
        maps = new HashMap<>();
    }
    public boolean check (String key, String val) {
        exist = false;
        if (maps == null) {
            maps = new HashMap<>();
            maps.put(key, val);
        } else if (maps.containsKey(key)) {
            if (maps.get(key).equals(val))
                exist = true;
            else {
                maps.replace(key, val);
            }
        } else
            maps.put(key, val);
        return exist;
    }

}