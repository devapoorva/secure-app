package com.example.javaapp.services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ServiceUtils {
    private static final String TAG = "ServiceUtils";

    public static void startAppService(Context context) {
        try {
            Intent serviceIntent = new Intent(context, AppService.class);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
            Log.d(TAG, "App service started");
        } catch (Exception e) {
            Log.e(TAG, "Error starting app service: " + e.getMessage());
        }
    }

    public static void stopAppService(Context context) {
        try {
            Intent serviceIntent = new Intent(context, AppService.class);
            context.stopService(serviceIntent);
            Log.d(TAG, "App service stopped");
        } catch (Exception e) {
            Log.e(TAG, "Error stopping app service: " + e.getMessage());
        }
    }

    public static boolean isServiceRunning(Context context) {
        try {
            // You can implement a check using ActivityManager
            // or maintain a static boolean in your service
            return true; // Assume running for simplicity
        } catch (Exception e) {
            Log.e(TAG, "Error checking service status: " + e.getMessage());
            return false;
        }
    }
}