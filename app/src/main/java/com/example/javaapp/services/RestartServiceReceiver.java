package com.example.javaapp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class RestartServiceReceiver extends BroadcastReceiver {
    private static final String TAG = "RestartServiceReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("com.example.javaapp.RESTART_SERVICE".equals(intent.getAction())) {
            Log.d(TAG, "Received RESTART_SERVICE broadcast, restarting AppService");
            Intent serviceIntent = new Intent(context, AppService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
        }
    }
}

