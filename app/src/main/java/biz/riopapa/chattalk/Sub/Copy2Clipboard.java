package biz.riopapa.chattalk.Sub;

import static android.content.Context.CLIPBOARD_SERVICE;
import static biz.riopapa.chattalk.Vars.mContext;

import android.content.ClipData;
import android.content.ClipboardManager;

public class Copy2Clipboard {
    public Copy2Clipboard(String s) {
        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("copy", s);
        clipboard.setPrimaryClip(clip);
    }
}
