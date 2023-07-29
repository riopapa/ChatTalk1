package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.NotificationListener.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import androidx.annotation.NonNull;

import com.urrecliner.chattalk.Sub.ToastText;
import com.urrecliner.chattalk.Sub.WifiName;

import java.util.Timer;
import java.util.TimerTask;

public class WifiMonitor {

    static String wifiName = null;
    static ConnectivityManager cM = null;
    static void init (Context context) {
        if (cM == null)
            cM = context.getSystemService(ConnectivityManager.class);
        cM.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                Network nw = cM.getActiveNetwork();
                NetworkCapabilities netCap = cM.getNetworkCapabilities(nw);
                if (netCap != null && netCap.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    String newName = WifiName.get(context);
                    if (newName != null && !newName.equals(wifiName)) {
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
                wifiName = new WifiName().get(context);
//                utils.logW("wifi status", wifiName+" Connected");
                new ToastText().show(wifiName+"에 연결됨");
            }
            @Override
            public void onLost(@NonNull Network network) {
                utils.logW("wifi status", wifiName+" Gone");
                if (wifiName != null) {
                    new ToastText().show(wifiName+" 연결 끊어짐");
                    wifiName = null;
                }
                super.onLost(network);
            }
        });
    }
}