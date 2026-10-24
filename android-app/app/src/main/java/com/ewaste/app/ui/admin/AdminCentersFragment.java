package com.ewaste.app.ui.admin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.ewaste.app.R;
import com.ewaste.app.data.model.FacilityResponse;
import com.ewaste.app.databinding.FragmentAdminCentersBinding;
import com.ewaste.app.ui.common.Resource;
import com.ewaste.app.ui.consumer.QuickActionMenuSheet;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class AdminCentersFragment extends Fragment implements AdminCentersAdapter.OnCenterActionListener {

    private FragmentAdminCentersBinding binding;
    private AdminViewModel viewModel;
    private AdminCentersAdapter adapter;
    private final List<FacilityResponse> allFacilities = new ArrayList<>();
    private boolean selectPendingOnNextLoad = false;

    public static AdminCentersFragment newInstance(boolean filterPending) {
        AdminCentersFragment fragment = new AdminCentersFragment();
        Bundle args = new Bundle();
        args.putBoolean("filter_pending", filterPending);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminCentersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);

        if (getArguments() != null && getArguments().getBoolean("filter_pending", false)) {
            selectPendingOnNextLoad = true;
        }

        setupUI();
        loadFacilities();
    }

    private void setupUI() {
        binding.btnTriangleMenu.setOnClickListener(v -> {
            QuickActionMenuSheet.newInstance()
                    .show(getParentFragmentManager(), "QuickActionMenuSheet");
        });

        adapter = new AdminCentersAdapter(this);
        binding.rvCenters.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCenters.setAdapter(adapter);

        binding.swipeRefresh.setOnRefreshListener(this::loadFacilities);

        binding.btnAddFacility.setOnClickListener(v -> {
            AddFacilityBottomSheet sheet = AddFacilityBottomSheet.newInstance(this::loadFacilities);
            sheet.show(getChildFragmentManager(), "AddFacilityBottomSheet");
        });

        binding.etSearchCenters.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilter();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.chipGroupFilter.setOnCheckedStateChangeListener((group, checkedIds) -> applyFilter());

        if (selectPendingOnNextLoad) {
            binding.chipPending.setChecked(true);
        }
    }

    public void selectPendingFilter() {
        if (binding != null) {
            binding.chipPending.setChecked(true);
        } else {
            selectPendingOnNextLoad = true;
        }
    }

    private void loadFacilities() {
        viewModel.getFacilities().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;
            binding.swipeRefresh.setRefreshing(false);

            if (resource.status == Resource.Status.LOADING) {
                if (allFacilities.isEmpty()) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                }
            } else if (resource.status == Resource.Status.SUCCESS) {
                binding.progressBar.setVisibility(View.GONE);
                allFacilities.clear();
                if (resource.data != null) {
                    allFacilities.addAll(resource.data);
                }
                applyFilter();
            } else if (resource.status == Resource.Status.ERROR) {
                binding.progressBar.setVisibility(View.GONE);
                Snackbar.make(binding.getRoot(), "Failed to load facilities", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFilter() {
        String query = binding.etSearchCenters.getText() != null
                ? binding.etSearchCenters.getText().toString().trim().toLowerCase()
                : "";

        int checkedChipId = binding.chipGroupFilter.getCheckedChipId();
        List<FacilityResponse> filtered = new ArrayList<>();

        for (FacilityResponse f : allFacilities) {
            // Check status chip filter
            boolean matchesChip;
            if (checkedChipId == R.id.chipPending) {
                matchesChip = !f.isVerifiedByAdmin();
            } else if (checkedChipId == R.id.chipVerified) {
                matchesChip = f.isVerifiedByAdmin() && f.isActive();
            } else if (checkedChipId == R.id.chipSuspended) {
                matchesChip = !f.isActive();
            } else {
                matchesChip = true; // All
            }

            if (!matchesChip) continue;

            // Check text search
            if (!query.isEmpty()) {
                String name = f.getName() != null ? f.getName().toLowerCase() : "";
                String address = f.getAddress() != null ? f.getAddress().toLowerCase() : "";
                if (!name.contains(query) && !address.contains(query)) {
                    continue;
                }
            }

            filtered.add(f);
        }

        adapter.submitList(filtered);

        if (filtered.isEmpty()) {
            binding.layoutEmptyState.setVisibility(View.VISIBLE);
            binding.rvCenters.setVisibility(View.GONE);
        } else {
            binding.layoutEmptyState.setVisibility(View.GONE);
            binding.rvCenters.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onManageAccess(FacilityResponse facility) {
        EditFacilityBottomSheet sheet = EditFacilityBottomSheet.newInstance(facility, this::loadFacilities);
        sheet.show(getChildFragmentManager(), "EditFacilityBottomSheet");
    }

    @Override
    public void onQuickApprove(FacilityResponse facility) {
        if (facility == null || facility.getId() == null) return;

        viewModel.verifyFacility(facility.getId(), true).observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;
            if (resource.status == Resource.Status.SUCCESS) {
                Snackbar.make(binding.getRoot(), "Approved " + facility.getName(), Snackbar.LENGTH_SHORT).show();
                loadFacilities();
            } else if (resource.status == Resource.Status.ERROR) {
                Snackbar.make(binding.getRoot(), "Failed to approve center", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
