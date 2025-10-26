package com.example.javaapp.utils;

import android.content.Context;

public class AppPref {
    private static AppPref instance;
    private final Context context;

    private AppPref(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized AppPref getInstance(Context context) {
        if (instance == null) {
            instance = new AppPref(context);
        }
        return instance;
    }

    public void setUserId(String userId) {
        context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                .edit()
                .putString("user_id", userId)
                .apply();
    }

    public String getUserId() {
        return context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                .getString("user_id", "");
    }

    public void setUsername(String userId) {
        context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                .edit()
                .putString("user_name", userId)
                .apply();
    }

    public String getUsername() {
        return context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                .getString("user_name", "");
    }

    public void setToken(String token) {
        context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                .edit()
                .putString("token", token)
                .apply();
    }

    public String getToken() {
        return context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                .getString("token", "");
    }

    public void clear() {
        context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
    }

}
