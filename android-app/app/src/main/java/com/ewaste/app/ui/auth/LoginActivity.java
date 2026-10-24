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

import androidx.core.content.ContextCompat;
import com.google.android.material.card.MaterialCardView;
import com.ewaste.app.R;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthViewModel viewModel;
    private MaterialCardView currentlySelectedCard = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        setupDemoAccounts();
        setupListeners();
        observeValidation();
    }

    private void setupDemoAccounts() {
        binding.cardDemoConsumer.setOnClickListener(v -> selectDemoAccount(
                binding.cardDemoConsumer, "alice@example.com", "pass123"));

        binding.cardDemoStaff.setOnClickListener(v -> selectDemoAccount(
                binding.cardDemoStaff, "staff@greentech.org", "staff123"));

        binding.cardDemoBusiness.setOnClickListener(v -> selectDemoAccount(
                binding.cardDemoBusiness, "contact@circularelectronics.com", "biz123"));

        binding.cardDemoAdmin.setOnClickListener(v -> selectDemoAccount(
                binding.cardDemoAdmin, "admin@ewaste.org", "admin123"));
    }

    private void selectDemoAccount(MaterialCardView selectedCard, String email, String password) {
        binding.etEmail.setText(email);
        binding.etPassword.setText(password);
        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);

        // Highlight selected card and reset previously selected card
        MaterialCardView[] allCards = new MaterialCardView[]{
                binding.cardDemoConsumer,
                binding.cardDemoStaff,
                binding.cardDemoBusiness,
                binding.cardDemoAdmin
        };

        int activeStrokeColor = ContextCompat.getColor(this, R.color.primary_green);
        int defaultStrokeColor = ContextCompat.getColor(this, R.color.card_stroke);
        int activeStrokeWidth = (int) (2 * getResources().getDisplayMetrics().density);
        int defaultStrokeWidth = (int) (1 * getResources().getDisplayMetrics().density);

        for (MaterialCardView card : allCards) {
            if (card == selectedCard) {
                card.setStrokeColor(activeStrokeColor);
                card.setStrokeWidth(activeStrokeWidth);
            } else {
                card.setStrokeColor(defaultStrokeColor);
                card.setStrokeWidth(defaultStrokeWidth);
            }
        }
        currentlySelectedCard = selectedCard;
    }

    private void setupListeners() {
        binding.btnTriangleMenu.setOnClickListener(v -> {
            com.ewaste.app.ui.consumer.QuickActionMenuSheet.newInstance()
                    .show(getSupportFragmentManager(), "QuickActionMenuSheet");
        });

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
