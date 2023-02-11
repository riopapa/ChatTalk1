package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.SubFunc.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import androidx.annotation.NonNull;

import com.urrecliner.chattalk.Sub.WifiName;

import java.util.Timer;
import java.util.TimerTask;

public class WifiMonitor {

    static String wifiName = null;

    static void init (Context context) {
        ConnectivityManager connectivityManager = context.getSystemService(ConnectivityManager.class);
        connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                Network nw = connectivityManager.getActiveNetwork();
                NetworkCapabilities netCap = connectivityManager.getNetworkCapabilities(nw);
                assert netCap != null;
                if (netCap.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    String newName = WifiName.get(context);
                    if (!newName.equals(wifiName)) {
                        if (newName.equals("unknown ssid")) {
                            new Timer().schedule(new TimerTask() {
                                public void run() {
                                    showWifiName(WifiName.get(context));
                                }
                            }, 3000);
                        } else {
                            showWifiName(newName);
                        }
                    }
                }
                super.onAvailable(network);
            }

            private void showWifiName(String newName) {
                wifiName = newName;
//                String s = wifiName + "에 연결됨";
                int qSize = Upload2Google.getQueSize();
                if (qSize > 0) {
//                    s += "\nUploading QSize=" + qSize;
                    Upload2Google.uploadStock();
                }
//                utils.showToast(s, 0);
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                if (wifiName != null) {
                    utils.showToast(wifiName+" 연결 끊어짐");
                    wifiName = null;
                }
            }
        });
    }
}