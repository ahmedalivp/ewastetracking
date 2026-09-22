package com.ewaste.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.ewaste.app.data.model.AuthResponse;
import com.ewaste.app.data.model.DropOffPointResponse;
import com.ewaste.app.data.model.RegisterRequest;
import com.ewaste.app.databinding.ActivityRegisterBinding;
import com.ewaste.app.ui.business.BusinessMainActivity;
import com.ewaste.app.ui.common.Resource;
import com.ewaste.app.ui.consumer.ConsumerMainActivity;
import com.ewaste.app.ui.facility.FacilityMainActivity;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private AuthViewModel viewModel;

    private final String[] roles = new String[]{"Consumer", "Facility Staff", "Business Enterprise"};
    private final String[] roleCodes = new String[]{"CONSUMER", "FACILITY_STAFF", "BUSINESS"};
    private String selectedRoleCode = "CONSUMER";

    private final List<DropOffPointResponse> dropOffPointList = new ArrayList<>();
    private Long selectedFacilityId = 1L; // Default fallback to facility 1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        setupRoleDropdown();
        setupListeners();
        observeValidation();
        loadDropOffPointsForFacilityStaff();
    }

    private void setupRoleDropdown() {
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                roles
        );
        binding.actvRole.setAdapter(roleAdapter);
        binding.actvRole.setText(roles[0], false);
        selectedRoleCode = roleCodes[0];

        binding.actvRole.setOnItemClickListener((parent, view, position, id) -> {
            selectedRoleCode = roleCodes[position];
            updateConditionalSections(selectedRoleCode);
        });
    }

    private void updateConditionalSections(String roleCode) {
        if ("FACILITY_STAFF".equals(roleCode)) {
            binding.layoutFacilitySection.setVisibility(View.VISIBLE);
            binding.layoutBusinessSection.setVisibility(View.GONE);
        } else if ("BUSINESS".equals(roleCode)) {
            binding.layoutFacilitySection.setVisibility(View.GONE);
            binding.layoutBusinessSection.setVisibility(View.VISIBLE);
        } else {
            binding.layoutFacilitySection.setVisibility(View.GONE);
            binding.layoutBusinessSection.setVisibility(View.GONE);
        }
    }

    private void loadDropOffPointsForFacilityStaff() {
        viewModel.getDropOffPoints().observe(this, resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
                dropOffPointList.clear();
                dropOffPointList.addAll(resource.data);

                List<String> names = new ArrayList<>();
                for (DropOffPointResponse point : dropOffPointList) {
                    String label = point.getLabel() != null ? point.getLabel() : "Facility Point";
                    String facility = point.getFacilityName() != null ? " (" + point.getFacilityName() + ")" : "";
                    names.add(label + facility);
                }

                if (!names.isEmpty()) {
                    ArrayAdapter<String> facilityAdapter = new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_dropdown_item_1line,
                            names
                    );
                    binding.actvFacility.setAdapter(facilityAdapter);
                    binding.actvFacility.setText(names.get(0), false);
                    selectedFacilityId = dropOffPointList.get(0).getId();

                    binding.actvFacility.setOnItemClickListener((parent, view, position, id) -> {
                        if (position >= 0 && position < dropOffPointList.size()) {
                            selectedFacilityId = dropOffPointList.get(position).getId();
                        }
                    });
                }
            }
        });
    }

    private void setupListeners() {
        binding.btnRegister.setOnClickListener(v -> performRegistration());
        binding.tvLoginLink.setOnClickListener(v -> finish());
    }

    private void observeValidation() {
        viewModel.getValidationError().observe(this, error -> {
            if (error != null) {
                Snackbar.make(binding.coordinatorLayout, error, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void performRegistration() {
        String fullName = binding.etFullName.getText() != null ? binding.etFullName.getText().toString().trim() : "";
        String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString().trim() : "";

        if (!viewModel.validateRegistration(fullName, email, password, selectedRoleCode)) {
            return;
        }

        RegisterRequest request = new RegisterRequest(fullName, email, password, selectedRoleCode);

        if ("FACILITY_STAFF".equals(selectedRoleCode)) {
            request.setFacilityId(selectedFacilityId != null ? selectedFacilityId : 1L);
        } else if ("BUSINESS".equals(selectedRoleCode)) {
            String bizName = binding.etBusinessName.getText() != null ? binding.etBusinessName.getText().toString().trim() : "";
            String bizType = binding.etBusinessType.getText() != null ? binding.etBusinessType.getText().toString().trim() : "";
            if (bizName.isEmpty()) {
                Snackbar.make(binding.coordinatorLayout, "Business name is required", Snackbar.LENGTH_SHORT).show();
                return;
            }
            request.setBusinessName(bizName);
            request.setBusinessType(bizType.isEmpty() ? "Refurbisher" : bizType);
        }

        viewModel.register(request).observe(this, this::handleRegistrationResponse);
    }

    private void handleRegistrationResponse(Resource<AuthResponse> resource) {
        if (resource == null) return;

        switch (resource.status) {
            case LOADING:
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.btnRegister.setEnabled(false);
                break;

            case SUCCESS:
                binding.progressBar.setVisibility(View.GONE);
                binding.btnRegister.setEnabled(true);
                if (resource.data != null) {
                    navigateToRoleHome(resource.data.getRole());
                }
                break;

            case ERROR:
                binding.progressBar.setVisibility(View.GONE);
                binding.btnRegister.setEnabled(true);
                String msg = resource.message != null ? resource.message : "Registration failed";
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
        } else {
            intent = new Intent(this, ConsumerMainActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
