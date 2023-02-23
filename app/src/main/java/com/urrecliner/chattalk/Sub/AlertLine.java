package com.urrecliner.chattalk.Sub;

public class AlertLine {
    public String group, who, key1, key2, talk, skip, memo, more, prev, next;
    public int matched;
    public AlertLine(String group, String who, String key1, String key2, String talk, int matched, String skip, String memo, String more, String prev, String next) {
        this.group = group;this.who = who;
        this.key1 = key1;this.key2 = key2;this.talk = talk;
        this.matched = matched; this.skip = skip; this.memo = memo; this.more = more;
        this.prev = prev; this.next = next;
    }
}