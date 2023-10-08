package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.aBar;
import static com.urrecliner.chattalk.Vars.downloadFolder;
import static com.urrecliner.chattalk.Vars.logQue;
import static com.urrecliner.chattalk.Vars.logSave;
import static com.urrecliner.chattalk.Vars.mActivity;
import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.Vars.sharedEditor;
import static com.urrecliner.chattalk.Vars.tableFolder;
import static com.urrecliner.chattalk.Vars.topTabs;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.urrecliner.chattalk.Sub.LogSpann;
import com.urrecliner.chattalk.Sub.SnackBar;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class Fragment_1Logs extends Fragment {

    ViewGroup rootView;
    ScrollView scrollView1;
    SpannableString ss, sv;
    EditText etTable, etKeyword;
    ImageView ivFind, ivClear, ivNext;
    Menu mainMenu;
    int logPos = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(
                R.layout.frag1_que, container, false);
        etTable = rootView.findViewById(R.id.text_que);
        etKeyword = rootView.findViewById(R.id.key_que);
        ivFind = rootView.findViewById(R.id.find_que);
        ivNext = rootView.findViewById(R.id.next_que);
        ivClear = rootView.findViewById(R.id.clear_que);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onResume() {
        topTabs.getTabAt(1).select();
        ss = new LogSpann().make(logQue, mContext);
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
            Editable etText = etTable.getText();
            String s = etText.toString();
            logPos = s.indexOf(key, logPos+1);
            if (logPos > 0) {
                Selection.setSelection(etText, logPos);
                etTable.requestFocus();
            }
        });

        ivClear.setOnClickListener(v -> etKeyword.setText(""));
        scrollView1 = rootView.findViewById(R.id.scroll_1_que);
        new Handler(Looper.getMainLooper()).post(() -> scrollView1.smoothScrollBy(0, 90000));
        super.onResume();

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        mainMenu = menu;
        inflater.inflate(R.menu.menu_1que, menu);
        super.onCreateOptionsMenu(menu, inflater);
        aBar.setTitle(topTabs.getTabAt(1).getText().toString());
        aBar.setSubtitle(null);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.delete_one_set) {
            showNextQue(new LogSpann().delOneSet(etTable.getText().toString(),
                    etTable.getSelectionStart(), mContext));

        } else if (item.getItemId() == R.id.action_reload) {
            reload_loqQue();

        } else if (item.getItemId() == R.id.delete_1_line) {
            showNextQue(new LogSpann().delOneLine(etTable.getText().toString(),
                    etTable.getSelectionStart(), mContext));

        } else if (item.getItemId() == R.id.copy2log) {
            String logNow = etTable.getText().toString().trim() + "\n";
            int ps = logNow.lastIndexOf("\n", etTable.getSelectionStart() - 1);
            if (ps == -1)
                ps = 0;
            int pf = logNow.indexOf("\n", ps + 1);
            if (pf == -1)
                pf = logNow.length();

            ps = logNow.lastIndexOf("\n", ps - 1);
            if (ps == -1)
                ps = 0;
            else
                ps = logNow.lastIndexOf("\n", ps - 1);

            String copied = logNow.substring(ps+1, pf);
            logSave += "\n" + copied;
            sharedEditor.putString("logSave", logSave);
            sharedEditor.apply();
            copied = copied.replace("\n", " ▶️ ");
            Toast.makeText(mContext, "que copied " + copied, Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showNextQue(Vars.DelItem delItem) {
//        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(etTable.getWindowToken(), 0);
        logQue = delItem.logNow;
        sharedEditor.putString("logQue", logQue);
        sharedEditor.apply();
        etTable.setText(delItem.ss);
        scrollView1.post(() -> {
            new Timer().schedule(new TimerTask() {
                public void run() {
                    mActivity.runOnUiThread(() -> {
                        Editable etText = etTable.getText();
                        Selection.setSelection(etText, delItem.ps, delItem.pf);
                        etTable.requestFocus();
                    });
                }
            }, 50);
        });
    }

    private void reload_loqQue() {
        if (tableFolder ==  null) {
            downloadFolder = new File(Environment.getExternalStorageDirectory(), "download");
            tableFolder = new File(downloadFolder, "_ChatTalk");
        }
        String [] que = new FileIO().readKR(new File(tableFolder, "logQue.txt").toString());
        StringBuilder sb = new StringBuilder();
        for (String s: que) {
            sb.append(s).append("\n");
        }
        logQue = sb.toString();
        etTable.setText(new LogSpann().make(logQue, mContext));
    }

}