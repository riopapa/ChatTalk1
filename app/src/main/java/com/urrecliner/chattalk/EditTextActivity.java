package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.kGroupDot;
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
import java.util.Arrays;
import java.util.stream.Collectors;

public class EditTextActivity extends AppCompatActivity {

    boolean isPackageNames, isStrReplace;
    final String dummyHead = "- [ ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_edit);
        isPackageNames = nowFileName.equals("packageNames");
        isStrReplace = nowFileName.equals("strReplaces");
        ActionBar ab = getSupportActionBar() ;
        assert ab != null;
        ab.setIcon(R.mipmap.chat_talk_mini);
        ab.setDisplayUseLogoEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setTitle("    "+nowFileName);

        EditText tv = findViewById(R.id.table_text);
        File file = new File(tableFolder, nowFileName + ".txt");
        String[] lines = tableListFile.readRaw(file);
        String text;

        ImageView iv = findViewById(R.id.searchKeyword);
        iv.setOnClickListener(v -> {
            int cnt = 0;
            int pos = -1;
            EditText et = findViewById(R.id.keyword);
            String key = et.getText().toString();           // .replace(" ","\u00A0");
            String fullText = tv.getText().toString();
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
        if (isStrReplace)
            text = replaceAddBlankLine(lines);
        else
            text = Arrays.stream(lines).map(s -> s + "\n").collect(Collectors.joining()) + "\n";
        tv.setText(text);
        tv.setFocusable(true);
        tv.setEnabled(true);
        tv.setClickable(true);
        tv.setFocusableInTouchMode(true);
    }

    String replaceAddBlankLine(String [] textLines) {
        String svGroup = "x";
        StringBuilder sb = new StringBuilder();
        for (String textLine: textLines) {
            if (textLine.length() < 2)
                continue;
            String [] oneL = textLine.split("\\^");
            if (!oneL[0].equals(svGroup)) {
                svGroup = oneL[0];
                String del = (kGroupDot.indexOf(svGroup+"!") > 0) ? "":" // 없는 그룹 //";
                sb.append(dummyHead).append(svGroup).append(del).append(" ] -\n\n");    // dummy some chars between groups
            }
            sb.append(textLine).append("\n").append("\n");
        }
        return sb.toString();
    }
    String sortText(String txt) {
        String[] arrText = txt.split("\n");
        Arrays.sort(arrText);
        StringBuilder sortedText = new StringBuilder();
        if (isStrReplace) {
            String sv = "";
            for (String t : arrText) {
                if (t.length() < 3 || t.startsWith(dummyHead)) // ignore if not "20^주식^aa^some text blank lines"
                    continue;
                if (!sv.equals(t.substring(0, 3))) {
                    sortedText.append("\n");
                    sv = t.substring(0,3);
                }
                sortedText.append(t).append("\n");
            }
        } else
            Arrays.stream(arrText).filter(t -> txt.length() > 2).forEach(t -> sortedText.append(t).append("\n"));
        return sortedText.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    final static String blank = StringUtils.repeat(" ", 20);
    private static String strPad(String s, int strLen) {
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
            FileIO.writeTextFile(tableFolder, nowFileName, (isPackageNames) ? sortPackage(s) : sortText(s));
            new OptionTables().readAll();
            finish();
        } else if (item.getItemId() == R.id.line_copy_add) {
            EditText et = findViewById(R.id.table_text);
            String logNow = et.getText().toString();
            StringBuilder sb=new StringBuilder(logNow);
            int pos = et.getSelectionStart();
            String s = insertClipBoard();
            sb.insert(pos, s);
            et.setText(sb.toString());
            et.setSelection(pos + s.length());
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
        StringBuilder sortedText = new StringBuilder();
        for (String t : arrText) {
            String[] fields = t.split("\\^");
            String memo= "";
            if (fields.length > 3)  // if nothing after ^ then no array return
                memo = fields[3];
            String oneLine = strPad(fields[0], 38) + "^" + strPad(fields[1], 10) + " ^ "
                    + strPad(fields[2], 4) + " ^ " + memo.trim();
            sortedText.append(oneLine).append("\n");
        }
        return sortedText.toString();
    }

    static int getByteLength(String s) {
        final String del = String.copyValueOf(new char[]{(char) Byte.parseByte("7F", 16)});
        int byteNumber = 0;
        for (int i = 0; i < s.length(); i++) {
            String bite = s.substring(i,i+1);
            byteNumber += (bite.compareTo(del)>0)? 2:1;
        }
        return byteNumber;
    }

    String insertClipBoard() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData pData = clipboard.getPrimaryClip();
        ClipData.Item item = pData.getItemAt(0);
        return "\n"+item.getText().toString() + ((isPackageNames)? " ^ @ ^  ^":"");
    }

}