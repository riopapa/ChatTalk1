package com.urrecliner.chattalk;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.urrecliner.chattalk.Vars.aBar;
import static com.urrecliner.chattalk.Vars.logSave;
import static com.urrecliner.chattalk.Vars.logStock;
import static com.urrecliner.chattalk.Vars.mActivity;
import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.Vars.sharedEditor;
import static com.urrecliner.chattalk.Vars.tableFolder;
import static com.urrecliner.chattalk.Vars.topTabs;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class Fragment_3Stock extends Fragment {

    ViewGroup rootView;
    ScrollView scrollView1;
    SpannableString ss, sv;
    EditText etTable, etKeyword;
    ImageView ivFind, ivClear, ivNext;
    Menu mainMenu;
    Typeface font1, font2;
    int logPos = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(
                R.layout.frag3_stock, container, false);
        rootView.setBackgroundColor(getContext().getColor(R.color.colorLine));
        etTable = rootView.findViewById(R.id.text_stock);
        etKeyword = rootView.findViewById(R.id.key_stock);
        ivFind = rootView.findViewById(R.id.find_stock);
        ivNext = rootView.findViewById(R.id.next_stock);
        ivClear = rootView.findViewById(R.id.clear_stock);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onResume() {
        topTabs.getTabAt(3).select();
        etTable.setText(logStock2Spannable());
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
            Editable etText = etTable.getText();
            String s = etText.toString();
            logPos = s.indexOf(key, logPos+1);
            if (logPos > 0) {
                Selection.setSelection(etText, logPos);
                etTable.requestFocus();
            }
        });

        ivClear.setOnClickListener(v -> etKeyword.setText(""));
        scrollView1 = rootView.findViewById(R.id.scroll_3_stock);
        new Handler(Looper.getMainLooper()).post(() -> scrollView1.smoothScrollBy(0, 10000));
        super.onResume();

    }

    private void setEditable() {
        etTable.setFocusableInTouchMode(true);
//        etTable.setEnabled(true);
        etTable.setFocusable(true);
    }

    SpannableString logStock2Spannable() {

        font1 = Typeface.create(ResourcesCompat.getFont(mContext, R.font.mayplestory), Typeface.NORMAL);
        font2 = Typeface.create(ResourcesCompat.getFont(mContext, R.font.cookie_run), Typeface.NORMAL);
        boolean newItem = false;
        int nPos = 0, sLen;

        int [][]colors = new int[2][];

        colors[0] = new int[]{
                mContext.getColor(R.color.log_head_f0), mContext.getColor(R.color.log_head_b0),
                mContext.getColor(R.color.log_line_f0), mContext.getColor(R.color.log_line_b0),
                mContext.getColor(R.color.log_line_x0)
        };
        colors[1] = new int[]{
                mContext.getColor(R.color.log_head_f1), mContext.getColor(R.color.log_head_b1),
                mContext.getColor(R.color.log_line_f1), mContext.getColor(R.color.log_line_b1),
                mContext.getColor(R.color.log_line_x1)
        };

        int colorIdx = 0;
        int colorFore, colorBack;

        Typeface font = font1;
        ss = new SpannableString(logStock);
        String[] msgLine = logStock.split("\n");
        for (String s : msgLine) {
            sLen = s.length();
            if (sLen == 0) {
                nPos += 1;
                continue;
            }
            if (s.contains("/**")) {    // new date separator
                colorIdx = (colorIdx + 1) % 2;
                colorFore = colors[colorIdx][0];
                colorBack = colors[colorIdx][1];
                newItem = true;
            } else if (StringUtils.isNumeric(String.valueOf(s.charAt(0)))) {  // timestamp + who
                colorFore = colors[colorIdx][0];
                colorBack = colors[colorIdx][1];
                newItem = true;
            }
            else {
                colorFore = colors[colorIdx][2];
                if (newItem) {
                    colorBack = colors[colorIdx][3];
                } else {
                    colorBack = colors[colorIdx][4];
                }
                newItem = false;
            }

            int endPos = nPos + sLen;
            ss.setSpan(new ForegroundColorSpan(colorFore), nPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new TypefaceSpan(font), nPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new BackgroundColorSpan(colorBack), nPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            ss.setSpan(new RelativeSizeSpan(fontSize), nPos, nPos + sLen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            nPos += sLen + 1;
        }
        sv = ss;
        return ss;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        mainMenu = menu;
        inflater.inflate(R.menu.menu_3stock, menu);
        super.onCreateOptionsMenu(menu, inflater);
        aBar.setTitle("  Today Log");
        aBar.setSubtitle(null);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.delete_item_stock) {
            delete_OneItem();

        } else if (item.getItemId() == R.id.action_restock) {
            reload_stock();

        } else if (item.getItemId() == R.id.delete_1line_stock) {
            delete_OneLine();

        } else if (item.getItemId() == R.id.copy2stock) {
            String logNow = etTable.getText().toString().trim() + "\n";
            int posCurr = etTable.getSelectionStart();
            int posStart = logNow.lastIndexOf("\n", posCurr - 1);
            if (posStart == -1)
                posStart = 0;
            int posFinish = logNow.indexOf("\n", posCurr);
            if (posFinish == -1)
                posFinish = logNow.length();

            posStart = logNow.lastIndexOf("\n", posStart - 1);
            if (posStart == -1)
                posStart = 0;
            else {
                posStart = logNow.lastIndexOf("\n", posStart - 1);
            }
            String copied = logNow.substring(posStart, posFinish);
            logSave += copied;
            sharedEditor.putString("logSave", logSave);
            sharedEditor.apply();
            copied = copied.replace("\n", " 🗼️ ");
            Toast.makeText(mContext, "stock copied " + copied, Toast.LENGTH_SHORT).show();

        }
        return super.onOptionsItemSelected(item);
    }

    private void delete_OneLine() {
        String logNow = etTable.getText().toString().trim() + "\n";
        int posCurr = etTable.getSelectionStart();
        int posStart = logNow.lastIndexOf("\n", posCurr - 1);
        if (posStart == -1)
            posStart = 0;
        int posFinish = logNow.indexOf("\n", posStart+1);
        if (posFinish == -1)
            posFinish = logNow.length() - 2;
        logStock = logNow.substring(0, posStart) + logNow.substring(posFinish);
        logStock = logStock.replace("    ","");
        sharedEditor.putString("logStock", logStock);
        sharedEditor.apply();
        SpannableString ss = logStock2Spannable();
        posStart = logStock.lastIndexOf("\n", posCurr - 1) + 1;
        posFinish = logStock.indexOf("\n", posStart+1) - 1;
        ss.setSpan(new StyleSpan(Typeface.ITALIC), posStart, posFinish, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        ss.setSpan(new UnderlineSpan(), posStart, posFinish,Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        etTable.setText(ss);
        Editable etText = etTable.getText();
        Selection.setSelection(etText, posStart, posFinish);
    }

    private void delete_OneItem() {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etTable.getWindowToken(), 0);
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
        logStock = logNow;
        logStock = logStock.replace("    ","");
        sharedEditor.putString("logStock", logStock);
        sharedEditor.apply();
        SpannableString ss = logStock2Spannable();
        if (prevStart >= logStock.length())
            prevStart = logStock.length();
        if (prevStart < 2)
            prevStart = 2;
        posCurr = logStock.lastIndexOf("\n", prevStart-1);
        if (posCurr < 0)
            posCurr = prevStart -3;
        ss.setSpan(new StyleSpan(Typeface.ITALIC), posCurr, prevStart-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new UnderlineSpan(), posCurr, prevStart-1,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        etTable.setText(ss);
        Editable etText = etTable.getText();
        Selection.setSelection(etText, posCurr, prevStart-1);
        etTable.requestFocus();
        scrollView1.post(() -> new Timer().schedule(new TimerTask() {
            public void run() {
                mActivity.runOnUiThread(() -> scrollView1.scrollBy(0, 100));
            }
        }, 30));
    }

    private void reload_stock() {
        String [] que = new FileIO().readKR(new File(tableFolder, "logStock.txt").toString());
        StringBuilder sb = new StringBuilder();
        for (String s: que) {
            sb.append(s);
        }
        logStock = sb.toString();
        etTable.setText(logStock2Spannable());
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