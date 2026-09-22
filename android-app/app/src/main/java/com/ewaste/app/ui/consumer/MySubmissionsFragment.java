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
import com.ewaste.app.R;
import com.ewaste.app.data.model.EWasteItemResponse;
import com.ewaste.app.databinding.FragmentMySubmissionsBinding;
import com.ewaste.app.ui.adapter.SubmissionsAdapter;
import com.ewaste.app.ui.common.Resource;
import com.google.android.material.snackbar.Snackbar;

public class MySubmissionsFragment extends Fragment {

    private FragmentMySubmissionsBinding binding;
    private ConsumerViewModel viewModel;
    private SubmissionsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMySubmissionsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ConsumerViewModel.class);

        setupRecyclerView();
        setupSwipeRefresh();
        loadSubmissions();
    }

    private void setupRecyclerView() {
        adapter = new SubmissionsAdapter(new SubmissionsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(EWasteItemResponse item) {
                onItemClicked(item);
            }

            @Override
            public void onQrClick(EWasteItemResponse item) {
                showQrCodeDialog(item);
            }
        });
        binding.rvSubmissions.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvSubmissions.setAdapter(adapter);
    }

    private void showQrCodeDialog(EWasteItemResponse item) {
        if (item == null || getContext() == null) return;

        com.ewaste.app.databinding.DialogQrCodeBinding dialogBinding =
                com.ewaste.app.databinding.DialogQrCodeBinding.inflate(LayoutInflater.from(requireContext()));

        String trackingCode = com.ewaste.app.util.QrCodeHelper.formatPassportToken(item.getId());
        dialogBinding.tvTrackingCode.setText(trackingCode);
        dialogBinding.tvDeviceTitle.setText(item.getDeviceDescription() != null ? item.getDeviceDescription() : "E-Waste Item");
        dialogBinding.tvDropOffLocation.setText(item.getDropOffPointLabel() != null ? "📍 Drop-off: " + item.getDropOffPointLabel() : "📍 Drop-off Center");

        android.graphics.Bitmap qrBitmap = com.ewaste.app.util.QrCodeHelper.generateQrCode(trackingCode, 450);
        if (qrBitmap != null) {
            dialogBinding.ivQrCode.setImageBitmap(qrBitmap);
        }

        com.google.android.material.dialog.MaterialAlertDialogBuilder builder =
                new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                        .setView(dialogBinding.getRoot());

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialogBinding.btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void onItemClicked(EWasteItemResponse item) {
        if (getActivity() instanceof ConsumerMainActivity) {
            ((ConsumerMainActivity) getActivity()).showItemDetail(item);
        }
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(this::loadSubmissions);
    }

    private void loadSubmissions() {
        viewModel.getMySubmissions().observe(getViewLifecycleOwner(), resource -> {
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
                    binding.rvSubmissions.setVisibility(View.VISIBLE);
                    adapter.submitList(resource.data);
                } else {
                    binding.layoutEmpty.setVisibility(View.VISIBLE);
                    binding.rvSubmissions.setVisibility(View.GONE);
                }
            } else if (resource.status == Resource.Status.ERROR) {
                binding.progressBar.setVisibility(View.GONE);
                String msg = resource.message != null ? resource.message : "Failed to load submissions";
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
