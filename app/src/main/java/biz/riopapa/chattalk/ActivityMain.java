package biz.riopapa.chattalk;

import static biz.riopapa.chattalk.NotificationListener.loadFunction;
import static biz.riopapa.chattalk.NotificationListener.notificationService;
import static biz.riopapa.chattalk.NotificationListener.utils;
import static biz.riopapa.chattalk.NotificationListener.vars;
import static biz.riopapa.chattalk.Vars.aBar;
import static biz.riopapa.chattalk.Vars.mActivity;
import static biz.riopapa.chattalk.Vars.mContext;
import static biz.riopapa.chattalk.Vars.packageDirectory;
import static biz.riopapa.chattalk.Vars.topTabs;
import static biz.riopapa.chattalk.Vars.viewPager2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayout;
import biz.riopapa.chattalk.Sub.Permission;
import biz.riopapa.chattalk.Sub.SnackBar;
import biz.riopapa.chattalk.Sub.WifiMonitor;
import biz.riopapa.chattalk.alerts.AlertTableIO;

import java.io.File;
import java.util.Set;

public class ActivityMain extends AppCompatActivity {


    public static int fragNumber = -1;
    static Intent mBackgroundServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        mActivity = this;

        try {
            PackageInfo info = getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), PackageManager.GET_PERMISSIONS);
            Permission.ask(this, this, info);
        } catch (Exception e) {
            Log.e("Permission", "No Permission " + e);
        }

// If you have access to the external storage, do whatever you need
        if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
        }

        if (!isNotificationAllowed(this.getPackageName())) {
            Toast.makeText(getApplicationContext(), "Allow permission on Android notification", Toast.LENGTH_LONG).show();
            Intent intListener = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intListener);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
            //permissions not granted -> request them
            requestPermissions(new String[]{Manifest.permission.WRITE_SETTINGS}, 6562);
        } else {
            Toast.makeText(getApplicationContext(), " permission OK", Toast.LENGTH_LONG).show();
            //permissions are granted - do your stuff here :)
        }

        if (!NotificationManagerCompat.getEnabledListenerPackages(this).contains(getPackageName())) {        //ask for permission
            Log.w("Permission","required "+getPackageName());
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        }

        vars = new Vars(mContext);
        aBar = getSupportActionBar();

        notificationService  = new NotificationService();

        //        new NotificationStart(pContext);
        if (!BootReceiver.isServiceRunning(mContext, notificationService.getClass())) {
            mBackgroundServiceIntent = new Intent(mContext, notificationService.getClass());
//            pContext.startForegroundService(mBackgroundServiceIntent);
            mContext.startService(mBackgroundServiceIntent);
        }

        new WifiMonitor(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.reload_all_tables) {
            new OptionTables().readAll();
            new AlertTableIO().get();

//            AlertTable.readFile("Main");
            new SnackBar().show("All Table","Reloaded");
        }
        return false;
    }

    @Override
    protected void onResume() {
        Log.w("mm Main","OnResume "+fragNumber);
        if (packageDirectory == null)
            packageDirectory = new File(Environment.getExternalStorageDirectory(), "_ChatTalkLog");

        if (utils == null)
            utils = new Utils();

        if (loadFunction == null)
            loadFunction = new LoadFunction();

        if (vars == null)
            vars = new Vars(mContext);

        if (aBar == null)
            aBar = getSupportActionBar();
        if (aBar != null) {
            aBar.setIcon(R.drawable.chat_talk_logo);
        }
        establishTabs();

        Intent updateIntent = new Intent(this, NotificationService.class);
        this.startForegroundService(updateIntent);
        NotificationBar.hideStop();

        super.onResume();
    }

    private boolean isNotificationAllowed(String packageName) {
        Set<String> listenerSet = NotificationManagerCompat.getEnabledListenerPackages(this);

        for (String pkg : listenerSet) {
            if (pkg != null && pkg.equals(packageName))
                return true;
        }
        return false;
    }

    private void establishTabs() {

//        if (topTabs == null) {
            topTabs = findViewById(R.id.tab_layout);
            topTabs.removeAllTabs();
            topTabs.addTab(topTabs.newTab().setText("Log"));
            topTabs.addTab(topTabs.newTab().setText("Table"));
            topTabs.addTab(topTabs.newTab().setText("Save"));
            topTabs.addTab(topTabs.newTab().setText("Stock"));
            topTabs.addTab(topTabs.newTab().setText("Alert"));
            topTabs.addTab(topTabs.newTab().setText("Chat"));
            topTabs.addTab(topTabs.newTab().setText("Work"));
            topTabs.setTabGravity(TabLayout.GRAVITY_FILL);
//        }
//        if (viewPager2 == null) {
            viewPager2 = findViewById(R.id.pager2);
            FragmentStateAdapter pagerAdapter = new PagerAdapter(this);
            viewPager2.setAdapter(pagerAdapter);
            viewPager2.setPageTransformer(new ZoomOutPageTransformer());
//        }
//        viewPager2.setCurrentItem(0);
        topTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                fragNumber = tab.getPosition();
                viewPager2.setCurrentItem(fragNumber);
                topTabs.getTabAt(fragNumber).select();
                aBar.setTitle(topTabs.getTabAt(fragNumber).getText().toString());
                aBar.setSubtitle(null);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
//        if (fragNumber == -1) {
//            fragNumber = 0; // initiated
//        }
        fragNumber = 0;
        if(topTabs != null) {
            fragNumber = topTabs.getSelectedTabPosition();
            viewPager2.setCurrentItem(fragNumber);
            viewPager2.invalidate();
            topTabs.getTabAt(fragNumber).select();
            aBar.setTitle(topTabs.getTabAt(fragNumber).getText().toString());
            aBar.setSubtitle(null);
        } else {
            establishTabs();
        }
        findViewById(R.id.main_layout).invalidate();
    }
}