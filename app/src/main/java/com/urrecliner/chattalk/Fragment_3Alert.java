package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.aBar;
import static com.urrecliner.chattalk.Vars.alertLines;
import static com.urrecliner.chattalk.Vars.alertsAdapter;
import static com.urrecliner.chattalk.Vars.isRotate;
import static com.urrecliner.chattalk.Vars.mActivity;
import static com.urrecliner.chattalk.Vars.topTabs;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.urrecliner.chattalk.Sub.AlertLine;

public class Fragment_3Alert extends Fragment {

    ViewGroup rootView;
    RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(
                R.layout.frag3_alert, container, false);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        topTabs.getTabAt(3).select();
        recyclerView = rootView.findViewById(R.id.recycle_alerts);
        alertsAdapter = new AlertsAdapter();
        recyclerView.setAdapter(alertsAdapter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_3alert, menu);
        aBar.setTitle("  Alert Table");
        aBar.setSubtitle(null);
    }
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.reload_all_tables) {
            new OptionTables().readAll();
            AlertTable.readFile();
            AlertTable.makeArrays();
            AlertTable.sort();
            alertsAdapter.notifyDataSetChanged();
        } else if (item.getItemId() == R.id.rotate_screen) {
            isRotate = !isRotate;
            mActivity.setRequestedOrientation((isRotate) ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (item.getItemId() == R.id.clear_matched_number) {
            for (int i = 0; i < alertLines.size(); i++) {
                AlertLine al = alertLines.get(i);
                if (al.matched != -1) {
                    if (al.matched > 100)
                        al.matched = 200;
                    else if (al.talk.length() > 0)
                        al.matched = 100;
                    else if (al.matched > 0)
                        al.matched = 50;
                }
                alertLines.set(i, al);
            }
            AlertTable.sort();
            AlertTable.saveFile();
            alertsAdapter.notifyDataSetChanged();
        } else if (item.getItemId() == R.id.copy2log_save) {
            AlertTable.sort();
            AlertTable.saveFile();
            alertsAdapter.notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }

}