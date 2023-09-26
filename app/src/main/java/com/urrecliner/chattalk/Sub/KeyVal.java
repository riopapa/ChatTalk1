package com.urrecliner.chattalk.Sub;

import java.util.HashMap;
import java.util.Map;

public class KeyVal {

    Map<String, String> maps;
    boolean exist;
    public KeyVal () {
        maps = new HashMap<>();
    }
    public boolean isDup(String key, String val) {
        exist = false;
        if (maps == null) {
            maps = new HashMap<>();
            maps.put(key, val);
            return false;
        } else if (maps.containsKey(key)) {
            if (maps.get(key).equals(val))
                return true;
            maps.replace(key, val);
            return false;
        }
        maps.put(key, val);
        return false;
    }
}