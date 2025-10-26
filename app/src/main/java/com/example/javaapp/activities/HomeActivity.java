package com.example.javaapp.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.TaskStackBuilder;
import androidx.appcompat.widget.Toolbar;

import com.example.javaapp.R;
import com.example.javaapp.api.ApiClient;
import com.example.javaapp.api.ApiInterface;
import com.example.javaapp.models.ApiResponse;
import com.example.javaapp.models.UserRequest;
import com.example.javaapp.models.UserResponseDto;
import com.example.javaapp.services.ServiceUtils;
import com.example.javaapp.utils.AppPref;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private EditText etUsername, etName, etFirmName, etPanNumber, etGst, etEmail;
    private Button btnNext;

    @Override
    public void onCreateSupportNavigateUpTaskStack(@NonNull TaskStackBuilder builder) {
        super.onCreateSupportNavigateUpTaskStack(builder);
        setContentView(R.layout.home_activity);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("mJunction");
        etUsername = findViewById(R.id.et_username);
        etName = findViewById(R.id.et_name);
        etFirmName = findViewById(R.id.et_firm_name);
        etPanNumber = findViewById(R.id.et_pan_number);
        etGst = findViewById(R.id.et_gst);
        etEmail = findViewById(R.id.et_email);

        btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(v -> {
            showSubscriptionDialog();
        });
        fetchUserDetails();
        hideSystemBars();
    }

    private void fetchUserDetails() {
        ApiInterface api = ApiClient.getClient(HomeActivity.this).create(ApiInterface.class);
        UserRequest request = new UserRequest();
        // Set login request fields as needed
        request.setUsername(AppPref.getInstance(HomeActivity.this).getUsername());
        Call<ApiResponse<UserResponseDto>> call = api.user(request);
        call.enqueue(new Callback<ApiResponse<UserResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponseDto>> call, Response<ApiResponse<UserResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    UserResponseDto user = response.body().getData();
                    etUsername.setText(user.getUsername());
                    etName.setText(user.getCommodity());
                    etFirmName.setText(user.getFirstName());
                    etPanNumber.setText(user.getPanNumber());
                    etGst.setText(user.getPanNumber());
                    etEmail.setText(user.getEmail());
                } else {
                    Toast.makeText(HomeActivity.this, "Failed to load user details", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<UserResponseDto>> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ensure service is running when user opens the app
        if (!ServiceUtils.isServiceRunning(this)) {
            ServiceUtils.startAppService(this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            AppPref.getInstance(HomeActivity.this).clear();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSubscriptionDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_subscription, null);
        builder.setView(dialogView);

        Button btnSubscribe = dialogView.findViewById(R.id.btn_subscribe);
        builder.setCancelable(true);
        android.app.AlertDialog dialog = builder.create();

        btnSubscribe.setOnClickListener(view -> {
            dialog.dismiss();
            Intent intent = new Intent(HomeActivity.this, NetBankingActivity.class);
            intent.putExtra("amount", 99);
            intent.putExtra("type", "Annual");
            startActivity(intent);
        });
        dialog.show();
    }
}
