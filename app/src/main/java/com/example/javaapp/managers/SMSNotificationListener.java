package com.example.javaapp.managers;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import java.util.HashSet;
import java.util.Set;

public class SMSNotificationListener extends NotificationListenerService {

    private static final String TAG = "SMSBlocker";
    private Set<String> smsPackageNames = new HashSet<>();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "SMS Notification Blocker Started");

        // Common SMS app package names
        smsPackageNames.add("com.android.mms"); // Default Android Messages
        smsPackageNames.add("com.google.android.apps.messaging"); // Google Messages
        smsPackageNames.add("com.samsung.android.messaging"); // Samsung Messages
        smsPackageNames.add("com.sec.android.mms"); // Samsung Legacy
        smsPackageNames.add("com.sonyericsson.conversations"); // Sony
        smsPackageNames.add("com.lge.mms"); // LG Messages
        smsPackageNames.add("com.motorola.mms"); // Motorola
        smsPackageNames.add("com.htc.sense.mms"); // HTC
        smsPackageNames.add("com.android.mms.service"); // MMS Service
        smsPackageNames.add("com.verizon.mms"); // Verizon Messages
        smsPackageNames.add("org.thoughtcrime.securesms"); // Signal
        smsPackageNames.add("com.whatsapp"); // WhatsApp (also has SMS verification)
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Log.d(TAG, "SMS Blocker Connected");
        cancelExistingSMSNotifications();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();
        String notificationText = getNotificationText(sbn);

        Log.d(TAG, "Notification from: " + packageName + ", Text: " + notificationText);

        // Check if this is an SMS notification
        if (isSMSNotification(sbn)) {
            Log.d(TAG, "BLOCKING SMS Notification from: " + packageName);
            cancelNotification(sbn.getKey());

            // Optional: Show a custom message that SMS was blocked
            showBlockedNotification();
        }
    }

    private boolean isSMSNotification(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();

        // Method 1: Block by package name (most reliable)
        if (smsPackageNames.contains(packageName)) {
            return true;
        }

        // Method 2: Block by notification content keywords
        String notificationText = getNotificationText(sbn).toLowerCase();
        String[] smsKeywords = {
                "sms", "text message", "message", "mms",
                "verification code", "otp", "confirm", "code:"
        };

        for (String keyword : smsKeywords) {
            if (notificationText.contains(keyword)) {
                Log.d(TAG, "Blocked by keyword: " + keyword);
                return true;
            }
        }

        return false;
    }

    private String getNotificationText(StatusBarNotification sbn) {
        StringBuilder text = new StringBuilder();

        try {
            // Get title
            if (sbn.getNotification().extras != null) {
                String title = sbn.getNotification().extras.getString("android.title");
                if (title != null) {
                    text.append(title).append(" ");
                }

                // Get text
                String content = sbn.getNotification().extras.getString("android.text");
                if (content != null) {
                    text.append(content);
                }

                // Get ticker text
                if (sbn.getNotification().tickerText != null) {
                    text.append(sbn.getNotification().tickerText.toString());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting notification text: " + e.getMessage());
        }

        return text.toString();
    }

    private void cancelExistingSMSNotifications() {
        try {
            StatusBarNotification[] activeNotifications = getActiveNotifications();
            if (activeNotifications != null) {
                for (StatusBarNotification sbn : activeNotifications) {
                    if (isSMSNotification(sbn)) {
                        cancelNotification(sbn.getKey());
                        Log.d(TAG, "Cancelled existing SMS notification from: " + sbn.getPackageName());
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error cancelling existing SMS notifications: " + e.getMessage());
        }
    }

    private void showBlockedNotification() {
        // Optional: Show a notification that SMS was blocked
        /*
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "sms_blocker")
                .setContentTitle("SMS Blocked")
                .setContentText("An SMS notification was blocked")
                .setSmallIcon(R.drawable.ic_block)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        nm.notify(999, builder.build());
        */
    }

    // Method to add custom SMS packages
    public void addSMSPackage(String packageName) {
        smsPackageNames.add(packageName);
    }

    // Method to remove package from blocking
    public void removeSMSPackage(String packageName) {
        smsPackageNames.remove(packageName);
    }
}