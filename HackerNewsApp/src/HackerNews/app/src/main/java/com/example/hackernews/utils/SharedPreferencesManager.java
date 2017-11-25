package com.example.hackernews.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by aman.kush on 9/15/2017.
 */
public class SharedPreferencesManager {
    private static SharedPreferences getSharedPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    private static SharedPreferences.Editor getSharedPrefsEditor(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit();
    }

    public static boolean put(Context context, String key, String val) {
        SharedPreferences.Editor editor = getSharedPrefsEditor(context);
        editor.putString(key, val);
        return editor.commit();
    }

    public static boolean put(Context context, String key, int val) {
        SharedPreferences.Editor editor = getSharedPrefsEditor(context);
        editor.putInt(key, val);
        return editor.commit();
    }

    public static boolean put(Context context, String key, long val) {
        SharedPreferences.Editor editor = getSharedPrefsEditor(context);
        editor.putLong(key, val);
        return editor.commit();
    }

    public static String getString(Context context, String key) {
        return getSharedPrefs(context).getString(key, "");
    }

    public static int getInt(Context context, String key) {
        return getSharedPrefs(context).getInt(key, 0);
    }

    public static long getLong(Context context, String key) {
        return getSharedPrefs(context).getLong(key, 0);
    }

}
