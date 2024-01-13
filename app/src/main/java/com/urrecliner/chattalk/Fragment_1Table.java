package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.aBar;
import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.Vars.nowFileName;
import static com.urrecliner.chattalk.Vars.topTabs;
import static com.urrecliner.chattalk.ActivityMain.fragNumber;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.urrecliner.chattalk.alerts.AlertTableIO;
import com.urrecliner.chattalk.Sub.SnackBar;
import com.urrecliner.chattalk.Sub.WifiName;

public class Fragment_1Table extends Fragment {

    ViewGroup rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(
                R.layout.frag1_table, container, false);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        fragNumber = 1;
        topTabs.getTabAt(fragNumber).select();
        aBar.setTitle(topTabs.getTabAt(fragNumber).getText().toString());
        aBar.setSubtitle(null);

        TextView tv_WifiState = rootView.findViewById(R.id.tv_wifi_state);
        String s = "Wifi : " + WifiName.get(mContext);
        tv_WifiState.setText(s);
        tv_WifiState.setOnClickListener(v -> {
            String s1 = "Wifi : " + WifiName.get(mContext);
            tv_WifiState.setText(s1);
        });

        rootView.findViewById(R.id.sms_with_no_number).setOnClickListener(this::edit_table);
        rootView.findViewById(R.id.app_text_ignores).setOnClickListener(this::edit_table);
        rootView.findViewById(R.id.system_ignores).setOnClickListener(this::edit_table);

        rootView.findViewById(R.id.sms_who_ignores).setOnClickListener(this::edit_table);
        rootView.findViewById(R.id.sms_text_ignores).setOnClickListener(this::edit_table);
        rootView.findViewById(R.id.package_names).setOnClickListener(this::edit_table);

        rootView.findViewById(R.id.k_group_who_ignores).setOnClickListener(this::edit_table);
        rootView.findViewById(R.id.k_text_ignores).setOnClickListener(this::edit_table);
        rootView.findViewById(R.id.kt_no_number).setOnClickListener(this::edit_table);
        rootView.findViewById(R.id.group_telegrams).setOnClickListener(this::edit_table);

        rootView.findViewById(R.id.string_replace).setOnClickListener(this::edit_replace);
        rootView.findViewById(R.id.toss_ignore).setOnClickListener(this::edit_table);

        rootView.invalidate();
    }

    public void edit_table(View v) {
        nowFileName = v.getTag().toString();
        Intent intent;
        if (nowFileName.equals("appNames")) {
            intent = new Intent(mContext, ActivityAppList.class);
        } else {
            intent = new Intent(mContext, ActivityEditText.class);

        }
        startActivity(intent);

    }

    public void edit_replace(View v) {
        nowFileName = v.getTag().toString();
        Intent intent = new Intent(mContext, ActivityStringReplace.class);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_0table, menu);
        super.onCreateOptionsMenu(menu, inflater);
        aBar.setTitle(topTabs.getTabAt(1).getText().toString());
        aBar.setSubtitle(null);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.reload_all_tables) {
            new OptionTables().readAll();
//            AlertTable.readFile("read All");
            new AlertTableIO().get();
            new SnackBar().show("All Table", "Reloaded");
        }
        return super.onOptionsItemSelected(item);
    }
}