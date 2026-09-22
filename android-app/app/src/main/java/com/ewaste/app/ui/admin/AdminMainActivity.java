package com.ewaste.app.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.ewaste.app.R;
import com.ewaste.app.data.local.SessionManager;
import com.ewaste.app.data.model.FacilityResponse;
import com.ewaste.app.databinding.ActivityAdminMainBinding;
import com.ewaste.app.ui.adapter.AdminFacilitiesAdapter;
import com.ewaste.app.ui.auth.LoginActivity;
import com.ewaste.app.ui.common.Resource;
import com.google.android.material.snackbar.Snackbar;

public class AdminMainActivity extends AppCompatActivity {

    private ActivityAdminMainBinding binding;
    private AdminViewModel viewModel;
    private AdminFacilitiesAdapter adapter;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        setupToolbar();
        setupRecyclerView();
        setupSwipeRefresh();
        loadDashboardData();
    }

    private void setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener(this::onMenuItemClick);
    }

    private boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            sessionManager.clearSession();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return false;
    }

    private void setupRecyclerView() {
        adapter = new AdminFacilitiesAdapter(this::onToggleFacilityVerification);
        binding.rvFacilities.setLayoutManager(new LinearLayoutManager(this));
        binding.rvFacilities.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(this::loadDashboardData);
    }

    private void loadDashboardData() {
        loadImpactStats();
        loadFacilities();
    }

    private void loadImpactStats() {
        viewModel.getImpactStats().observe(this, resource -> {
            if (resource == null) return;
            binding.swipeRefresh.setRefreshing(false);

            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                binding.tvDivertedKg.setText(String.format("%.1f", resource.data.getDivertedLandfillKg()));
                binding.tvCo2Saved.setText(String.format("🌱 Estimated %.1f kg CO₂ emissions prevented", resource.data.getCo2EmissionsSavedKg()));
                binding.tvStatSubmissions.setText(String.valueOf(resource.data.getTotalSubmissions()));
                binding.tvStatHarvested.setText(String.valueOf(resource.data.getTotalComponentsHarvested()));
                binding.tvStatToxins.setText(String.valueOf(resource.data.getTotalHazardousDiverted()));
                binding.tvStatCredits.setText(String.valueOf(resource.data.getTotalCreditsAwarded()));
            }
        });
    }

    private void loadFacilities() {
        viewModel.getFacilities().observe(this, resource -> {
            if (resource == null) return;

            if (resource.status == Resource.Status.LOADING) {
                if (adapter.getItemCount() == 0) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                }
            } else if (resource.status == Resource.Status.SUCCESS) {
                binding.progressBar.setVisibility(View.GONE);
                if (resource.data != null) {
                    adapter.submitList(resource.data);
                }
            } else if (resource.status == Resource.Status.ERROR) {
                binding.progressBar.setVisibility(View.GONE);
                Snackbar.make(binding.getRoot(), "Failed to load facilities", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void onToggleFacilityVerification(FacilityResponse facility, boolean newStatus) {
        if (facility == null || facility.getId() == null) return;

        viewModel.verifyFacility(facility.getId(), newStatus).observe(this, resource -> {
            if (resource == null) return;

            if (resource.status == Resource.Status.SUCCESS) {
                String action = newStatus ? "verified" : "revoked";
                Snackbar.make(binding.getRoot(), facility.getName() + " " + action, Snackbar.LENGTH_SHORT).show();
                loadFacilities();
            } else if (resource.status == Resource.Status.ERROR) {
                Snackbar.make(binding.getRoot(), "Failed to update status", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}
