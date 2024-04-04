package biz.riopapa.chattalk.Sub;


import android.util.Log;
import java.io.UnsupportedEncodingException;

public class ByteLength {

    public static int get(String str) {
        try {
            return str.getBytes("euc-kr").length;
        } catch (UnsupportedEncodingException e) {
            Log.e("ByteLength","Encoding Error \n" + e);
        }
        return str.length();
    }

}