package com.example.javaapp.managers;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

public class DNDManager {
    private NotificationManager mNotificationManager;

    public DNDManager(Context context) {
        mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public boolean hasDNDPermission() {
        return mNotificationManager.isNotificationPolicyAccessGranted();
    }

    public void requestDNDPermission(Context context) {
        Intent intent = new Intent(
                android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
        context.startActivity(intent);
    }

    public void enableDND() {
        if (hasDNDPermission()) {
            mNotificationManager.setInterruptionFilter(
                    NotificationManager.INTERRUPTION_FILTER_NONE);
        }
    }

    public void disableDND() {
        if (hasDNDPermission()) {
            mNotificationManager.setInterruptionFilter(
                    NotificationManager.INTERRUPTION_FILTER_ALL);
        }
    }
}