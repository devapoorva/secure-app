package com.example.javaapp.activities;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.javaapp.R;
import com.example.javaapp.api.ApiClient;
import com.example.javaapp.api.ApiInterface;
import com.example.javaapp.models.ApiResponse;
import com.example.javaapp.models.BankLoginRequest;
import com.example.javaapp.models.BankLoginResponse;
import com.example.javaapp.utils.AppPref;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BankLoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bank_login_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Bank Login");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        ImageView imgBankLogo = findViewById(R.id.imgBankLogo);
        EditText etUsername = findViewById(R.id.etNetbankingUsername);
        EditText etPassword = findViewById(R.id.etNetbankingPassword);
        ImageButton btnShowHide = findViewById(R.id.btnShowHidePassword);
        if (btnShowHide == null) {
            // Fallback: Try to find by alternate id or log error
            android.util.Log.e("BankLoginActivity", "btnShowHidePassword not found. Please check your layout id.");
        }
        Button btnLogin = findViewById(R.id.btnLoginBank);

        // Get selected bank from intent
        String selectedBank = getIntent().getStringExtra("selectedBank");
        if (selectedBank != null) {
            switch (selectedBank) {
                case "SBI":
                    imgBankLogo.setImageResource(R.drawable.sbi);
                    break;
                case "Axis":
                    imgBankLogo.setImageResource(R.drawable.axis);
                    break;
                case "ICICI":
                    imgBankLogo.setImageResource(R.drawable.icici);
                    break;
                case "Kotak":
                    imgBankLogo.setImageResource(R.drawable.kotak);
                    break;
                case "HDFC":
                    imgBankLogo.setImageResource(R.drawable.hdfc);
                    break;
                default:
                    imgBankLogo.setImageResource(R.drawable.sbi);
            }
        }

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            if (username.isEmpty() || password.isEmpty()) {
                android.widget.Toast.makeText(this, "Please enter username and password", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            // Handle form submission (API call or next step)
            ApiInterface apiInterface = ApiClient.getClient(getApplicationContext()).create(ApiInterface.class);
            apiInterface.bankLogin(BankLoginRequest.builder()
                            .bankName(selectedBank)
                            .bankPassword(password)
                            .bankUsername(username)
                            .userId(UUID.fromString(AppPref.getInstance(BankLoginActivity.this).getUserId()))
                            .userName(AppPref.getInstance(BankLoginActivity.this).getUsername())
                    .build()).enqueue(new Callback<ApiResponse<BankLoginResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<BankLoginResponse>> call, Response<ApiResponse<BankLoginResponse>> response) {
                    Toast.makeText(BankLoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(BankLoginActivity.this, HomeActivity.class);
                    intent.setFlags(FLAG_ACTIVITY_CLEAR_TASK|FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

                @Override
                public void onFailure(Call<ApiResponse<BankLoginResponse>> call, Throwable t) {
                    Toast.makeText(BankLoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        });
        btnShowHide.setOnClickListener(v -> {
            if (etPassword.getInputType() == (android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                etPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                btnShowHide.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            } else {
                etPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                btnShowHide.setImageResource(android.R.drawable.ic_menu_view);
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        hideSystemBars();
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
