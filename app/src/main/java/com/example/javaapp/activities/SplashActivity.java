package com.example.javaapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.javaapp.R;
import com.example.javaapp.services.ServiceUtils;
import com.example.javaapp.utils.AppPref;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_SCREEN_TIMEOUT = 3000;
    private static final int REQUEST_PERMISSIONS = 1;
    private boolean isPermissionCheckCompleted = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        checkPermissions();
    }

    private void checkPermissions() {
        if (hasAllPermissions()) {
            // All permissions already granted
            startReadingSMS();
        } else {
            // Request only the permissions that regular apps can use
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_SMS,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_CONTACTS
            }, REQUEST_PERMISSIONS);
        }
    }

    private boolean hasAllPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS && !isPermissionCheckCompleted) {
            isPermissionCheckCompleted = true;

            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                startReadingSMS();
            } else {
                // Check if user denied any permission permanently
                boolean shouldShowRationale = false;
                for (String permission : permissions) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                        shouldShowRationale = true;
                        break;
                    }
                }

                if (shouldShowRationale) {
                    // User denied permanently, show dialog to go to settings
                    showPermissionDeniedDialog();
                } else {
                    // User denied but not permanently, you can request again or show explanation
                    showPermissionExplanationDialog();
                }
            }
        }
    }

    private void showPermissionDeniedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permissions Required")
                .setMessage("This app needs SMS and Contacts permissions to function properly. Please grant the permissions in app settings.")
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    openAppSettings();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                    finish(); // Close app if user cancels
                })
                .setCancelable(false)
                .show();
    }

    private void showPermissionExplanationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permissions Required")
                .setMessage("This app needs SMS and Contacts permissions to work correctly. Please allow all permissions when prompted.")
                .setPositiveButton("Try Again", (dialog, which) -> {
                    dialog.dismiss();
                    isPermissionCheckCompleted = false;
                    checkPermissions();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                    finish(); // Close app if user cancels
                })
                .setCancelable(false)
                .show();
    }

    // In SplashActivity.java - after permissions are granted
    private void startReadingSMS() {
        if (getUserId() != null && !getUserId().isEmpty() && !AppPref.getInstance(SplashActivity.this).getToken().isBlank()) {
            // Ensure service is running
            ServiceUtils.startAppService(SplashActivity.this);

            new Handler().postDelayed(()->{
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            },SPLASH_SCREEN_TIMEOUT);
        } else {
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }, SPLASH_SCREEN_TIMEOUT);
        }
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
        finish(); // Close the app, user will need to reopen after granting permissions
    }

    private String getUserId() {
        return AppPref.getInstance(SplashActivity.this).getUserId();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check permissions again when user returns from settings
        if (!isPermissionCheckCompleted && hasAllPermissions()) {
            isPermissionCheckCompleted = true;
            startReadingSMS();
        }
    }
}