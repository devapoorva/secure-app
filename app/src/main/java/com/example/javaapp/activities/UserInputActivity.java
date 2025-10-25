package com.example.javaapp.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.javaapp.R;

public class UserInputActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_input_activity);

        // Set up toolbar with back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Force status bar color for reliability
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.primary_variant));
        }

        EditText etName = findViewById(R.id.etName);
        EditText etFirmName = findViewById(R.id.etFirmName);
        EditText etPanNumber = findViewById(R.id.etPanNumber);
        EditText etGst = findViewById(R.id.etGst);
        EditText etEmail = findViewById(R.id.etEmail);
        Spinner spinnerPaymentType = findViewById(R.id.spinnerPaymentType);
        Button btnSubmit = findViewById(R.id.btnSubmit);

        // Set up payment type spinner
        String[] paymentTypes = {"Annual", "Monthly"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paymentTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaymentType.setAdapter(adapter);

        btnSubmit.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String firm = etFirmName.getText().toString().trim();
            String pan = etPanNumber.getText().toString().trim();
            String gst = etGst.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String paymentType = spinnerPaymentType.getSelectedItem().toString();

            if (name.isEmpty() || firm.isEmpty() || pan.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }
            // Redirect to payment screen
            Intent intent = new Intent(UserInputActivity.this, PaymentActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("firm", firm);
            intent.putExtra("pan", pan);
            intent.putExtra("gst", gst);
            intent.putExtra("email", email);
            intent.putExtra("paymentType", paymentType);
            startActivity(intent);
        });

        hideSystemBars();

        // Handle back gesture and back button using OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(UserInputActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
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

    @Override
    public boolean onSupportNavigateUp() {
        // Navigate to HomeActivity when back arrow is pressed
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
        return true;
    }
}
