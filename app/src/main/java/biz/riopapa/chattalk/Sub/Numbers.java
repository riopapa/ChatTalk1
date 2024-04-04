package biz.riopapa.chattalk.Sub;

public class Numbers {
    public String deduct(String str) {
        return str.replaceAll("[\\d,:/]", "");
    }
}
