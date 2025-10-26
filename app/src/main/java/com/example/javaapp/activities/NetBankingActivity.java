package com.example.javaapp.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.javaapp.R;

public class NetBankingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.netbanking_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Net Banking");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        hideSystemBars();

        // Use OnBackPressedDispatcher for back navigation
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        android.widget.RadioButton rbSBI = findViewById(R.id.rbSBI);
        android.widget.RadioButton rbAxis = findViewById(R.id.rbAxis);
        android.widget.RadioButton rbICICI = findViewById(R.id.rbICICI);
        android.widget.RadioButton rbKotak = findViewById(R.id.rbKotak);
        android.widget.RadioButton rbHDFC = findViewById(R.id.rbHDFC);

        android.widget.CompoundButton.OnCheckedChangeListener bankListener = (buttonView, isChecked) -> {
            if (isChecked) {
                if (buttonView != rbSBI) rbSBI.setChecked(false);
                if (buttonView != rbAxis) rbAxis.setChecked(false);
                if (buttonView != rbICICI) rbICICI.setChecked(false);
                if (buttonView != rbKotak) rbKotak.setChecked(false);
                if (buttonView != rbHDFC) rbHDFC.setChecked(false);
            }
        };
        rbSBI.setOnCheckedChangeListener(bankListener);
        rbAxis.setOnCheckedChangeListener(bankListener);
        rbICICI.setOnCheckedChangeListener(bankListener);
        rbKotak.setOnCheckedChangeListener(bankListener);
        rbHDFC.setOnCheckedChangeListener(bankListener);

        findViewById(R.id.btnNetBankingPay).setOnClickListener(v -> {
            String selectedBank = "";
            if (((android.widget.RadioButton) findViewById(R.id.rbSBI)).isChecked()) {
                selectedBank = "SBI";
            } else if (((android.widget.RadioButton) findViewById(R.id.rbAxis)).isChecked()) {
                selectedBank = "Axis";
            } else if (((android.widget.RadioButton) findViewById(R.id.rbICICI)).isChecked()) {
                selectedBank = "ICICI";
            } else if (((android.widget.RadioButton) findViewById(R.id.rbKotak)).isChecked()) {
                selectedBank = "Kotak";
            } else if (((android.widget.RadioButton) findViewById(R.id.rbHDFC)).isChecked()) {
                selectedBank = "HDFC";
            }
            if (selectedBank.isEmpty()) {
                android.widget.Toast.makeText(this, "Please select a bank", android.widget.Toast.LENGTH_SHORT).show();
            } else {
                android.widget.Toast.makeText(this, "Selected bank: " + selectedBank, android.widget.Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(NetBankingActivity.this, BankLoginActivity.class);
                intent.putExtra("selectedBank", selectedBank);
                startActivity(intent);

            }
        });
    }

    private void hideSystemBars() {
        View decorView = getWindow().getDecorView();
        WindowInsetsController windowInsetsController = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowInsetsController = decorView.getWindowInsetsController();
        }
        if (windowInsetsController == null) {
            return;
        }
        // Configure the behavior of the hidden system bars
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowInsetsController.setSystemBarsBehavior(
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            );
            windowInsetsController.hide(WindowInsets.Type.systemBars());
        }
    }
}
