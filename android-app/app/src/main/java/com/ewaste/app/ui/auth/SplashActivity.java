package com.ewaste.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.ewaste.app.EWasteApplication;
import com.ewaste.app.data.local.SessionManager;
import com.ewaste.app.databinding.ActivitySplashBinding;
import com.ewaste.app.ui.business.BusinessMainActivity;
import com.ewaste.app.ui.consumer.ConsumerMainActivity;
import com.ewaste.app.ui.facility.FacilityMainActivity;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        new Handler(Looper.getMainLooper()).postDelayed(this::checkSessionAndNavigate, 1200);
    }

    private void checkSessionAndNavigate() {
        SessionManager sessionManager = EWasteApplication.getInstance().getSessionManager();
        if (sessionManager.isLoggedIn()) {
            String role = sessionManager.getRole();
            Intent intent;
            if ("FACILITY_STAFF".equalsIgnoreCase(role)) {
                intent = new Intent(this, FacilityMainActivity.class);
            } else if ("BUSINESS".equalsIgnoreCase(role)) {
                intent = new Intent(this, BusinessMainActivity.class);
            } else if ("ADMIN".equalsIgnoreCase(role)) {
                intent = new Intent(this, com.ewaste.app.ui.admin.AdminMainActivity.class);
            } else {
                intent = new Intent(this, ConsumerMainActivity.class);
            }
            startActivity(intent);
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }
}
