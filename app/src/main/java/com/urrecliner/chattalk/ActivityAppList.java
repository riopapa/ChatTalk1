package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.appAdapter;
import static com.urrecliner.chattalk.Vars.appPos;
import static com.urrecliner.chattalk.Vars.apps;
import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.Vars.todayFolder;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.urrecliner.chattalk.Sub.App;
import com.urrecliner.chattalk.Sub.AppsTable;
import com.urrecliner.chattalk.databinding.ActivityAppListBinding;

import java.util.List;

public class ActivityAppList extends AppCompatActivity {

    RecyclerView appRecyclerView;
    ActivityAppListBinding binding;
    String key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_app_list);

        binding = ActivityAppListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        super.onCreate(savedInstanceState);

//        PackageManager packageManager = getPackageManager();
//        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);
//
//        for (int i = 0; i < installedPackages.size(); i++) {
//            Log.w("pkg "+i, "name="+installedPackages.get(i));
//        }
    }

    @Override
    public void onResume() {
        super.onResume();

        appRecyclerView = findViewById(R.id.recycle_applist);
        appAdapter = new AppAdapter();
        appRecyclerView.setAdapter(appAdapter);
        if (todayFolder == null)
            new ReadyToday();

        binding.search.setOnClickListener(v -> {
            key = binding.searchKey.getText().toString();
            if (key.length() > 1) {
                searchApps(0);
            }
        });
        binding.searchNext.setOnClickListener(v -> {
            key = binding.searchKey.getText().toString();
            if (key.length() > 1) {
                searchApps(appPos+1);
            }
        });

        if (appPos > 0) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) appRecyclerView
                    .getLayoutManager();
            layoutManager.scrollToPositionWithOffset(
                    appPos, (appPos > 1) ? appPos - 1 : appPos);

        }
    }
    void searchApps(int startPos) {
        appPos = -1;
        for (int i = startPos; i < apps.size(); i++) {
            App app = apps.get(i);
            if (app.nickName.contains(key) || app.fullName.contains(key) ||
                app.memo.contains(key)) {
                appPos = i;
                break;
            }
        }
        if (appPos > 0) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) appRecyclerView
                    .getLayoutManager();
            layoutManager.scrollToPositionWithOffset(
                    appPos, (appPos> 1) ? appPos-1:appPos);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_applist, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.app_reload) {
            apps = new AppsTable().get();
            appAdapter = new AppAdapter();
        } else if (item.getItemId() == R.id.app_save) {
            new AppsTable().put(this);
        } else if (item.getItemId() == R.id.app_add) {
            appPos = -1;
            Intent intent = new Intent(this, ActivityAppEdit.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}