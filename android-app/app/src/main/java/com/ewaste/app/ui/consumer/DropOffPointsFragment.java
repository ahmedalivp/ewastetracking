package com.ewaste.app.ui.consumer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.ewaste.app.databinding.FragmentDropOffPointsBinding;
import com.ewaste.app.ui.adapter.DropOffPointsAdapter;
import com.ewaste.app.ui.common.Resource;
import com.google.android.material.snackbar.Snackbar;

public class DropOffPointsFragment extends Fragment {

    private FragmentDropOffPointsBinding binding;
    private ConsumerViewModel viewModel;
    private DropOffPointsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDropOffPointsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ConsumerViewModel.class);

        setupRecyclerView();
        setupSwipeRefresh();
        loadDropOffPoints();
    }

    private void setupRecyclerView() {
        adapter = new DropOffPointsAdapter(new DropOffPointsAdapter.OnDropOffClickListener() {
            @Override
            public void onMapClick(com.ewaste.app.data.model.DropOffPointResponse item) {
                showMapDialog(item);
            }

            @Override
            public void onNavigateClick(com.ewaste.app.data.model.DropOffPointResponse item) {
                if (item != null && item.getLat() != null && item.getLng() != null) {
                    com.ewaste.app.util.MapHelper.launchNavigation(requireContext(), item.getLat(), item.getLng(), item.getLabel());
                } else {
                    Snackbar.make(binding.getRoot(), "Coordinates not available for this site", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        binding.rvDropOffPoints.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvDropOffPoints.setAdapter(adapter);
    }

    private void showMapDialog(com.ewaste.app.data.model.DropOffPointResponse item) {
        if (item == null || getContext() == null) return;

        com.ewaste.app.databinding.DialogMapPreviewBinding mapBinding =
                com.ewaste.app.databinding.DialogMapPreviewBinding.inflate(LayoutInflater.from(requireContext()));

        mapBinding.tvMapTitle.setText(item.getLabel() != null ? item.getLabel() : "Drop-Off Hub");
        mapBinding.tvMapSubtitle.setText(item.getFacilityName() != null ? item.getFacilityName() : "Certified Facility");

        double lat = item.getLat() != null ? item.getLat() : 37.7749;
        double lng = item.getLng() != null ? item.getLng() : -122.4194;

        com.ewaste.app.util.MapHelper.renderMap(mapBinding.mapWebView, lat, lng, item.getLabel(), item.getFacilityName());

        androidx.appcompat.app.AlertDialog dialog = new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setView(mapBinding.getRoot())
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        mapBinding.btnCloseMap.setOnClickListener(v -> dialog.dismiss());
        mapBinding.btnOpenNavigation.setOnClickListener(v -> {
            com.ewaste.app.util.MapHelper.launchNavigation(requireContext(), lat, lng, item.getLabel());
            dialog.dismiss();
        });

        dialog.show();
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(this::loadDropOffPoints);
    }

    private void loadDropOffPoints() {
        viewModel.getDropOffPoints().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            binding.swipeRefresh.setRefreshing(false);

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
                String msg = resource.message != null ? resource.message : "Failed to load drop-off locations";
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
