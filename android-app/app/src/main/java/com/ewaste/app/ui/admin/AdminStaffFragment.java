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
import com.ewaste.app.data.model.StaffMemberResponse;
import com.ewaste.app.data.model.UpdateStaffAccessRequest;
import com.ewaste.app.databinding.FragmentAdminStaffBinding;
import com.ewaste.app.ui.common.Resource;
import com.ewaste.app.ui.consumer.QuickActionMenuSheet;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class AdminStaffFragment extends Fragment implements AdminStaffAdapter.OnStaffActionListener {

    private FragmentAdminStaffBinding binding;
    private AdminViewModel viewModel;
    private AdminStaffAdapter adapter;
    private final List<StaffMemberResponse> allStaff = new ArrayList<>();

    public static AdminStaffFragment newInstance() {
        return new AdminStaffFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminStaffBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);

        setupUI();
        loadStaff();
    }

    private void setupUI() {
        binding.btnTriangleMenu.setOnClickListener(v -> {
            QuickActionMenuSheet.newInstance()
                    .show(getParentFragmentManager(), "QuickActionMenuSheet");
        });

        adapter = new AdminStaffAdapter(this);
        binding.rvStaff.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvStaff.setAdapter(adapter);

        binding.swipeRefresh.setOnRefreshListener(this::loadStaff);

        binding.etSearchStaff.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilter();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.chipGroupStaffFilter.setOnCheckedStateChangeListener((group, checkedIds) -> applyFilter());
    }

    private void loadStaff() {
        viewModel.getAdminStaff(null).observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;
            binding.swipeRefresh.setRefreshing(false);

            if (resource.status == Resource.Status.LOADING) {
                if (allStaff.isEmpty()) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                }
            } else if (resource.status == Resource.Status.SUCCESS) {
                binding.progressBar.setVisibility(View.GONE);
                allStaff.clear();
                if (resource.data != null) {
                    allStaff.addAll(resource.data);
                }
                applyFilter();
            } else if (resource.status == Resource.Status.ERROR) {
                binding.progressBar.setVisibility(View.GONE);
                Snackbar.make(binding.getRoot(), "Failed to load staff list", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFilter() {
        String query = binding.etSearchStaff.getText() != null
                ? binding.etSearchStaff.getText().toString().trim().toLowerCase()
                : "";

        int checkedChipId = binding.chipGroupStaffFilter.getCheckedChipId();
        List<StaffMemberResponse> filtered = new ArrayList<>();

        for (StaffMemberResponse s : allStaff) {
            boolean matchesChip;
            if (checkedChipId == R.id.chipAuthorized) {
                matchesChip = s.isActive();
            } else if (checkedChipId == R.id.chipSuspended) {
                matchesChip = !s.isActive();
            } else {
                matchesChip = true; // All
            }

            if (!matchesChip) continue;

            if (!query.isEmpty()) {
                String name = s.getFullName() != null ? s.getFullName().toLowerCase() : "";
                String email = s.getEmail() != null ? s.getEmail().toLowerCase() : "";
                String facility = s.getFacilityName() != null ? s.getFacilityName().toLowerCase() : "";
                if (!name.contains(query) && !email.contains(query) && !facility.contains(query)) {
                    continue;
                }
            }

            filtered.add(s);
        }

        adapter.submitList(new ArrayList<>(filtered));

        if (filtered.isEmpty()) {
            binding.layoutEmptyState.setVisibility(View.VISIBLE);
            binding.rvStaff.setVisibility(View.GONE);
        } else {
            binding.layoutEmptyState.setVisibility(View.GONE);
            binding.rvStaff.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onToggleActive(StaffMemberResponse staff, boolean active) {
        if (staff == null || staff.getId() == null) return;

        UpdateStaffAccessRequest req = new UpdateStaffAccessRequest(null, active);
        viewModel.updateStaffAccess(staff.getId(), req).observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;
            if (resource.status == Resource.Status.SUCCESS) {
                String state = active ? "authorized" : "suspended";
                Snackbar.make(binding.getRoot(), staff.getFullName() + " access " + state, Snackbar.LENGTH_SHORT).show();
                loadStaff();
            } else if (resource.status == Resource.Status.ERROR) {
                Snackbar.make(binding.getRoot(), "Failed to update staff access", Snackbar.LENGTH_SHORT).show();
                loadStaff(); // Revert UI
            }
        });
    }

    @Override
    public void onReassignFacility(StaffMemberResponse staff) {
        if (staff == null || staff.getId() == null) return;

        ReassignStaffBottomSheet sheet = ReassignStaffBottomSheet.newInstance(
                staff.getId(),
                staff.getFullName(),
                staff.getFacilityId()
        );
        sheet.setOnReassignedListener(this::loadStaff);
        sheet.show(getChildFragmentManager(), "ReassignStaffBottomSheet");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
