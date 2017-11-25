package com.example.hackernews.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by aman.kush on 9/16/2017.
 */
public class Utils {
    public static boolean isDataConnectivityOn(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == 1) {
                return true;//WIFI
            } else if (info.getType() == 0) {
                return true;//Mobile
            }
        }
        return false;
    }
}
