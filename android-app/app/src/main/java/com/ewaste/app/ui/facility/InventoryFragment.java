package com.ewaste.app.ui.facility;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.ewaste.app.databinding.FragmentInventoryBinding;
import com.ewaste.app.ui.adapter.InventoryAdapter;
import com.ewaste.app.ui.common.Resource;
import com.google.android.material.snackbar.Snackbar;

public class InventoryFragment extends Fragment {

    private FragmentInventoryBinding binding;
    private FacilityViewModel viewModel;
    private InventoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentInventoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(FacilityViewModel.class);

        setupRecyclerView();
        setupSwipeRefresh();
        loadInventory();
    }

    private void setupRecyclerView() {
        adapter = new InventoryAdapter();
        binding.rvInventory.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvInventory.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(this::loadInventory);
    }

    private void loadInventory() {
        viewModel.getInventoryComponents().observe(getViewLifecycleOwner(), resource -> {
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
                    binding.rvInventory.setVisibility(View.VISIBLE);
                    adapter.submitList(resource.data);
                } else {
                    binding.layoutEmpty.setVisibility(View.VISIBLE);
                    binding.rvInventory.setVisibility(View.GONE);
                }
            } else if (resource.status == Resource.Status.ERROR) {
                binding.progressBar.setVisibility(View.GONE);
                String msg = resource.message != null ? resource.message : "Failed to load inventory";
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
