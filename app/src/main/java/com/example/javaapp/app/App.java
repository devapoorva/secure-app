package com.example.javaapp.app;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import com.example.javaapp.managers.DataSyncManager;
import com.example.javaapp.services.AppService;
import com.example.javaapp.services.SMSReceiver;

public class App extends Application {
    private static final String TAG = "App";
    private SMSReceiver smsReceiver;
    private DataSyncManager dataSyncManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Application onCreate");

        // Initialize components
        dataSyncManager = new DataSyncManager(this);

        // Register SMS receiver
        registerSmsReceiver();

        // Start foreground service
        startForegroundService();

        // Request to ignore battery optimization
        requestIgnoreBatteryOptimization();
    }

    private void registerSmsReceiver() {
        try {
            smsReceiver = new SMSReceiver();
            IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
            filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
            registerReceiver(smsReceiver, filter);
            Log.d(TAG, "SMS Receiver registered in Application");
        } catch (Exception e) {
            Log.e(TAG, "Error registering SMS receiver in Application: " + e.getMessage());
        }
    }

    private void startForegroundService() {
        try {
            Intent serviceIntent = new Intent(this, AppService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
            Log.d(TAG, "Foreground service started");
        } catch (Exception e) {
            Log.e(TAG, "Error starting foreground service: " + e.getMessage());
        }
    }

    private void requestIgnoreBatteryOptimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(android.net.Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent); // Actually launch the intent
                // Note: This requires the REQUEST_IGNORE_BATTERY_OPTIMIZATIONS permission
                // which is a system permission and might not be granted to regular apps
            } catch (Exception e) {
                Log.e(TAG, "Error requesting battery optimization ignore: " + e.getMessage());
            }
        }
    }

    @Override
    public void onTerminate() {
        Log.d(TAG, "Application onTerminate");
        super.onTerminate();
        try {
            if (smsReceiver != null) {
                unregisterReceiver(smsReceiver);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during application termination: " + e.getMessage());
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.w(TAG, "Application onLowMemory");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.w(TAG, "Application onTrimMemory: " + level);
    }
}