package com.ewaste.app.ui.business;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.ewaste.app.R;
import com.ewaste.app.data.model.ComponentResponse;
import com.ewaste.app.databinding.DialogRequestComponentBinding;
import com.ewaste.app.databinding.FragmentBrowseComponentsBinding;
import com.ewaste.app.ui.adapter.MarketplaceComponentAdapter;
import com.ewaste.app.ui.common.Resource;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

public class BrowseComponentsFragment extends Fragment {

    private FragmentBrowseComponentsBinding binding;
    private BusinessViewModel viewModel;
    private MarketplaceComponentAdapter adapter;
    private String currentCategoryFilter = null; // null = all

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBrowseComponentsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(BusinessViewModel.class);

        setupRecyclerView();
        setupFilterChips();
        setupSwipeRefresh();
        loadComponents();
    }

    private void setupRecyclerView() {
        adapter = new MarketplaceComponentAdapter(this::onPartRequested);
        binding.rvBrowseComponents.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvBrowseComponents.setAdapter(adapter);
    }

    private void setupFilterChips() {
        binding.chipGroupCategory.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chipAll) {
                currentCategoryFilter = null;
            } else if (id == R.id.chipLaptop) {
                currentCategoryFilter = "Laptop";
            } else if (id == R.id.chipSmartphone) {
                currentCategoryFilter = "Smartphone";
            } else if (id == R.id.chipBattery) {
                currentCategoryFilter = "Battery";
            } else if (id == R.id.chipTV) {
                currentCategoryFilter = "TV";
            }
            loadComponents();
        });
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(this::loadComponents);
    }

    private void loadComponents() {
        viewModel.getAvailableComponents(currentCategoryFilter).observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            binding.swipeRefresh.setRefreshing(false);

            if (resource.status == Resource.Status.LOADING) {
                if (adapter.getItemCount() == 0) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                }
            } else if (resource.status == Resource.Status.SUCCESS) {
                binding.progressBar.setVisibility(View.GONE);
                if (resource.data != null && !resource.data.isEmpty()) {
                    binding.layoutEmpty.setVisibility(View.GONE);
                    binding.rvBrowseComponents.setVisibility(View.VISIBLE);
                    adapter.submitList(resource.data);
                } else {
                    binding.layoutEmpty.setVisibility(View.VISIBLE);
                    binding.rvBrowseComponents.setVisibility(View.GONE);
                }
            } else if (resource.status == Resource.Status.ERROR) {
                binding.progressBar.setVisibility(View.GONE);
                String msg = resource.message != null ? resource.message : "Failed to load components";
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void onPartRequested(ComponentResponse component) {
        if (getContext() == null) return;

        DialogRequestComponentBinding dialogBinding = DialogRequestComponentBinding.inflate(getLayoutInflater());
        dialogBinding.tvDialogPartName.setText(component.getName() != null ? component.getName() : "Salvaged Part");
        dialogBinding.tvDialogDetails.setText(String.format("Condition: %s • Type: %s • Category: %s",
                component.getCondition() != null ? component.getCondition() : "FAIR",
                component.getType() != null ? component.getType() : "Component",
                component.getCategoryName() != null ? component.getCategoryName() : "Electronics"));

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogBinding.getRoot())
                .create();

        dialogBinding.btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialogBinding.btnConfirmRequest.setOnClickListener(v -> {
            dialogBinding.btnConfirmRequest.setEnabled(false);

            viewModel.requestComponent(component.getId()).observe(getViewLifecycleOwner(), resource -> {
                if (resource != null && resource.status == Resource.Status.SUCCESS) {
                    dialog.dismiss();
                    Snackbar.make(binding.getRoot(), "✅ Requisition submitted for " + component.getName() + "!", Snackbar.LENGTH_LONG)
                            .setAction("View Requests", view -> {
                                if (getActivity() instanceof BusinessMainActivity) {
                                    ((BusinessMainActivity) getActivity()).navigateToRequests();
                                }
                            })
                            .show();
                    loadComponents();
                } else if (resource != null && resource.status == Resource.Status.ERROR) {
                    dialogBinding.btnConfirmRequest.setEnabled(true);
                    String msg = resource.message != null ? resource.message : "Requisition failed";
                    Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
                }
            });
        });

        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
