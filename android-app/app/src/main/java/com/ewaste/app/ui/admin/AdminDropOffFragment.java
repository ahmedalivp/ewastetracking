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
import com.ewaste.app.data.model.DropOffPointResponse;
import com.ewaste.app.databinding.FragmentAdminDropoffBinding;
import com.ewaste.app.ui.common.Resource;
import com.ewaste.app.ui.consumer.QuickActionMenuSheet;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class AdminDropOffFragment extends Fragment implements AdminDropOffAdapter.OnDropOffActionListener {

    private FragmentAdminDropoffBinding binding;
    private AdminViewModel viewModel;
    private AdminDropOffAdapter adapter;
    private final List<DropOffPointResponse> allPoints = new ArrayList<>();

    public static AdminDropOffFragment newInstance() {
        return new AdminDropOffFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminDropoffBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);

        setupUI();
        loadDropOffPoints();
    }

    private void setupUI() {
        binding.btnTriangleMenu.setOnClickListener(v -> {
            QuickActionMenuSheet.newInstance()
                    .show(getParentFragmentManager(), "QuickActionMenuSheet");
        });

        adapter = new AdminDropOffAdapter(this);
        binding.rvDropOffPoints.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvDropOffPoints.setAdapter(adapter);

        binding.swipeRefresh.setOnRefreshListener(this::loadDropOffPoints);

        binding.btnAddDropOff.setOnClickListener(v -> {
            AddEditDropOffBottomSheet sheet = AddEditDropOffBottomSheet.newInstance(null, this::loadDropOffPoints);
            sheet.show(getChildFragmentManager(), "AddEditDropOffBottomSheet");
        });

        binding.etSearchDropOff.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilter();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadDropOffPoints() {
        viewModel.getDropOffPoints().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;
            binding.swipeRefresh.setRefreshing(false);

            if (resource.status == Resource.Status.LOADING) {
                if (allPoints.isEmpty()) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                }
            } else if (resource.status == Resource.Status.SUCCESS) {
                binding.progressBar.setVisibility(View.GONE);
                allPoints.clear();
                if (resource.data != null) {
                    allPoints.addAll(resource.data);
                }
                applyFilter();
            } else if (resource.status == Resource.Status.ERROR) {
                binding.progressBar.setVisibility(View.GONE);
                Snackbar.make(binding.getRoot(), "Failed to load drop-off points", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFilter() {
        String query = binding.etSearchDropOff.getText() != null
                ? binding.etSearchDropOff.getText().toString().trim().toLowerCase()
                : "";

        List<DropOffPointResponse> filtered = new ArrayList<>();
        for (DropOffPointResponse p : allPoints) {
            if (query.isEmpty()) {
                filtered.add(p);
                continue;
            }

            String label = p.getLabel() != null ? p.getLabel().toLowerCase() : "";
            String address = p.getAddress() != null ? p.getAddress().toLowerCase() : "";
            String facility = p.getFacilityName() != null ? p.getFacilityName().toLowerCase() : "";
            String types = p.getAcceptedTypes() != null ? p.getAcceptedTypes().toLowerCase() : "";

            if (label.contains(query) || address.contains(query) || facility.contains(query) || types.contains(query)) {
                filtered.add(p);
            }
        }

        adapter.submitList(filtered);

        if (filtered.isEmpty()) {
            binding.layoutEmptyState.setVisibility(View.VISIBLE);
            binding.rvDropOffPoints.setVisibility(View.GONE);
        } else {
            binding.layoutEmptyState.setVisibility(View.GONE);
            binding.rvDropOffPoints.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onEdit(DropOffPointResponse dropOff) {
        AddEditDropOffBottomSheet sheet = AddEditDropOffBottomSheet.newInstance(dropOff, this::loadDropOffPoints);
        sheet.show(getChildFragmentManager(), "AddEditDropOffBottomSheet");
    }

    @Override
    public void onDelete(DropOffPointResponse dropOff) {
        if (dropOff == null || dropOff.getId() == null) return;

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Drop-Off Point")
                .setMessage("Are you sure you want to remove '" + dropOff.getLabel() + "'? This kiosk will no longer appear on public consumer maps.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteDropOffPoint(dropOff.getId()).observe(getViewLifecycleOwner(), resource -> {
                        if (resource == null) return;
                        if (resource.status == Resource.Status.SUCCESS) {
                            Snackbar.make(binding.getRoot(), "Drop-off point removed", Snackbar.LENGTH_SHORT).show();
                            loadDropOffPoints();
                        } else if (resource.status == Resource.Status.ERROR) {
                            Snackbar.make(binding.getRoot(), "Failed to delete drop-off point", Snackbar.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
