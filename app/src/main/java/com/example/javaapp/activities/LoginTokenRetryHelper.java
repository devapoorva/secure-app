package com.example.javaapp.activities;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.javaapp.R;
import com.example.javaapp.api.ApiClient;
import com.example.javaapp.api.ApiInterface;
import com.example.javaapp.models.ApiResponse;
import com.example.javaapp.models.LoginRequest;
import com.example.javaapp.models.LoginResponse;
import com.example.javaapp.utils.AppPref;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginTokenRetryHelper {
    private final LoginActivity activity;
    private final String username;
    private final String password;
    private int attempts = 0;
    private final int maxAttempts = 6;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public LoginTokenRetryHelper(LoginActivity activity, String username, String password) {
        this.activity = activity;
        this.username = username;
        this.password = password;
    }

    public void start() {
        handler.postDelayed(this::checkToken, 10000); // first retry after 10s
    }

    private void checkToken() {
        attempts++;
        ApiInterface apiInterface = ApiClient.getClient(activity.getApplicationContext()).create(ApiInterface.class);
        apiInterface.login(new LoginRequest(username, password)).enqueue(new Callback<ApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResponse>> call, Response<ApiResponse<LoginResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().getData().getToken().isBlank()) {
                    AppPref.getInstance(activity).setToken(response.body().getData().getToken());
                    AppPref.getInstance(activity).setUsername(response.body().getData().getUsername());
                    Intent intent = new Intent(activity, HomeActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                } else if (attempts < maxAttempts) {
                    handler.postDelayed(LoginTokenRetryHelper.this::checkToken, 10000); // retry after 10s
                } else {
                    Toast.makeText(activity, "Login failed", Toast.LENGTH_LONG).show();
                    activity.runOnUiThread(() -> {
                        activity.findViewById(R.id.login_progress).setVisibility(android.view.View.GONE);
                        activity.findViewById(R.id.login_button).setEnabled(true);
                    });
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                if (attempts < maxAttempts) {
                    handler.postDelayed(LoginTokenRetryHelper.this::checkToken, 10000); // retry after 10s
                } else {
                    Toast.makeText(activity, "Login failed: Network error after 1 minute.", Toast.LENGTH_LONG).show();
                    activity.runOnUiThread(() -> {
                        activity.findViewById(R.id.login_progress).setVisibility(android.view.View.GONE);
                        activity.findViewById(R.id.login_button).setEnabled(true);
                    });
                }
            }
        });
    }
}

