package biz.riopapa.chattalk.Sub;

import static biz.riopapa.chattalk.Sub.WifiName.wifiName;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import androidx.annotation.NonNull;

import java.util.Timer;
import java.util.TimerTask;

public class WifiMonitor {

    static ConnectivityManager cM = null;
    public WifiMonitor (Context context) {
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
//                String wiFi = new WifiName().get(context);
//                if (wiFi != null && !wiFi.equals(wifiName)) {
//                    wifiName = wiFi;
//                    new ToastText().show(wifiName + "에 연결됨");
//                }
            }
            @Override
            public void onLost(@NonNull Network network) {
                new ToastText().show("Wifi Off "+wifiName);
                wifiName = "wiFi";
                super.onLost(network);
            }
        });
    }
}