package com.urrecliner.chattalk.Sub;

import static com.urrecliner.chattalk.Vars.mContext;

import android.widget.Toast;

public class ToastText {
    public void show(String text) {    // 0: short 1:long
        Toast toast = Toast.makeText(mContext,"\n"+text+"\n", Toast.LENGTH_LONG);
        toast.show();
    }

}
