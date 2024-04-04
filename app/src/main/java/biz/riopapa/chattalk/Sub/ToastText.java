package biz.riopapa.chattalk.Sub;

import static biz.riopapa.chattalk.Vars.mActivity;
import static biz.riopapa.chattalk.Vars.mContext;

import android.widget.Toast;

public class ToastText {
    public void show(String text) {    // 0: short 1:long
        mActivity.runOnUiThread(() -> {
            Toast toast = Toast.makeText(mContext,text, Toast.LENGTH_LONG);
            toast.show();
        });
    }
}
