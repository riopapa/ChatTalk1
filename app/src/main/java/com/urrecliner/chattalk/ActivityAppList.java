package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.alertsAdapter;
import static com.urrecliner.chattalk.Vars.appAdapter;
import static com.urrecliner.chattalk.Vars.appPos;
import static com.urrecliner.chattalk.Vars.apps;
import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.Vars.todayFolder;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.urrecliner.chattalk.Sub.AppsTable;

public class ActivityAppList extends AppCompatActivity {

    RecyclerView appRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_list);
    }

    @Override
    public void onResume() {
        super.onResume();

        appRecyclerView = findViewById(R.id.recycle_applist);
        appAdapter = new AppAdapter();
        appRecyclerView.setAdapter(appAdapter);
        if (todayFolder == null)
            new ReadyToday();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_applist, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.reload_all_apps) {
            apps = new AppsTable().get();
            appAdapter = new AppAdapter();
        } else if (item.getItemId() == R.id.save_table) {
            new AppsTable().put(this);
        } else if (item.getItemId() == R.id.add_one_app) {
            Intent intent = new Intent(this, ActivityAppEdit.class);
            appPos = -1;
            mContext.startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}