package com.ewaste.app.ui.consumer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import com.ewaste.app.EWasteApplication;
import com.ewaste.app.R;
import com.ewaste.app.data.local.SessionManager;
import com.ewaste.app.databinding.DialogQuickActionMenuBinding;
import com.ewaste.app.ui.auth.LoginActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class QuickActionMenuSheet extends BottomSheetDialogFragment {

    public interface OnFilterSelectedListener {
        void onFilterChanged(boolean openNowOnly);
    }

    private DialogQuickActionMenuBinding binding;
    private OnFilterSelectedListener filterListener;
    private boolean currentOpenNowOnly = false;

    public static QuickActionMenuSheet newInstance() {
        return newInstance(false, null);
    }

    public static QuickActionMenuSheet newInstance(boolean openNowOnly, OnFilterSelectedListener filterListener) {
        QuickActionMenuSheet fragment = new QuickActionMenuSheet();
        fragment.currentOpenNowOnly = openNowOnly;
        fragment.filterListener = filterListener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogQuickActionMenuBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SessionManager session = EWasteApplication.getInstance().getSessionManager();
        if (session != null && session.isLoggedIn()) {
            binding.cardProfileSummary.setVisibility(View.VISIBLE);
            binding.btnMenuLogout.setVisibility(View.VISIBLE);

            String name = session.getFullName();
            String role = session.getRole();
            binding.tvMenuUserName.setText(name != null && !name.trim().isEmpty() ? name : "EcoTrack User");

            if ("FACILITY".equalsIgnoreCase(role)) {
                binding.tvMenuUserRole.setText("Facility Operations Staff");
                binding.tvMenuCredits.setVisibility(View.GONE);
            } else if ("BUSINESS".equalsIgnoreCase(role)) {
                binding.tvMenuUserRole.setText("Certified Enterprise Account");
                binding.tvMenuCredits.setVisibility(View.GONE);
            } else if ("ADMIN".equalsIgnoreCase(role)) {
                binding.tvMenuUserRole.setText("System Administrator");
                binding.tvMenuCredits.setVisibility(View.GONE);
            } else {
                binding.tvMenuUserRole.setText("Certified Consumer Account");
                binding.tvMenuCredits.setVisibility(View.VISIBLE);
                binding.tvMenuCredits.setText("100 pts");
            }
        } else {
            binding.cardProfileSummary.setVisibility(View.GONE);
            binding.btnMenuLogout.setVisibility(View.GONE);
        }

        // Theme Toggle Setup
        int currentNightMode = AppCompatDelegate.getDefaultNightMode();
        if (currentNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            binding.btnThemeDark.setChecked(true);
        } else if (currentNightMode == AppCompatDelegate.MODE_NIGHT_NO) {
            binding.btnThemeLight.setChecked(true);
        } else {
            binding.btnThemeSystem.setChecked(true);
        }

        binding.btnThemeLight.setOnClickListener(v -> {
            if (session != null) session.setThemeMode(AppCompatDelegate.MODE_NIGHT_NO);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            dismiss();
        });

        binding.btnThemeDark.setOnClickListener(v -> {
            if (session != null) session.setThemeMode(AppCompatDelegate.MODE_NIGHT_YES);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            dismiss();
        });

        binding.btnThemeSystem.setOnClickListener(v -> {
            if (session != null) session.setThemeMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            dismiss();
        });

        // Quick Filter Setup (Only show if filter listener provided)
        if (filterListener != null) {
            binding.tvFilterTitle.setVisibility(View.VISIBLE);
            binding.chipGroupQuickFilter.setVisibility(View.VISIBLE);

            if (currentOpenNowOnly) {
                binding.chipFilterOpenOnly.setChecked(true);
            } else {
                binding.chipFilterAll.setChecked(true);
            }

            binding.chipGroupQuickFilter.setOnCheckedStateChangeListener((group, checkedIds) -> {
                boolean openOnly = checkedIds.contains(R.id.chipFilterOpenOnly);
                if (filterListener != null) {
                    filterListener.onFilterChanged(openOnly);
                }
                dismiss();
            });
        } else {
            binding.tvFilterTitle.setVisibility(View.GONE);
            binding.chipGroupQuickFilter.setVisibility(View.GONE);
        }

        // Logout Setup
        binding.btnMenuLogout.setOnClickListener(v -> {
            dismiss();
            if (session != null) {
                session.clearSession();
            }
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
