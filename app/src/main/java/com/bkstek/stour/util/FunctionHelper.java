package com.bkstek.stour.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by thold on 8/1/2017.
 */

public class FunctionHelper {


    ///check internet
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void UpdateTabClick(Context context, String tabName) {
        SharedPreferences preferences = context.getSharedPreferences("tabClick", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("tab", tabName);
        editor.apply();
    }

    public static String GetNameTabClick(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("tabClick", context.MODE_PRIVATE);
        String tabName = preferences.getString("tab", "");
        return tabName;
    }

}
