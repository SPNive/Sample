package com.spn.service;

/**
 * Created by developer on 12/4/15.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Sessions {

    public static void saveSession(String key, String value , Context context){
        Editor editor = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getSession(String key,Context context){
        SharedPreferences prefs = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE);
        return prefs.getString(key, "");
    }

    public static void clearSession(Context context){
        Editor editor = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }
}
