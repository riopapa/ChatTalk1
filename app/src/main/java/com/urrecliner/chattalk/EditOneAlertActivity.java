package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.alertLines;
import static com.urrecliner.chattalk.Vars.alertsAdapter;
import static com.urrecliner.chattalk.Vars.mContext;

import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.urrecliner.chattalk.Sub.AlertLine;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditOneAlertActivity extends AppCompatActivity {

    AlertLine al;
    EditText eGroup, eWho, eKey1, eKey2, eTalk, eMatched, eSkip, eMemo, ePrev, eNext;
    TextView tGroup, tWho, tKey1, tTalk, tMatched, tMemo, tPrev;
    String mGroup, mWho, mPercent, mMemo, mPrev, mNext;
    View deleteMenu;
    boolean newGroup = false;
    int linePos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_edit_one);
        linePos = getIntent().getIntExtra("linePos", linePos);
//        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        newGroup = false;
        al = alertLines.get(linePos);
        eGroup = findViewById(R.id.e_group); eGroup.setText(al.group);
        eWho = findViewById(R.id.e_who); eWho.setText(al.who);
        eKey1 = findViewById(R.id.e_key1); eKey1.setText(al.key1);
        eKey2 = findViewById(R.id.e_key2); eKey2.setText(al.key2);
        eTalk = findViewById(R.id.e_talk);  eTalk.setText(al.talk);
        eMatched = findViewById(R.id.e_matched);  eMatched.setText(""+al.matched);
        eSkip = findViewById(R.id.e_skip); eSkip.setText(al.skip);
        eMemo = findViewById(R.id.e_memo); eMemo.setText(al.memo.replace("~","\n"));
        ePrev = findViewById(R.id.e_prev); ePrev.setText(al.prev);
        eNext = findViewById(R.id.e_next); eNext.setText(al.next);
        tGroup = findViewById(R.id.t_group); tWho = findViewById(R.id.t_who);
        tKey1 = findViewById(R.id.t_key1);
        tTalk = findViewById(R.id.t_talk); tMatched = findViewById(R.id.t_matched);
        tMemo = findViewById(R.id.t_memo);
        tPrev = findViewById(R.id.t_prev);
        if (al.matched == -1) { // group line
            tGroup.setText("Group Name"); tWho.setText("Group Info");
            tKey1.setText("Skip 1,2");
            tTalk.setText("Skip 3,4"); tMatched.setText("Matched");
            tMemo.setText("Say More");
            tPrev.setText("Prev/Next");
        } else {
            tGroup.setText("Group Name"); tWho.setText("Who");
            tKey1.setText("Key 1,2");
            tTalk.setText("Talk,Skip"); tMatched.setText("Matched");
            tMemo.setText("Memo ~");
            tPrev.setText("Prev/Next");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_one, menu);
        new Handler().post(() -> {
            deleteMenu = this.findViewById(R.id.action_remove);
            if (deleteMenu != null) {
                setLongClick();
            }
        });
        return true;
    }

    private void setLongClick() {
        deleteMenu.setOnLongClickListener(v -> {
            deleteAlert();
            return true;
        });
    }

    private void deleteAlert() {
        if (alertLines.get(linePos).matched == -1) {    // group delete
            makeGroupMemo();
            mWho += "\n 삭제됨 : " + new SimpleDateFormat(".MM/dd HH:mm", Locale.KOREA).format(new Date());
            mPercent += "\n 그룹 삭제 " + new SimpleDateFormat(".MM/dd HH:mm", Locale.KOREA).format(new Date());
            int alertSize = alertLines.size();
            for (int i = 0; i < alertSize;) {
                if (alertLines.get(i).group.equals(mGroup)) {
                    alertLines.remove(i);
                    alertsAdapter.notifyItemRemoved(i);
                    alertSize--;
                }
                else
                    i++;
            }
        } else {
            alertLines.remove(linePos);
            alertsAdapter.notifyItemRemoved(linePos);
            makeGroupMemo();
        }
        Upload2Google.uploadComment(mGroup, mWho, mPercent, mMemo);
        new AlertSave();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.save_alert) {
            String matchStr = eMatched.getText().toString();
            int matchInt = matchStr.equals("") ? 0: Integer.parseInt(matchStr);
            String [] memos = eMemo.getText().toString().split("~");
            String memo = memos[0].trim();
            String more = (memos.length> 1) ? memos[1].trim() : "";
            String key1 = eKey1.getText().toString();
            String key2 = eKey2.getText().toString();
            String prev = ePrev.getText().toString(); if (prev.equals("")) prev = key1;
            String next = eNext.getText().toString(); if (next.equals("")) next = key2;
            al = new AlertLine(eGroup.getText().toString(), eWho.getText().toString(),
                    key1, key2,
                    eTalk.getText().toString(), matchInt,
                    eSkip.getText().toString(), memo, more,
                    prev, next);
            alertLines.set(linePos, al);
            if (al.matched == -1 && newGroup) { // add new group dummy line
                al = new AlertLine(eGroup.getText().toString(), "누군가",
                        "종목명", "매수가", "", 123, "","", "", "","");
                alertLines.add(al);
                alertsAdapter.notifyItemInserted(linePos);
                newGroup = false;
            }
            makeGroupMemo();
            Upload2Google.uploadComment(mGroup, mWho, mPercent, mMemo);
            AlertTable.sort();
            alertsAdapter = new AlertsAdapter();
//            alertsAdapter.notifyDataSetChanged();
            new AlertSave();
            finish();

        } else if (item.getItemId() == R.id.duplicate_alert) {
            linePos++;
            alertLines.add(linePos, al);
            alertsAdapter.notifyItemInserted(linePos);
            Toast.makeText(mContext,"Duplicated "+ al.group+" / " + al.who, Toast.LENGTH_SHORT).show();
            if (al.matched == -1)
                newGroup = true;
        }
        return false;
    }

    void makeGroupMemo() {
        mGroup = al.group; mWho = al.who; mPercent = ""; mMemo = "";
        StringBuilder sb = new StringBuilder();
        for (AlertLine al: alertLines) {
            if (al.group.equals(mGroup)) {
                if (al.matched == -1) {
                    mWho = al.who;
                    mPercent = al.key1+", "+al.key2+", "+al.talk+", "+al.skip+"\n"
                        + al.memo.replace("~","\n");
                } else {
                    if (sb.length() > 1)
                        sb.append("\n");
                    sb.append(al.who).append(", key( ")
                        .append(al.key1).append(", ")
                        .append(al.key2).append(" ), ")
                        .append(al.matched).append(" ")
                        .append((al.talk.length()>1)? ", talk( "+al.talk+" ) ":" ")
                        .append((al.skip.length()>1)? ", skip( "+al.skip+" ) ":" ")
                        .append((al.memo.length()>1)? ", "+al.memo:"")
                        .append(" <").append(al.prev).append("x").append(al.next)
                        .append(" >");
                }
            }
        }
        mMemo = sb.toString();
    }
}