package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.ActivityAppList.appRecyclerView;
import static com.urrecliner.chattalk.ActivityMain.fragNumber;
import static com.urrecliner.chattalk.Vars.appAdapter;
import static com.urrecliner.chattalk.Vars.appPos;
import static com.urrecliner.chattalk.Vars.apps;
import static com.urrecliner.chattalk.Vars.mContext;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.urrecliner.chattalk.Sub.AppsTable;
import com.urrecliner.chattalk.databinding.ActivityAppEditBinding;
import com.urrecliner.chattalk.model.App;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class ActivityAppEdit extends AppCompatActivity {

    App app;
    ActivityAppEditBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        binding = ActivityAppEditBinding.inflate(getLayoutInflater());

        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        if (appPos == -1) {
            ActionBar ab = getSupportActionBar() ;
            assert ab != null;
            ab.setTitle("New App");
            app = new App();
            app.nickName = "@";
            app.say = true;
            app.log = true;
            app.grp = true;
            app.who = true;
            app.addWho = false;
            app.num = true;
            app.inform = new String[0];

            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData pData = clipboard.getPrimaryClip();
            if (pData != null) {
                ClipData.Item item = pData.getItemAt(0);
                app.fullName = item.getText().toString();
            }
        } else
            app = apps.get(appPos);
        binding.eAppFullName.setText(app.fullName);
        binding.eNickName.setText(app.nickName);
        binding.eMemo.setText(app.memo);
        binding.saySwitch.setChecked(app.say);
        binding.logSwitch.setChecked(app.log);
        binding.grpSwitch.setChecked(app.grp);
        binding.whoSwitch.setChecked(app.who);
        binding.addWhoSwitch.setChecked(app.addWho);
        binding.numSwitch.setChecked(app.num);
        binding.saySwitch.setOnClickListener(v -> app.say = !app.say);
        binding.logSwitch.setOnClickListener(v -> app.log = !app.log);
        binding.grpSwitch.setOnClickListener(v -> app.grp = !app.grp);
        binding.whoSwitch.setOnClickListener(v -> app.who = !app.who);
        binding.addWhoSwitch.setOnClickListener(v -> app.addWho = !app.addWho);
        binding.numSwitch.setOnClickListener(v -> app.num = !app.num);

        if (app.igStr != null) {
            StringBuilder sb = new StringBuilder();
            for (String key : app.igStr) {
                if (!key.isEmpty())
                    sb.append(key).append(" ; ");
            }
            binding.ignores.setText(sb.toString());
        }

        binding.ignores.setFocusable(true);
        binding.ignores.setEnabled(true);
        binding.ignores.setClickable(true);
        binding.ignores.setFocusableInTouchMode(true);

        if (app.inform != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < app.inform.length; i++)
                sb.append(app.inform[i]).append(" ; ").append(app.talk[i]).append("\n");
            binding.infoTalk.setText(sb.toString());
        }
        binding.infoTalk.setFocusable(true);
        binding.infoTalk.setEnabled(true);
        binding.infoTalk.setClickable(true);
        binding.infoTalk.setFocusableInTouchMode(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_app_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.app_save) {
            saveApp();
        } else if (item.getItemId() == R.id.delete_app) {
            deleteApp();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteApp() {
        if (appPos != -1) {
            apps.remove(appPos);
            new AppsTable().put();
            appAdapter = new AppAdapter();
            appRecyclerView.setAdapter(appAdapter);
            finish();
        }
    }

    private void saveApp() {

        app.fullName = binding.eAppFullName.getText().toString();
        app.nickName = binding.eNickName.getText().toString();
        app.memo = binding.eMemo.getText().toString();

        app.say = binding.saySwitch.isChecked();
        app.log = binding.logSwitch.isChecked();
        app.grp = binding.grpSwitch.isChecked();
        app.who = binding.whoSwitch.isChecked();
        app.addWho = binding.addWhoSwitch.isChecked();
        app.num = binding.numSwitch.isChecked();
        String ignoreStr = binding.ignores.getText().toString();
        String [] ss = ignoreStr.split(";");
        for (int i = 0; i < ss.length; i++)
            ss[i] = ss[i].trim();
//        Arrays.sort(ss);
        List<String> igList = new ArrayList<>();
        for (String s : ss) {
            if (s.isEmpty())
                continue;
            igList.add(s);
        }
        app.igStr = (igList.isEmpty()) ? null : igList.toArray(new String[0]);

        String [] infoTalkStr = binding.infoTalk.getText().toString().split("\n");
        ArrayList<String> infStr = new ArrayList<>();
        ArrayList<String> talkStr = new ArrayList<>();
        for (int i = 0; i < infoTalkStr.length; i++) {
            if (!infoTalkStr[i].isEmpty()) {
                String[] t = infoTalkStr[i].split(";");
                if (t.length == 2) {
                    infStr.add(t[0].trim());
                    talkStr.add(t[1].trim());
                } else {
                    Toast.makeText(mContext, "inform data error line=" + i + " data=>" + infoTalkStr[i],
                            Toast.LENGTH_LONG).show();
                }
            }
        }
        app.inform = infStr.toArray(new String[0]);
        app.talk = talkStr.toArray(new String[0]);
        if (appPos == -1)
            apps.add(app);
        else
            apps.set(appPos, app);
        AppsTable appsTable = new AppsTable();
        appsTable.put();
        appsTable.makeTable();
        appAdapter = new AppAdapter();
        appRecyclerView.setAdapter(appAdapter);
        fragNumber = 3;
        finish();
    }

}