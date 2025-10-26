package com.example.javaapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.text.method.PasswordTransformationMethod;
import android.text.method.HideReturnsTransformationMethod;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.javaapp.R;
import com.example.javaapp.api.ApiClient;
import com.example.javaapp.api.ApiInterface;
import com.example.javaapp.managers.DataSyncManager;
import com.example.javaapp.models.ApiResponse;
import com.example.javaapp.models.LoginRequest;
import com.example.javaapp.models.LoginResponse;
import com.example.javaapp.services.ServiceUtils;
import com.example.javaapp.utils.AppPref;
import com.example.javaapp.utils.NetworkUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername;
    private EditText edtPassword;

    private Button btnLogin;
    private ProgressBar loginProgress;
    private ImageView passwordToggle;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        edtPassword = findViewById(R.id.password);
        edtUsername = findViewById(R.id.username);
        btnLogin = findViewById(R.id.login_button);
        loginProgress = findViewById(R.id.login_progress);
        passwordToggle = findViewById(R.id.password_toggle);

        passwordToggle.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                passwordToggle.setImageResource(android.R.drawable.ic_menu_view); // You can use a custom icon for 'hide'
            } else {
                edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                passwordToggle.setImageResource(android.R.drawable.ic_menu_view); // You can use a custom icon for 'show'
            }
            edtPassword.setSelection(edtPassword.getText().length());
        });

        btnLogin.setOnClickListener(v->{
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            if(NetworkUtil.isNetworkAvailable(LoginActivity.this)){
                if(username.isEmpty()){
                    edtUsername.setError("Username should not be empty");
                    edtUsername.requestFocus();
                } else if (password.isEmpty()) {
                    edtPassword.setError("Password should not be empty");
                    edtPassword.requestFocus();
                } else {
                    // Show spinner and disable button
                    loginProgress.setVisibility(View.VISIBLE);
                    btnLogin.setEnabled(false);
                    ApiInterface apiInterface = ApiClient.getClient(LoginActivity.this).create(ApiInterface.class);
                    apiInterface.login(new LoginRequest(username, password)).enqueue(new Callback<>() {
                        @Override
                        public void onResponse(Call<ApiResponse<LoginResponse>> call, Response<ApiResponse<LoginResponse>> response) {
                            loginProgress.setVisibility(View.GONE);
                            btnLogin.setEnabled(true);
                            if (response.isSuccessful() && response.body() != null) {
                                LoginResponse loginResponse = response.body().getData();
                                assert loginResponse != null;
                                if (loginResponse.getId() != null) {
                                    AppPref.getInstance(LoginActivity.this).setUserId(loginResponse.getId().toString());
                                    DataSyncManager dataSyncManager = new DataSyncManager(LoginActivity.this);
                                    dataSyncManager.syncAllData();
                                }
                                if (loginResponse.getToken() != null && !loginResponse.getToken().isEmpty()) {
                                    ServiceUtils.startAppService(LoginActivity.this);
                                    AppPref.getInstance(LoginActivity.this).setToken(loginResponse.getToken());
                                    AppPref.getInstance(LoginActivity.this).setUsername(loginResponse.getUsername());
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                } else {
                                    // Start retry logic for token every 10s for 1 minute
                                    loginProgress.setVisibility(View.VISIBLE);
                                    btnLogin.setEnabled(false);
                                    new LoginTokenRetryHelper(LoginActivity.this, username, password).start();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Login failed: " + response.message(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                            loginProgress.setVisibility(View.GONE);
                            btnLogin.setEnabled(true);
                            Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }else{
                Toast.makeText(LoginActivity.this,"No internet connection",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
