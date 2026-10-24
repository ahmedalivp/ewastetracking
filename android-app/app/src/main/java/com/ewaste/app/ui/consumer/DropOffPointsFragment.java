package com.ewaste.app.ui.consumer;

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
import androidx.recyclerview.widget.GridLayoutManager;
import com.ewaste.app.data.model.DropOffPointResponse;
import com.ewaste.app.databinding.FragmentDropOffPointsBinding;
import com.ewaste.app.ui.adapter.DropOffPointsAdapter;
import com.ewaste.app.ui.common.Resource;
import com.ewaste.app.util.MapHelper;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class DropOffPointsFragment extends Fragment {

    private FragmentDropOffPointsBinding binding;
    private ConsumerViewModel viewModel;
    private DropOffPointsAdapter adapter;

    private final List<DropOffPointResponse> fullPointList = new ArrayList<>();
    private String currentSearchQuery = "";
    private boolean filterOpenNowOnly = false;

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
        setupSearchAndTriangleMenu();
        setupMapControls();
        setupSwipeRefresh();
        loadDropOffPoints();
    }

    private void setupRecyclerView() {
        adapter = new DropOffPointsAdapter(this::showHubActionSheet);

        // 2-column grid layout as requested in wireframe
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        binding.rvDropOffPoints.setLayoutManager(gridLayoutManager);
        binding.rvDropOffPoints.setAdapter(adapter);
    }

    private void setupSearchAndTriangleMenu() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s != null ? s.toString().trim().toLowerCase() : "";
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Unconventional Inverted Triangle Quick Action Button (▼)
        binding.btnTriangleMenu.setOnClickListener(v -> {
            QuickActionMenuSheet menuSheet = QuickActionMenuSheet.newInstance(filterOpenNowOnly, openNow -> {
                filterOpenNowOnly = openNow;
                applyFilters();
            });
            menuSheet.show(getParentFragmentManager(), "QuickActionMenuSheet");
        });
    }

    private void setupMapControls() {
        binding.btnFitAll.setOnClickListener(v -> {
            MapHelper.fitAllMarkers(binding.mapWebView);
            adapter.setSelectedPointId(null);
        });
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
                    fullPointList.clear();
                    fullPointList.addAll(resource.data);

                    // Render Map with all drop-off points (adaptive dark/light styling)
                    MapHelper.renderMultiPointMap(binding.mapWebView, fullPointList, id -> {
                        requireActivity().runOnUiThread(() -> {
                            int position = adapter.getPositionForId(id);
                            if (position >= 0) {
                                adapter.setSelectedPointId(id);
                                binding.rvDropOffPoints.smoothScrollToPosition(position);
                                DropOffPointResponse pt = findPointById(id);
                                if (pt != null) {
                                    showHubActionSheet(pt);
                                }
                            }
                        });
                    });

                    applyFilters();
                }
            } else if (resource.status == Resource.Status.ERROR) {
                binding.progressBar.setVisibility(View.GONE);
                String msg = resource.message != null ? resource.message : "Failed to load drop-off locations";
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void showHubActionSheet(DropOffPointResponse item) {
        if (item == null) return;

        adapter.setSelectedPointId(item.getId());
        if (item.getLat() != null && item.getLng() != null) {
            MapHelper.centerMapOn(binding.mapWebView, item.getLat(), item.getLng(), 15, item.getId() != null ? item.getId() : 0);
        }

        HubActionBottomSheet sheet = HubActionBottomSheet.newInstance(item, hub -> {
            if (getActivity() instanceof ConsumerMainActivity) {
                ((ConsumerMainActivity) getActivity()).navigateToSubmitWithHub(hub);
            }
        });
        sheet.show(getParentFragmentManager(), "HubActionBottomSheet");
    }

    private DropOffPointResponse findPointById(long id) {
        for (DropOffPointResponse pt : fullPointList) {
            if (pt.getId() != null && pt.getId() == id) {
                return pt;
            }
        }
        return null;
    }

    private void applyFilters() {
        List<DropOffPointResponse> filtered = new ArrayList<>();

        for (DropOffPointResponse pt : fullPointList) {
            boolean matchesSearch = true;
            if (!currentSearchQuery.isEmpty()) {
                String label = pt.getLabel() != null ? pt.getLabel().toLowerCase() : "";
                String facility = pt.getFacilityName() != null ? pt.getFacilityName().toLowerCase() : "";
                String address = pt.getAddress() != null ? pt.getAddress().toLowerCase() : "";
                String types = pt.getAcceptedTypes() != null ? pt.getAcceptedTypes().toLowerCase() : "";

                matchesSearch = label.contains(currentSearchQuery)
                        || facility.contains(currentSearchQuery)
                        || address.contains(currentSearchQuery)
                        || types.contains(currentSearchQuery);
            }

            if (!matchesSearch) continue;

            if (filterOpenNowOnly) {
                String hours = pt.getOperatingHours() != null ? pt.getOperatingHours().toLowerCase() : "";
                boolean isOpen = hours.contains("24/7") || hours.contains("open") || hours.contains("mon");
                if (!isOpen) continue;
            }

            filtered.add(pt);
        }

        adapter.submitList(filtered);

        if (filtered.isEmpty() && !fullPointList.isEmpty()) {
            binding.tvEmptyState.setVisibility(View.VISIBLE);
        } else {
            binding.tvEmptyState.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
