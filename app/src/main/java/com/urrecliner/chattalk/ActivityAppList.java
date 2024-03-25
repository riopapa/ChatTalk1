package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.aBar;
import static com.urrecliner.chattalk.Vars.appAdapter;
import static com.urrecliner.chattalk.Vars.appPos;
import static com.urrecliner.chattalk.Vars.apps;
import static com.urrecliner.chattalk.Vars.todayFolder;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.urrecliner.chattalk.Sub.AppsTable;
import com.urrecliner.chattalk.databinding.ActivityAppListBinding;
import com.urrecliner.chattalk.model.App;

public class ActivityAppList extends AppCompatActivity {

    public static RecyclerView appRecyclerView;
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
        aBar.setTitle("app edit");
        aBar.setSubtitle(null);

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
                searchApps(appPos+2);
            }
        });

    }
    void searchApps(int startPos) {
        appPos = -1;
        String result = "";
        for (int i = startPos; i < apps.size(); i++) {
            App app = apps.get(i);
            if (app.nickName.contains(key) || app.fullName.contains(key) ||
                app.memo.contains(key)) {
                appPos = i;
                result = app.nickName+" "+app.fullName + " " +app.memo;
                break;
            }
        }
        if (appPos > 0) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) appRecyclerView
                    .getLayoutManager();
            layoutManager.scrollToPositionWithOffset((appPos > 2) ? appPos-2:appPos, 10);
            Toast.makeText(this, key+" found "+result, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_applist, menu);
        aBar.setTitle("app edit");
        aBar.setSubtitle(null);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        AppsTable appsTable = new AppsTable();
        if (item.getItemId() == R.id.app_reload) {
            appsTable.get();
            appAdapter = new AppAdapter();
            appRecyclerView.setAdapter(appAdapter);
        } else if (item.getItemId() == R.id.app_save) {
            appsTable.put();
        } else if (item.getItemId() == R.id.app_add) {
            appPos = -1;
            Intent intent = new Intent(this, ActivityAppEdit.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}