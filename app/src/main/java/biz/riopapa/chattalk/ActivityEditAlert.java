package biz.riopapa.chattalk;

import static biz.riopapa.chattalk.ActivityMain.fragNumber;
import static biz.riopapa.chattalk.Vars.alertLines;
import static biz.riopapa.chattalk.Vars.alertPos;
import static biz.riopapa.chattalk.Vars.alertsAdapter;
import static biz.riopapa.chattalk.Vars.mContext;

import android.content.Context;
import android.content.SharedPreferences;
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

import biz.riopapa.chattalk.model.AlertLine;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class ActivityEditAlert extends AppCompatActivity {

    AlertLine al;
    EditText eGroup, eWho, eKey1, eKey2, eTalk, eMatched, eSkip, eMore, ePrev, eNext;
    TextView tGroup, tWho, tTalk, tKey1, tPrev;
    String mGroup, mWho, mPercent, mStatement;
    View deleteMenu;
    boolean newGroup = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_edit);
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
        tTalk = findViewById(R.id.t_talk);
        tPrev = findViewById(R.id.t_prev);
        if (al.matched == -1) { // group line
            tGroup.setText("Group"); tWho.setText("Grp Info");
            tKey1.setText("Skip 1,2");
            eKey1.setHint("Skip 1"); eKey2.setHint("Skip 2");
            eTalk.setHint("Skip 3"); eSkip.setHint("Skip 4");
            tTalk.setText("SKip3, 4");
            tPrev.setText("의미 없음");
        } else {
            tGroup.setText("Group Name"); tWho.setText("Who");
            tKey1.setText("Key 1,2");
            eKey1.setHint("Key 1"); eKey2.setHint("Key 2");
            tTalk.setText("Talk,SKip");
            eTalk.setHint("Talk"); eSkip.setHint("Skip");
            tPrev.setText("Prev/Next");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_alert_one, menu);
        new Handler(Looper.getMainLooper()).post(() -> {
            deleteMenu = this.findViewById(R.id.delete_app);
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
            mStatement = makeGroupMemo();
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
            mStatement =makeGroupMemo();
        }
        Upload2Google.uploadGroupInfo(mGroup, mWho, mPercent, "", mStatement);
        new AlertSave("Delete "+info);
        remove(alertLines, mContext);
        finish();
    }

    void remove(ArrayList<AlertLine> alertLines, Context context) {

        SharedPreferences sharePref = context.getSharedPreferences("alertLine", MODE_PRIVATE);
        SharedPreferences.Editor sharedEditor = sharePref.edit();
        Map<String, ?> map = sharePref.getAll();
        for(Map.Entry<String,?> entry : map.entrySet()){
            String [] grpWho = entry.getKey().split("~~");
            if (grpWho[0].equals("matched")) {
                int idx = -1;
                for (int i = 0; i < alertLines.size(); i++) {
                    AlertLine al = alertLines.get(i);
                    if (al.group.equals(grpWho[1]) && al.who.equals(grpWho[2]) &&
                            al.key1.equals(grpWho[3]) && al.key2.equals(grpWho[4])) {
                        idx = i;
                        break;
                    }
                }
                if (idx == -1) {
//                    Log.w("sharedPref","removing ... " +entry.getKey());
                    sharedEditor.remove(entry.getKey());
                }
            }
        }
        sharedEditor.apply();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.app_save) {
            saveAlert();

        } else if (item.getItemId() == R.id.duplicate_alert) {
            duplicateAlert();
        }
        return super.onOptionsItemSelected(item);
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
        int matchInt = matchStr.isEmpty() ? 0: Integer.parseInt(matchStr);
        String more = eMore.getText().toString();
        String mTalk = eTalk.getText().toString();
        String skip = eSkip.getText().toString();
        String prev = ePrev.getText().toString();
        String next = eNext.getText().toString();
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
        mStatement = makeGroupMemo();
        mTalk = new SimpleDateFormat("yy/MM/dd\nHH:mm", Locale.KOREA).format(new Date());
        Upload2Google.uploadGroupInfo(mGroup, mWho, mPercent, mTalk, mStatement);
//        alertsAdapter = new AlertsAdapter();
        AlertTable.makeArrays();
//        alertsAdapter.notifyDataSetChanged();
        fragNumber = 4;
        finish();
    }

    String makeGroupMemo() {
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
        return sb.toString();
    }
}