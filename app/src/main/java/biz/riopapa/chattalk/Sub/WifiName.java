package biz.riopapa.chattalk.Sub;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class WifiName {
    public static String get(Context wContext) {
        ConnectivityManager conManager = (ConnectivityManager) wContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities capabilities = conManager.getNetworkCapabilities(conManager.getActiveNetwork());
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                // or TRANSPORT_CELLULAR
                final WifiManager wifiManager = (WifiManager) wContext.getSystemService(Context.WIFI_SERVICE);
                final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
                return (connectionInfo == null) ? null :  connectionInfo.getSSID();
            }
        }
        return null; // Wi-Fi adapter is OFF
    }
}