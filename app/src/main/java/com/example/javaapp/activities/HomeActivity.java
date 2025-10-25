package com.example.javaapp.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.TaskStackBuilder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javaapp.R;
import com.example.javaapp.adapters.TenderNoticeAdapter;
import com.example.javaapp.models.TenderNotice;
import com.example.javaapp.services.ServiceUtils;
import com.example.javaapp.utils.AppPref;

import java.util.ArrayList;
import java.util.List;
import androidx.appcompat.widget.Toolbar;

public class HomeActivity extends AppCompatActivity {

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

        Button btnVerifyAccount = findViewById(R.id.btn_verify_account);
        btnVerifyAccount.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, UserInputActivity.class));
        });

        RecyclerView tenderList = findViewById(R.id.tender_list);
        tenderList.setLayoutManager(new LinearLayoutManager(this));
        List<TenderNotice> sampleNotices = new ArrayList<>();
        sampleNotices.add(new TenderNotice("Tender 1", "Construction of bridge.", "2025-10-05"));
        sampleNotices.add(new TenderNotice("Tender 2", "Road repair work.", "2025-10-10"));
        sampleNotices.add(new TenderNotice("Tender 3", "School building renovation.", "2025-10-15"));
        TenderNoticeAdapter adapter = new TenderNoticeAdapter(sampleNotices);
        tenderList.setAdapter(adapter);

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
}
