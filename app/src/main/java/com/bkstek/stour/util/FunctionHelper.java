package com.bkstek.stour.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by thold on 8/1/2017.
 */

public class FunctionHelper {


    ///check internet
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
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
