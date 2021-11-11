package com.quar.turnoffsilentmode.config;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class AppConfig {
    static final String MY_PREFS_NAME = "config";

    public static void setHaveContact(Context context, boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean("IS_HAVE_CONTACT", value);
        editor.apply();
    }

    public static boolean getHaveContact(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean("IS_HAVE_CONTACT", false);
    }



}
