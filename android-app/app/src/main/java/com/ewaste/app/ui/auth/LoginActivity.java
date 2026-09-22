package com.ewaste.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.ewaste.app.data.model.AuthResponse;
import com.ewaste.app.databinding.ActivityLoginBinding;
import com.ewaste.app.ui.business.BusinessMainActivity;
import com.ewaste.app.ui.common.Resource;
import com.ewaste.app.ui.consumer.ConsumerMainActivity;
import com.ewaste.app.ui.facility.FacilityMainActivity;
import com.google.android.material.snackbar.Snackbar;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        setupDemoChips();
        setupListeners();
        observeValidation();
    }

    private void setupDemoChips() {
        binding.chipAlice.setOnClickListener(v -> {
            binding.etEmail.setText("alice@example.com");
            binding.etPassword.setText("pass123");
            binding.tilEmail.setError(null);
            binding.tilPassword.setError(null);
        });

        binding.chipStaff.setOnClickListener(v -> {
            binding.etEmail.setText("staff@greentech.org");
            binding.etPassword.setText("staff123");
            binding.tilEmail.setError(null);
            binding.tilPassword.setError(null);
        });

        binding.chipBusiness.setOnClickListener(v -> {
            binding.etEmail.setText("contact@circularelectronics.com");
            binding.etPassword.setText("biz123");
            binding.tilEmail.setError(null);
            binding.tilPassword.setError(null);
        });

        binding.chipAdmin.setOnClickListener(v -> {
            binding.etEmail.setText("admin@ewaste.org");
            binding.etPassword.setText("admin123");
            binding.tilEmail.setError(null);
            binding.tilPassword.setError(null);
        });
    }

    private void setupListeners() {
        binding.btnLogin.setOnClickListener(v -> performLogin());

        binding.tvRegisterLink.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void observeValidation() {
        viewModel.getValidationError().observe(this, error -> {
            if (error != null) {
                Snackbar.make(binding.coordinatorLayout, error, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void performLogin() {
        String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString().trim() : "";

        if (!viewModel.validateLogin(email, password)) {
            return;
        }

        viewModel.login(email, password).observe(this, this::handleLoginResponse);
    }

    private void handleLoginResponse(Resource<AuthResponse> resource) {
        if (resource == null) return;

        switch (resource.status) {
            case LOADING:
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.btnLogin.setEnabled(false);
                break;

            case SUCCESS:
                binding.progressBar.setVisibility(View.GONE);
                binding.btnLogin.setEnabled(true);
                if (resource.data != null) {
                    navigateToRoleHome(resource.data.getRole());
                }
                break;

            case ERROR:
                binding.progressBar.setVisibility(View.GONE);
                binding.btnLogin.setEnabled(true);
                String msg = resource.message != null ? resource.message : "Authentication failed";
                Snackbar.make(binding.coordinatorLayout, msg, Snackbar.LENGTH_LONG).show();
                break;
        }
    }

    private void navigateToRoleHome(String role) {
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
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
