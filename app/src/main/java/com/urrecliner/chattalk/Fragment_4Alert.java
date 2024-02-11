package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.ActivityMain.fragNumber;
import static com.urrecliner.chattalk.Vars.aBar;
import static com.urrecliner.chattalk.Vars.alertLines;
import static com.urrecliner.chattalk.Vars.alertPos;
import static com.urrecliner.chattalk.Vars.alertsAdapter;
import static com.urrecliner.chattalk.Vars.todayFolder;
import static com.urrecliner.chattalk.Vars.topTabs;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.urrecliner.chattalk.model.AlertLine;
import com.urrecliner.chattalk.alerts.AlertTableIO;

public class Fragment_4Alert extends Fragment {

    ViewGroup rootView;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(
                R.layout.frag4_alert, container, false);
        setHasOptionsMenu(true);
        if (alertsAdapter == null)
            alertsAdapter = new AlertsAdapter();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        fragNumber = 4;
        topTabs.getTabAt(fragNumber).select();
        aBar.setTitle(topTabs.getTabAt(fragNumber).getText().toString());
        aBar.setSubtitle(null);

        recyclerView = rootView.findViewById(R.id.recycle_alerts);
        recyclerView.setAdapter(alertsAdapter);
        if (todayFolder == null)
            new ReadyToday();

        if (alertPos > 0) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();
            assert layoutManager != null;
            layoutManager.scrollToPositionWithOffset(
                    alertPos, (alertPos > 3) ? alertPos - 3 : alertPos - 2);
        }

//        if (alertPos == -1)
//            alertPos = alertsAdapter.getItemCount() / 2;
//        mActivity.runOnUiThread(() -> {
//            recyclerView.smoothScrollToPosition(2);
//            recyclerView.smoothScrollToPosition(alertPos);
//        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_4alert, menu);
        aBar.setTitle(topTabs.getTabAt(4).getText().toString());
        aBar.setSubtitle(null);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.reload_all_tables) {
            new OptionTables().readAll();
            new AlertTableIO().get();
            alertsAdapter.notifyDataSetChanged();
        } else if (item.getItemId() == R.id.clear_matched_number) {
            for (int i = 0; i < alertLines.size(); i++) {
                AlertLine al = alertLines.get(i);
                if (al.matched != -1) {
                    if (al.talk.length() > 0)
                        al.matched = 1000;
                    else
                        al.matched = (al.matched+99) / 100  * 100;
                }
                alertLines.set(i, al);
            }
            new AlertSave("Clear Matches");
            alertsAdapter.notifyDataSetChanged();
        } else if (item.getItemId() == R.id.copy2log) {
            new AlertSave("Copy");
            alertsAdapter.notifyDataSetChanged();
        }
        super.onOptionsItemSelected(item);
        return false;
    }

}