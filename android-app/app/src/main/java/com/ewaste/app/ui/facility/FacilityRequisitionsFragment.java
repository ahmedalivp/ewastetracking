package com.ewaste.app.ui.facility;

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
import com.ewaste.app.data.model.ComponentRequestResponse;
import com.ewaste.app.databinding.FragmentFacilityRequisitionsBinding;
import com.ewaste.app.ui.business.RequisitionDetailBottomSheet;
import com.ewaste.app.ui.common.Resource;
import com.ewaste.app.ui.consumer.QuickActionMenuSheet;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FacilityRequisitionsFragment extends Fragment {

    private FragmentFacilityRequisitionsBinding binding;
    private FacilityViewModel viewModel;
    private FacilityRequisitionsAdapter adapter;
    private final List<ComponentRequestResponse> allRequisitions = new ArrayList<>();
    private String currentSearchQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFacilityRequisitionsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(FacilityViewModel.class);

        setupRecyclerView();
        setupSearch();
        setupFilters();
        setupSwipeRefresh();

        binding.btnTriangleMenu.setOnClickListener(v -> {
            QuickActionMenuSheet.newInstance()
                    .show(getParentFragmentManager(), "QuickActionMenuSheet");
        });

        loadRequisitions();
    }

    private void setupRecyclerView() {
        adapter = new FacilityRequisitionsAdapter(new FacilityRequisitionsAdapter.OnActionListener() {
            @Override
            public void onApprove(ComponentRequestResponse item) {
                confirmApprove(item);
            }

            @Override
            public void onFulfill(ComponentRequestResponse item) {
                confirmFulfill(item);
            }

            @Override
            public void onItemClick(ComponentRequestResponse item) {
                RequisitionDetailBottomSheet.newInstance(item)
                        .show(getParentFragmentManager(), "RequisitionDetailBottomSheet");
            }
        });

        binding.rvRequisitions.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvRequisitions.setAdapter(adapter);
    }

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s != null ? s.toString().trim().toLowerCase(Locale.getDefault()) : "";
                binding.btnClearSearch.setVisibility(currentSearchQuery.isEmpty() ? View.GONE : View.VISIBLE);
                applyFilterAndSearch();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.btnClearSearch.setOnClickListener(v -> {
            binding.etSearch.setText("");
        });
    }

    private void setupFilters() {
        binding.chipGroupStatus.setOnCheckedStateChangeListener((group, checkedIds) -> {
            applyFilterAndSearch();
        });
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(this::loadRequisitions);
    }

    private void loadRequisitions() {
        viewModel.getComponentRequests().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            binding.swipeRefresh.setRefreshing(false);

            if (resource.status == Resource.Status.LOADING) {
                if (adapter.getItemCount() == 0) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                }
            } else if (resource.status == Resource.Status.SUCCESS) {
                binding.progressBar.setVisibility(View.GONE);
                allRequisitions.clear();
                if (resource.data != null) {
                    allRequisitions.addAll(resource.data);
                }
                applyFilterAndSearch();
            } else if (resource.status == Resource.Status.ERROR) {
                binding.progressBar.setVisibility(View.GONE);
                String msg = resource.message != null ? resource.message : "Failed to load requisitions";
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void applyFilterAndSearch() {
        int checkedId = binding.chipGroupStatus.getCheckedChipId();
        List<ComponentRequestResponse> filtered = new ArrayList<>();

        for (ComponentRequestResponse item : allRequisitions) {
            // Status check
            boolean matchesStatus = true;
            if (checkedId == binding.chipPendingStatus.getId()) {
                matchesStatus = "PENDING".equalsIgnoreCase(item.getStatus());
            } else if (checkedId == binding.chipApprovedStatus.getId()) {
                matchesStatus = "APPROVED".equalsIgnoreCase(item.getStatus());
            } else if (checkedId == binding.chipFulfilledStatus.getId()) {
                matchesStatus = "FULFILLED".equalsIgnoreCase(item.getStatus());
            }

            if (!matchesStatus) continue;

            // Search query check
            if (!currentSearchQuery.isEmpty()) {
                String name = item.getComponentName() != null ? item.getComponentName().toLowerCase(Locale.getDefault()) : "";
                String business = item.getBusinessName() != null ? item.getBusinessName().toLowerCase(Locale.getDefault()) : "";
                String idStr = String.valueOf(item.getId());

                if (!name.contains(currentSearchQuery) && !business.contains(currentSearchQuery) && !idStr.contains(currentSearchQuery)) {
                    continue;
                }
            }

            filtered.add(item);
        }

        if (filtered.isEmpty()) {
            binding.layoutEmpty.setVisibility(View.VISIBLE);
            binding.rvRequisitions.setVisibility(View.GONE);
        } else {
            binding.layoutEmpty.setVisibility(View.GONE);
            binding.rvRequisitions.setVisibility(View.VISIBLE);
        }

        adapter.submitList(filtered);
    }

    private void confirmApprove(ComponentRequestResponse item) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Approve Requisition #" + item.getId())
                .setMessage("Authorize this component transfer for " + (item.getBusinessName() != null ? item.getBusinessName() : "business enterprise") + "?")
                .setPositiveButton("Approve", (dialog, which) -> {
                    updateStatus(item.getId(), "APPROVED", "Requisition #" + item.getId() + " approved");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmFulfill(ComponentRequestResponse item) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Fulfill & Issue Transfer Certificate")
                .setMessage("Fulfill requisition #" + item.getId() + " and issue certified circular transfer certificate?")
                .setPositiveButton("Fulfill & Certify", (dialog, which) -> {
                    updateStatus(item.getId(), "FULFILLED", "Requisition #" + item.getId() + " fulfilled & transfer certificate issued");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateStatus(Long requestId, String newStatus, String successMsg) {
        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.updateComponentRequestStatus(requestId, newStatus).observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            if (resource.status == Resource.Status.SUCCESS) {
                binding.progressBar.setVisibility(View.GONE);
                Snackbar.make(binding.getRoot(), successMsg, Snackbar.LENGTH_SHORT).show();
                loadRequisitions();
            } else if (resource.status == Resource.Status.ERROR) {
                binding.progressBar.setVisibility(View.GONE);
                String msg = resource.message != null ? resource.message : "Failed to update requisition status";
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
