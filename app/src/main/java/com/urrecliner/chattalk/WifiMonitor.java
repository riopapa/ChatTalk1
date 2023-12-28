package com.urrecliner.chattalk;

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

    public static String wifiName = "wiFi";
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
//                    String newName = WifiName.get(context);
//                    if (newName != null && !newName.equals(wifiName)) {
//                        if (newName.equals("unknown ssid")) {
                            new Timer().schedule(new TimerTask() {
                                public void run() {
                                    showWifiName();
                                }
                            }, 3000);
//                        } else {
//                            showWifiName();
//                        }
//                    }
                }
                super.onAvailable(network);
            }

            private void showWifiName() {
                String wiFi = new WifiName().get(context);
                if (wiFi != null && !wiFi.equals(wifiName)) {
                    wifiName = wiFi;
                    new ToastText().show(wifiName + "에 연결됨");
                }
            }
            @Override
            public void onLost(@NonNull Network network) {
                new ToastText().show(wifiName+" 연결 끊어짐");
                wifiName = "wiFi";
                super.onLost(network);
            }
        });
    }
}