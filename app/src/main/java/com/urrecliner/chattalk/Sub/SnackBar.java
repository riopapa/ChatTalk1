package com.urrecliner.chattalk.Sub;

import static com.google.android.material.snackbar.Snackbar.*;
import static com.urrecliner.chattalk.Vars.mActivity;
import static com.urrecliner.chattalk.Vars.mLayoutView;

import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.urrecliner.chattalk.R;

public class SnackBar {
    public void show(String title, String text) {

        if (mLayoutView == null) {
            mLayoutView = mActivity.findViewById(R.id.main_layout);
        }
        Snackbar snackbar = make(mLayoutView, "", LENGTH_SHORT);
        View sView = mActivity.getLayoutInflater().inflate(R.layout.snack_message, null);

        TextView tv1 = sView.findViewById(R.id.text_header);
        TextView tv2 = sView.findViewById(R.id.text_body);

        tv1.setText(title);
        tv2.setText(text);

        // now change the layout of the ToastText
        SnackbarLayout snackBarLayout = (SnackbarLayout) snackbar.getView();
        snackBarLayout.setBackgroundColor(0x0033FFFF);  // transparent background
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)snackBarLayout.getLayoutParams();
        params.gravity = Gravity.CENTER_VERTICAL;
        sView.setLayoutParams(params);
        snackBarLayout.addView(sView, 0);

        snackbar.show();
    }

}
