package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.aBar;
import static com.urrecliner.chattalk.Vars.logSave;
import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.Vars.sharedEditor;
import static com.urrecliner.chattalk.Vars.topTabs;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.TypefaceSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

public class Fragment_2Saved extends Fragment {

    ViewGroup rootView;

    SpannableString ss, sv;
    EditText etTable, etKeyword;
    ImageView ivFind, ivNext;
    Menu mainMenu;
    Typeface font1, font2;
    int logPos = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(
                R.layout.frag2_log_saved, container, false);
        etTable = rootView.findViewById(R.id.text_log2);
        etKeyword = rootView.findViewById(R.id.keyword2);
        ivFind = rootView.findViewById(R.id.find2);
        ivNext = rootView.findViewById(R.id.next2);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onResume() {
        topTabs.getTabAt(2).select();
        etTable.setText(logSave2Spannable());
        setEditable();
        ivNext.setVisibility(View.GONE);
        logPos = -1;
        ivFind.setOnClickListener(v -> {
            String key = etKeyword.getText().toString();
            if (key.length() < 2)
                return;
            int cnt = 0;
            logPos = -1;
            String fullText = etTable.getText().toString();
            ss = sv;
            int oEnd = fullText.indexOf(key);
            for (int oStart = 0; oStart < fullText.length() && oEnd != -1; oStart = oEnd + 2) {
                oEnd = fullText.indexOf(key, oStart);
                if (oEnd > 0) {
                    cnt++;
                    if (logPos < 0)
                        logPos = oEnd;
                    ss.setSpan(new BackgroundColorSpan(0xFFFFFF00), oEnd, oEnd + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            sv = ss;
            etTable.setText(ss);
            new Utils().showSnackBar(key, cnt+" times Found");
            Editable etText = etTable.getText();
            if (logPos > 0) {
                Selection.setSelection(etText, logPos);
                etTable.requestFocus();
                ivNext.setVisibility(View.VISIBLE);
            }
        });

        ivNext.setOnClickListener(v -> {
            String key = etKeyword.getText().toString();
            if (key.length() < 2)
                return;
            Editable edText = etTable.getText();
            String s = edText.toString();
            logPos = s.indexOf(key, logPos+1);
            if (logPos > 0) {
                Selection.setSelection(edText, logPos);
                etTable.requestFocus();
            }
        });

        new Handler().post(() -> {
            ScrollView scrollView1 = rootView.findViewById(R.id.scroll_2_log);
            scrollView1.smoothScrollBy(0, 40000);
        });
        super.onResume();

    }

    private void setEditable() {
        etTable.setFocusableInTouchMode(true);
        etTable.setFocusable(true);
    }

    SpannableString logSave2Spannable() {

        font1 = Typeface.create(ResourcesCompat.getFont(mContext, R.font.mayplestory), Typeface.NORMAL);
        font2 = Typeface.create(ResourcesCompat.getFont(mContext, R.font.cookie_run), Typeface.NORMAL);

        int nPos = 0, sLen;
        int foreColor,backColor;
        Typeface font;
        ss = new SpannableString(logSave);
        String[] msgLine = logSave.split("\n");
        boolean changeColor = true;
        for (String s : msgLine) {
            sLen = s.length();
            if (sLen == 0) {
                nPos += 1;
                continue;
            }
            if (changeColor) {
                    foreColor = mContext.getColor(R.color.log_head_f1);
                    backColor = mContext.getColor(R.color.log_head_b0);
                    font = font2;
            } else {
                    foreColor = mContext.getColor(R.color.log_head_f0);
                    backColor = mContext.getColor(R.color.log_head_b0);
                    font = font1;
            }
            changeColor = !changeColor;

            ss.setSpan(new BackgroundColorSpan(backColor), nPos, nPos + sLen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(foreColor), nPos, nPos + sLen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new TypefaceSpan(font), nPos, nPos + sLen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            ss.setSpan(new RelativeSizeSpan(fontSize), nPos, nPos + sLen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            nPos += sLen + 1;
        }
        sv = ss;
        return ss;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        mainMenu = menu;
        inflater.inflate(R.menu.menu_2log_save, menu);
        super.onCreateOptionsMenu(menu, inflater);
        aBar.setTitle("  Saved Log");
        aBar.setSubtitle(null);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.line_delete_item) {
            String logNow = etTable.getText().toString().trim() + "\n";
            int posCurr = etTable.getSelectionStart();
            int posStart = logNow.lastIndexOf("\n", posCurr - 1);
            int posFinish = logNow.indexOf("\n", posCurr);
            if (posFinish == -1)
                posFinish = logNow.length();
            int prevStart = logNow.lastIndexOf("\n", posStart - 2);
            if (prevStart == -1)
                prevStart = 1;
            if (logNow.charAt(prevStart - 1) == '\n')
                logNow = logNow.substring(0, prevStart - 1) + logNow.substring(posFinish);
            else
                logNow = logNow.substring(0, prevStart) + logNow.substring(posFinish);
            logSave = logNow.replace("    ","");
            sharedEditor.putString("logSave", logSave);
            sharedEditor.apply();
            etTable.setText(logSave2Spannable());
            if (prevStart >= logSave.length())
                prevStart = logSave.length();
            etTable.setSelection(prevStart-1);

        } else if (item.getItemId() == R.id.line_delete_one) {
            String logNow = etTable.getText().toString().trim() + "\n";
            int posCurr = etTable.getSelectionStart();
            int posStart = logNow.lastIndexOf("\n", posCurr - 1);
            if (posStart == -1)
                posStart = 0;
            int posFinish = logNow.indexOf("\n", posCurr);
            if (posFinish == -1)
                posFinish = logNow.length();
            logSave = logNow.substring(0, posStart) + logNow.substring(posFinish);
            logSave = logSave.replace("    ","");
            sharedEditor.putString("logSave", logSave);
            sharedEditor.apply();
            etTable.setText(logSave2Spannable());
            etTable.setSelection(posStart);

        } else if (item.getItemId() == R.id.save_log_save) {
            logSave = etTable.getText().toString().trim() + "\n";
            logSave = logSave.replace("    ","");
            sharedEditor.putString("logSave", logSave);
            sharedEditor.apply();
            etTable.setText(logSave2Spannable());
        }
        return super.onOptionsItemSelected(item);
    }

    OnBackPressedCallback callback;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback.remove();
    }
}

//            View view = mActivity.getCurrentFocus();
//            if (view != null) {
//                InputMethodManager imm = (InputMethodManager)
//                        mActivity.getSystemService(INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.SHOW_IMPLICIT);
//            }