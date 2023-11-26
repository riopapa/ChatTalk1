package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.NotificationListener.vars;
import static com.urrecliner.chattalk.Vars.aGroups;
import static com.urrecliner.chattalk.Vars.nowFileName;
import static com.urrecliner.chattalk.Vars.tableFolder;
import static com.urrecliner.chattalk.Vars.tableListFile;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

public class ActivityStringReplace extends AppCompatActivity {

    final String dummyHead = "- [ ";
    int pos = -1;
    EditText et;
    String key, fullText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_edit);
        ActionBar ab = getSupportActionBar() ;
        assert ab != null;
        ab.setTitle("   Str Replaces ");

        EditText tv = findViewById(R.id.table_text);
        if (tableFolder == null)
            vars = new Vars(this);
        File file = new File(tableFolder, nowFileName + ".txt");
        String[] lines = tableListFile.readRaw(file);
        String text;

        ImageView ivSearch = findViewById(R.id.search);
        ivSearch.setOnClickListener(v -> {
            int cnt = 0;
            et = findViewById(R.id.key_que);
            key = et.getText().toString();           // .replace(" ","\u00A0");
            fullText = tv.getText().toString().trim();
            tv.setText(fullText);   // reset previous searched color
            Spannable Word2Span = new SpannableString( tv.getText() );
            int offsetEnd = fullText.indexOf(key);
            for(int offsetStart=0;offsetStart<fullText.length() && offsetEnd!=-1;offsetStart=offsetEnd+1) {
                offsetEnd = fullText.indexOf(key,offsetStart);
                if(offsetEnd > 0) {
                    if (cnt == 0)
                        pos = offsetEnd;
                    cnt++;
                    Word2Span.setSpan(new BackgroundColorSpan(0xFFFFFF00), offsetEnd, offsetEnd+key.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv.setText(Word2Span, TextView.BufferType.SPANNABLE);
                }
            }
            if (pos > 0)
                tv.setSelection(pos, pos+key.length());
            Toast.makeText(this, "Total "+cnt+" words found", Toast.LENGTH_SHORT).show();
        });

        ImageView ivSearchNxt = findViewById(R.id.searchNext);
        ivSearchNxt.setOnClickListener(v -> {
            pos = tv.getText().toString().indexOf(key, pos + key.length());
            if (pos > 0)
                tv.setSelection(pos, pos+key.length());
        });
        text = insertHeader(lines);
        tv.setText(text);
        tv.setFocusable(true);
        tv.setEnabled(true);
        tv.setClickable(true);
        tv.setFocusableInTouchMode(true);
    }

    String insertHeader(String [] textLines) {
        String svGroup = "x";
        StringBuilder sb = new StringBuilder();
        for (String textLine: textLines) {
            if (textLine.length() < 2)
                continue;
            String [] oneL = textLine.split("\\^");
            if (!oneL[0].equals(svGroup)) {
                svGroup = oneL[0];
                int gIdx = Collections.binarySearch(aGroups, svGroup);
                String del = (gIdx >= 0) ? "":" // 없는 그룹 //";
                sb.append(dummyHead).append(svGroup).append(del).append(" ] -\n\n");    // dummy some chars between groups
            }
            sb.append(textLine).append("\n").append("\n");
        }
        return sb.toString();
    }
    String removeHeader(String txt) {
        String[] arrText = txt.split("\n");
        Arrays.sort(arrText);
        StringBuilder sortedText = new StringBuilder();
        String sv = "";
        for (String t : arrText) {
            if (t.length() < 3 || t.startsWith(dummyHead)) // ignore if not "20^주식^aa^some text blank lines"
                continue;
            t = t.trim();
            if (!sv.equals(t.substring(0, 3))) {
                sortedText.append("\n");
                sv = t.substring(0,3);
            }
            sortedText.append(t).append("\n");
        }
        return sortedText.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_alert_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.save_table) {
            TextView tv = findViewById(R.id.table_text);
            String s = tv.getText().toString();             // .replace("\u00A0"," ");
            FileIO.writeFile(tableFolder, nowFileName, removeHeader(s),".txt");
            new OptionTables().readAll();
            finish();
        } else if (item.getItemId() == R.id.line_copy_add) {
            EditText et = findViewById(R.id.table_text);
            String strNow = et.getText().toString();
            StringBuilder sb = new StringBuilder(strNow);
            int pos = et.getSelectionStart();
            String s = getClipBoard();
            sb.insert(pos, s);
            et.setText(sb.toString());
            et.setSelection(pos+1);
        } else if (item.getItemId() == R.id.remove_this_line) {
            EditText et = findViewById(R.id.table_text);
            String strNow = et.getText().toString();
            int lineF = et.getSelectionStart();
            int lineS = strNow.lastIndexOf("\n", lineF-1);
            StringBuilder sb = new StringBuilder(strNow);
            sb.replace(lineS, lineF,"");
            et.setText(sb.toString());
            et.setSelection(lineS);
        }
        return false;
    }

    String getClipBoard() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData pData = clipboard.getPrimaryClip();
        ClipData.Item item = pData.getItemAt(0);
        return "\n^단축^"+item.getText().toString()+"\n";
    }

}