package com.ewaste.app.ui.business;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.ewaste.app.databinding.FragmentMyRequestsBinding;
import com.ewaste.app.ui.adapter.ComponentRequestsAdapter;
import com.ewaste.app.ui.common.Resource;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import com.ewaste.app.data.model.ComponentRequestResponse;

public class MyRequestsFragment extends Fragment {

    private FragmentMyRequestsBinding binding;
    private BusinessViewModel viewModel;
    private ComponentRequestsAdapter adapter;
    private final List<ComponentRequestResponse> allRequests = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMyRequestsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(BusinessViewModel.class);

        setupRecyclerView();
        setupFilters();
        setupSwipeRefresh();
        binding.btnTriangleMenu.setOnClickListener(v -> {
            com.ewaste.app.ui.consumer.QuickActionMenuSheet.newInstance()
                    .show(getParentFragmentManager(), "QuickActionMenuSheet");
        });
        loadRequests();
    }

    private void setupRecyclerView() {
        adapter = new ComponentRequestsAdapter(item -> {
            RequisitionDetailBottomSheet.newInstance(item)
                    .show(getParentFragmentManager(), "RequisitionDetailBottomSheet");
        });
        binding.rvMyRequests.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvMyRequests.setAdapter(adapter);
    }

    private void setupFilters() {
        binding.chipGroupStatus.setOnCheckedStateChangeListener((group, checkedIds) -> {
            applyFilter();
        });
    }

    private void applyFilter() {
        int checkedId = binding.chipGroupStatus.getCheckedChipId();
        List<ComponentRequestResponse> filtered = new ArrayList<>();

        if (checkedId == binding.chipPendingStatus.getId()) {
            for (ComponentRequestResponse r : allRequests) {
                if ("PENDING".equalsIgnoreCase(r.getStatus())) filtered.add(r);
            }
        } else if (checkedId == binding.chipApprovedStatus.getId()) {
            for (ComponentRequestResponse r : allRequests) {
                if ("APPROVED".equalsIgnoreCase(r.getStatus())) filtered.add(r);
            }
        } else if (checkedId == binding.chipFulfilledStatus.getId()) {
            for (ComponentRequestResponse r : allRequests) {
                if ("FULFILLED".equalsIgnoreCase(r.getStatus())) filtered.add(r);
            }
        } else {
            filtered.addAll(allRequests);
        }

        if (filtered.isEmpty()) {
            binding.layoutEmpty.setVisibility(View.VISIBLE);
            binding.rvMyRequests.setVisibility(View.GONE);
        } else {
            binding.layoutEmpty.setVisibility(View.GONE);
            binding.rvMyRequests.setVisibility(View.VISIBLE);
        }
        adapter.submitList(filtered);
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(this::loadRequests);
    }

    private void loadRequests() {
        viewModel.getMyRequests().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            binding.swipeRefresh.setRefreshing(false);

            if (resource.status == Resource.Status.LOADING) {
                if (adapter.getItemCount() == 0) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                }
            } else if (resource.status == Resource.Status.SUCCESS) {
                binding.progressBar.setVisibility(View.GONE);
                allRequests.clear();
                if (resource.data != null) {
                    allRequests.addAll(resource.data);
                }
                applyFilter();
            } else if (resource.status == Resource.Status.ERROR) {
                binding.progressBar.setVisibility(View.GONE);
                String msg = resource.message != null ? resource.message : "Failed to load requests";
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
