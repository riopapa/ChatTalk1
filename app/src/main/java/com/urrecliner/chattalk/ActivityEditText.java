package com.urrecliner.chattalk;

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

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ActivityEditText extends AppCompatActivity {

    boolean isPackageNames;
    int pos = -1;
    EditText et;
    String key, fullText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_edit);
        isPackageNames = nowFileName.equals("appNames");
        ActionBar ab = getSupportActionBar() ;
        assert ab != null;
        ab.setTitle("  "+nowFileName);

        EditText tv = findViewById(R.id.table_text);
        File file = new File(tableFolder, nowFileName + ".txt");
        String[] lines = tableListFile.readRaw(file);
        String text;

        ImageView iv = findViewById(R.id.search);
        iv.setOnClickListener(v -> {
            int cnt = 0;
            et = findViewById(R.id.key_que);
            key = et.getText().toString();           // .replace(" ","\u00A0");
            fullText = tv.getText().toString();
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
            text = Arrays.stream(lines).map(s -> s + "\n").collect(Collectors.joining()) + "\n";
        tv.setText(text);
        tv.setFocusable(true);
        tv.setEnabled(true);
        tv.setClickable(true);
        tv.setFocusableInTouchMode(true);
    }

    String sortText(String txt) {
        String[] arrText = txt.split("\n");
        Arrays.sort(arrText);
        StringBuilder sortedText = new StringBuilder();
        Arrays.stream(arrText).filter(t -> txt.length() > 2).forEach(t -> sortedText.append(t).append("\n"));
        return sortedText.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_alert_edit, menu);
        return true;
    }

    private static String strPad(String s, int strLen) {
        String blank = StringUtils.repeat(" ", 60);
        s = s.trim();
        int byteLen = getByteLength(s);
        if (byteLen >= strLen)
            return s;
        int padL = (strLen - byteLen) / 2;
        int padR = strLen - byteLen - padL;
        return blank.substring(0, padL) + s + blank.substring(0, padR);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.save_table) {
            TextView tv = findViewById(R.id.table_text);
            String s = tv.getText().toString();             // .replace("\u00A0"," ");
            FileIO.writeFile(tableFolder, nowFileName,
                    (isPackageNames) ? sortPackage(s) : sortText(s),".txt");
            new OptionTables().readAll();
            finish();
        } else if (item.getItemId() == R.id.line_copy_add) {
            EditText et = findViewById(R.id.table_text);
            String logNow = et.getText().toString();
            StringBuilder sb=new StringBuilder(logNow);
            int pos = et.getSelectionStart();
            String s = getClipBoardText();
            sb.insert(pos, s);
            et.setText(sb.toString());
            et.setSelection(pos + s.length()-2);
        } else if (item.getItemId() == R.id.remove_this_line) {
            EditText et = findViewById(R.id.table_text);
            String logNow = et.getText().toString();
            int lineF = et.getSelectionStart();
            int lineS = logNow.lastIndexOf("\n", lineF-1);
            StringBuilder sb = new StringBuilder(logNow);
            sb.replace(lineS, lineF,"");
            et.setText(sb.toString());
            et.setSelection(lineS);
        }
        return false;
    }


    String sortPackage(String txt) {

        String[] arrText = txt.split("\n");
        for (int i = 0; i < arrText.length; i++)
            arrText[i] = arrText[i].trim();
        Arrays.sort(arrText);
        int maxLen = 0;
        for (String t : arrText) {
            if (t.length() < 2)
                continue;
            String[] fields = t.split("\\^");
            int len = fields[0].trim().length();
            if (len > maxLen)
                maxLen = len;
        }
        String blank = StringUtils.repeat(" ", maxLen);
        StringBuilder sortedText = new StringBuilder();
        for (String t : arrText) {
            if (t.length() < 2)
                continue;
            String[] fields = t.split("\\^");
            String memo= "";
            if (fields.length > 3)  // if nothing after ^ then no array return
                memo = fields[3].trim();
            String pkg = blank+fields[0].trim()+" ";
            String oneLine = pkg.substring(pkg.length()-maxLen-1) + "^" +  // package full name
                    strPad(fields[1], 12) + " ^ "           // package nick name
                    + strPad(fields[2], 4) + " ^ "          // yyn
                    + memo;                                       // comment
            sortedText.append(oneLine).append("\n");
        }
        return sortedText.toString();
    }

    static int getByteLength(String str) {
        try {
            return str.getBytes("euc-kr").length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str.length();
    }

    String getClipBoardText() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData pData = clipboard.getPrimaryClip();
        ClipData.Item item = pData.getItemAt(0);
        return "\n"+item.getText().toString() + ((isPackageNames)? " ^ @ ^ yyn ^":"") + "\n";
    }

}