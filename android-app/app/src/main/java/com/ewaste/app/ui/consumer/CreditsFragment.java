package com.ewaste.app.ui.consumer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.ewaste.app.R;
import com.ewaste.app.data.model.CreditRedemptionResponse;
import com.ewaste.app.data.model.CreditResponse;
import com.ewaste.app.data.model.UserProfileResponse;
import com.ewaste.app.databinding.DialogRedeemCreditBinding;
import com.ewaste.app.databinding.FragmentCreditsBinding;
import com.ewaste.app.ui.adapter.CreditsAdapter;
import com.ewaste.app.ui.common.Resource;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

public class CreditsFragment extends Fragment {

    private FragmentCreditsBinding binding;
    private ConsumerViewModel viewModel;
    private CreditsAdapter adapter;

    private int currentBalance = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCreditsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ConsumerViewModel.class);

        setupRecyclerView();
        setupSwipeRefresh();
        setupListeners();
        loadData();
    }

    private void setupRecyclerView() {
        adapter = new CreditsAdapter();
        binding.rvCredits.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCredits.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(this::loadData);
    }

    private void setupListeners() {
        binding.btnRedeem.setOnClickListener(v -> showRedeemDialog());
    }

    private void loadData() {
        loadProfileAndBalance();
        loadCreditsHistory();
    }

    private void loadProfileAndBalance() {
        viewModel.getProfile().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
                UserProfileResponse profile = resource.data;
                if (profile.getCreditBalance() != null) {
                    currentBalance = profile.getCreditBalance();
                    binding.tvBalancePoints.setText(String.valueOf(currentBalance));
                }
                if (profile.getDashboardSummary() != null) {
                    binding.tvPolymorphicSummary.setText(profile.getDashboardSummary());
                }
            }
        });
    }

    private void loadCreditsHistory() {
        viewModel.getMyCredits().observe(getViewLifecycleOwner(), resource -> {
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
                    binding.rvCredits.setVisibility(View.VISIBLE);
                    adapter.submitList(resource.data);
                } else {
                    binding.layoutEmpty.setVisibility(View.VISIBLE);
                    binding.rvCredits.setVisibility(View.GONE);
                }
            } else if (resource.status == Resource.Status.ERROR) {
                binding.progressBar.setVisibility(View.GONE);
                String msg = resource.message != null ? resource.message : "Failed to load credits history";
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void showRedeemDialog() {
        if (getContext() == null) return;

        DialogRedeemCreditBinding dialogBinding = DialogRedeemCreditBinding.inflate(getLayoutInflater());
        dialogBinding.tvCurrentBalance.setText(String.format("Available Balance: %d pts", currentBalance));

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogBinding.getRoot())
                .setCancelable(true)
                .create();

        dialogBinding.btnCancelRedeem.setOnClickListener(v -> dialog.dismiss());

        dialogBinding.btnConfirmRedeem.setOnClickListener(v -> {
            String amountStr = dialogBinding.etRedeemAmount.getText() != null ? dialogBinding.etRedeemAmount.getText().toString().trim() : "";
            String purpose = dialogBinding.etRedeemFor.getText() != null ? dialogBinding.etRedeemFor.getText().toString().trim() : "";

            if (amountStr.isEmpty()) {
                dialogBinding.tilRedeemAmount.setError("Enter points amount");
                return;
            }

            int amount;
            try {
                amount = Integer.parseInt(amountStr);
            } catch (NumberFormatException e) {
                dialogBinding.tilRedeemAmount.setError("Invalid number");
                return;
            }

            if (amount <= 0) {
                dialogBinding.tilRedeemAmount.setError("Amount must be greater than 0");
                return;
            }

            if (amount > currentBalance) {
                dialogBinding.tilRedeemAmount.setError("Insufficient balance (" + currentBalance + " available)");
                return;
            }
            dialogBinding.tilRedeemAmount.setError(null);

            if (purpose.isEmpty()) {
                dialogBinding.tilRedeemFor.setError("Please specify reward or voucher");
                return;
            }
            dialogBinding.tilRedeemFor.setError(null);

            dialogBinding.btnConfirmRedeem.setEnabled(false);
            performRedeem(amount, purpose, dialog);
        });

        dialog.show();
    }

    private void performRedeem(int amount, String purpose, AlertDialog dialog) {
        viewModel.redeemCredits(amount, purpose).observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            if (resource.status == Resource.Status.SUCCESS) {
                dialog.dismiss();
                if (resource.data != null) {
                    currentBalance = resource.data.getRemainingBalance();
                    binding.tvBalancePoints.setText(String.valueOf(currentBalance));
                }
                Snackbar.make(binding.getRoot(), "🎉 Successfully redeemed " + amount + " credits for " + purpose + "!", Snackbar.LENGTH_LONG).show();
                loadData();
            } else if (resource.status == Resource.Status.ERROR) {
                dialog.dismiss();
                String msg = resource.message != null ? resource.message : "Redemption failed";
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
