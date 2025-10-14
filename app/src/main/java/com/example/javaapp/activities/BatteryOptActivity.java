package com.example.javaapp.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class BatteryOptActivity extends Activity {
    private static final String TAG = "BatteryOptActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestIgnoreBatteryOptimization();
    }

    private void requestIgnoreBatteryOptimization() {
        try {
            @SuppressLint("BatteryLife") Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(android.net.Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            Toast.makeText(this, "Please allow the app to run in the background.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "Error requesting battery optimization ignore: " + e.getMessage());
            Toast.makeText(this, "Unable to open battery optimization settings.", Toast.LENGTH_LONG).show();
        }
        finish();
    }
}

