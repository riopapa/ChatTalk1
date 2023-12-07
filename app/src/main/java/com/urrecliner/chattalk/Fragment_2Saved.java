package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.ActivityMain.fragNumber;
import static com.urrecliner.chattalk.Vars.aBar;
import static com.urrecliner.chattalk.Vars.logSave;
import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.Vars.sharedEditor;
import static com.urrecliner.chattalk.Vars.topTabs;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.urrecliner.chattalk.Sub.LogSpann;
import com.urrecliner.chattalk.Sub.SnackBar;

public class Fragment_2Saved extends Fragment {

    ViewGroup rootView;

    SpannableString ss, sv;
    EditText etTable, etKeyword;
    ImageView ivFind, ivNext;
    Menu mainMenu;
    int logPos = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(
                R.layout.frag2_saved, container, false);
        etTable = rootView.findViewById(R.id.text_log2);
        etKeyword = rootView.findViewById(R.id.keyword2);
        ivFind = rootView.findViewById(R.id.find2);
        ivNext = rootView.findViewById(R.id.next2);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onResume() {
        fragNumber = 2;
        topTabs.getTabAt(fragNumber).select();
        aBar.setTitle(topTabs.getTabAt(fragNumber).getText().toString());
        aBar.setSubtitle(null);

        logSave = logSave.replace("    ","");
        ss = new LogSpann().make(logSave, mContext);
        sv = ss;
        etTable.setText(ss);
        etTable.setFocusableInTouchMode(true);
        etTable.setFocusable(true);

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
            new SnackBar().show(key, cnt+" times Found");
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

        new Handler(Looper.getMainLooper()).post(() -> {
            ScrollView scrollView1 = rootView.findViewById(R.id.scroll_2_log);
            scrollView1.smoothScrollBy(0, 40000);
        });
        rootView.invalidate();
        super.onResume();

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        mainMenu = menu;
        inflater.inflate(R.menu.menu_2save, menu);
        super.onCreateOptionsMenu(menu, inflater);
        aBar.setTitle(topTabs.getTabAt(2).getText().toString());
        aBar.setSubtitle(null);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.line_delete_item) {
            String logNow = etTable.getText().toString().trim() + "\n";
            int ps = logNow.lastIndexOf("\n", etTable.getSelectionStart() - 1);
            int pf = logNow.indexOf("\n", ps + 1 );
            ps = logNow.lastIndexOf("\n", ps - 1);
            if (ps == -1)
                ps = 1;
            if (logNow.charAt(ps - 1) == '\n')
                logSave = logNow.substring(0, ps - 1) + logNow.substring(pf);
            else
                logSave = logNow.substring(0, ps) + logNow.substring(pf);
            sharedEditor.putString("logSave", logSave);
            sharedEditor.apply();
            etTable.setText(new LogSpann().make(logSave, mContext));
            pf = ps - 1;
            ps = logSave.lastIndexOf("\n", pf - 1) + 1;
            etTable.setSelection(ps, pf);

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
            etTable.setText(new LogSpann().make(logSave, mContext));
            etTable.setSelection(posStart);

        } else if (item.getItemId() == R.id.save_log_save) {
            logSave = etTable.getText().toString().trim() + "\n";
            logSave = logSave.replace("    ","");
            sharedEditor.putString("logSave", logSave);
            sharedEditor.apply();
            etTable.setText(new LogSpann().make(logSave, mContext));
        }
        return super.onOptionsItemSelected(item);
    }

}
