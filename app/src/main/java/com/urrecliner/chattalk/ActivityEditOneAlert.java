package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.alertLines;
import static com.urrecliner.chattalk.Vars.alertPos;
import static com.urrecliner.chattalk.Vars.alertsAdapter;
import static com.urrecliner.chattalk.Vars.mContext;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.urrecliner.chattalk.Sub.AlertLine;
import com.urrecliner.chattalk.Sub.AlertTableIO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ActivityEditOneAlert extends AppCompatActivity {

    AlertLine al;
    EditText eGroup, eWho, eKey1, eKey2, eTalk, eMatched, eSkip, eMore, ePrev, eNext;
    TextView tGroup, tWho, tKey1, tTalk, tMatched, tMore, tPrev;
    String mGroup, mWho, mPercent, mStatement;
    View deleteMenu;
    boolean newGroup = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_edit_one);
//        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        newGroup = false;
        al = alertLines.get(alertPos);
        eGroup = findViewById(R.id.e_group); eGroup.setText(al.group);
        eWho = findViewById(R.id.e_who); eWho.setText(al.who);
        eKey1 = findViewById(R.id.e_key1); eKey1.setText(al.key1);
        eKey2 = findViewById(R.id.e_key2); eKey2.setText(al.key2);
        eTalk = findViewById(R.id.e_talk);  eTalk.setText(al.talk);
        eMatched = findViewById(R.id.e_matched);  eMatched.setText(""+al.matched);
        eSkip = findViewById(R.id.e_skip); eSkip.setText(al.skip);
        eMore = findViewById(R.id.e_more); eMore.setText(al.more);
        ePrev = findViewById(R.id.e_prev); ePrev.setText(al.prev);
        eNext = findViewById(R.id.e_next); eNext.setText(al.next);
        tGroup = findViewById(R.id.t_group); tWho = findViewById(R.id.t_who);
        tKey1 = findViewById(R.id.t_key1);
        tTalk = findViewById(R.id.t_talk); tMatched = findViewById(R.id.t_matched);
        tMore = findViewById(R.id.t_more);
        tPrev = findViewById(R.id.t_prev);
        if (al.matched == -1) { // group line
            tGroup.setText("Group"); tWho.setText("Info");
            tKey1.setText("Skip 1,2");
            tTalk.setText("Skip 3,4"); tMatched.setText("Matched");
            tMore.setText("More");
            tPrev.setText("Skip 5,6");
        } else {
            tGroup.setText("Group Name"); tWho.setText("Who");
            tKey1.setText("Key 1,2");
            tTalk.setText("Talk,Skip"); tMatched.setText("Matched");
            tMore.setText("More");
            tPrev.setText("Prev/Next");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_one, menu);
        new Handler(Looper.getMainLooper()).post(() -> {
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
        String info;
        if (alertLines.get(alertPos).matched == -1) {    // group delete
            info = alertLines.get(alertPos).group;
            makeGroupMemo();
            mWho = "\n삭제됨\n" + mWho + "\n"
                    + new SimpleDateFormat(".MM/dd HH:mm", Locale.KOREA).format(new Date())
                    + "\n삭제됨\n";
            mPercent += "\n삭제\n" +mPercent+"\n"
                    + new SimpleDateFormat(".MM/dd HH:mm", Locale.KOREA).format(new Date());
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
            info = alertLines.get(alertPos).group+" "+ alertLines.get(alertPos).who;
            alertLines.remove(alertPos);
            alertsAdapter.notifyItemRemoved(alertPos);
            makeGroupMemo();
        }
        Upload2Google.uploadComment(mGroup, mWho, mPercent, "", mStatement);
        new AlertSave("Delete "+info);
        new AlertTableIO().remove(alertLines, mContext);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.save_alert) {
            saveAlert();

        } else if (item.getItemId() == R.id.duplicate_alert) {
            duplicateAlert();
        }
        return false;
    }

    private void duplicateAlert() {
        alertPos++;
        alertLines.add(alertPos, al);
        alertsAdapter.notifyItemInserted(alertPos);
        Toast.makeText(mContext,"Duplicated "+ al.group+" / " + al.who, Toast.LENGTH_SHORT).show();
        if (al.matched == -1)
            newGroup = true;
    }

    private void saveAlert() {
        String group = eGroup.getText().toString();
        String who = eWho.getText().toString().trim();
        String key1 = eKey1.getText().toString().trim();
        String key2 = eKey2.getText().toString().trim();
        String matchStr = eMatched.getText().toString();
        int matchInt = matchStr.equals("") ? 0: Integer.parseInt(matchStr);
        String more = eMore.getText().toString();
        String mTalk = eTalk.getText().toString();
        String skip = eSkip.getText().toString();
        String prev = ePrev.getText().toString();
        if (prev.equals("")) prev = key1;
        String next = eNext.getText().toString();
        if (next.equals("")) next = key2;
        al = new AlertLine(group, who, key1, key2, mTalk, matchInt, skip, more,
                prev, next);
        alertLines.set(alertPos, al);
        if (al.matched == -1 && newGroup) { // add new group dummy line
            al = new AlertLine(group, group+"누군가",
                    "종목명", "매수가", "", 0, "","",
                    "종목명","매수가");
            alertLines.add(al);
//                alertsAdapter.notifyItemInserted(alertPos);
            newGroup = false;
        }
        new AlertSave((al.matched == -1)? ("Save Group "+eGroup.getText().toString()):
                ("Save "+eGroup.getText().toString() + " : " + eWho.getText().toString()));
        makeGroupMemo();
        mTalk = new SimpleDateFormat("yy/MM/dd\nHH:mm", Locale.KOREA).format(new Date());
        Upload2Google.uploadComment(mGroup, mWho, mPercent, mTalk, mStatement);
        alertsAdapter = new AlertsAdapter();
        AlertTable.makeArrays();
        finish();
        alertsAdapter.notifyItemChanged(alertPos);
    }

    void makeGroupMemo() {
        mGroup = al.group;
        StringBuilder sb = new StringBuilder();
        for (AlertLine al: alertLines) {
            if (al.group.equals(mGroup)) {
                if (al.matched == -1) {
                    mWho = al.who;
                    mPercent = "s("+al.key1+", "+al.key2+"\n"+al.talk+", "+al.skip+"\n"+al.prev+", "+al.next+")";
                } else {
                    if (sb.length() > 1)
                        sb.append("\n");
                    sb.append(al.who).append(", k(")
                        .append(al.key1).append(", ")
                        .append(al.key2).append("), ")
                        .append(al.matched).append(" ")
                        .append((al.talk.length()>1)? " t("+al.talk+") ":"")
                        .append((al.skip.length()>1)? " s("+al.skip+") ":"")
                        .append((al.more.length()>1)? ", "+al.more:"")
                        .append(" pn<").append(al.prev).append(",").append(al.next)
                        .append(">");
                }
            }
        }
        mStatement = sb.toString();
    }
}