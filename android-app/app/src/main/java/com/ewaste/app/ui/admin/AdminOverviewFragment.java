package com.ewaste.app.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.ewaste.app.R;
import com.ewaste.app.data.model.FacilityResponse;
import com.ewaste.app.databinding.FragmentAdminOverviewBinding;
import com.ewaste.app.ui.common.Resource;
import com.ewaste.app.ui.consumer.QuickActionMenuSheet;

import java.util.List;

public class AdminOverviewFragment extends Fragment {

    private FragmentAdminOverviewBinding binding;
    private AdminViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminOverviewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);

        binding.btnTriangleMenu.setOnClickListener(v -> {
            QuickActionMenuSheet.newInstance()
                    .show(getParentFragmentManager(), "QuickActionMenuSheet");
        });

        binding.swipeRefresh.setOnRefreshListener(this::loadData);

        binding.cardPendingAlert.setOnClickListener(v -> {
            if (getActivity() instanceof AdminMainActivity) {
                ((AdminMainActivity) getActivity()).navigateToPendingCenters();
            }
        });

        loadData();
    }

    private void loadData() {
        loadImpactStats();
        loadNetworkCounts();
    }

    private void loadImpactStats() {
        viewModel.getImpactStats().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;
            binding.swipeRefresh.setRefreshing(false);

            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                binding.tvDivertedKg.setText(String.format("%.1f", resource.data.getDivertedLandfillKg()));
                binding.tvCo2Saved.setText(String.format("🌱 Estimated %.1f kg CO₂ emissions prevented", resource.data.getCo2EmissionsSavedKg()));
                binding.tvStatSubmissions.setText(String.valueOf(resource.data.getTotalSubmissions()));
                binding.tvStatHarvested.setText(String.valueOf(resource.data.getTotalComponentsHarvested()));
                binding.tvStatToxins.setText(String.valueOf(resource.data.getTotalHazardousDiverted()));
                binding.tvStatCredits.setText(String.valueOf(resource.data.getTotalCreditsAwarded()));
            }
        });
    }

    private void loadNetworkCounts() {
        viewModel.getFacilities().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
                List<FacilityResponse> facilities = resource.data;
                int activeCount = 0;
                int pendingCount = 0;
                for (FacilityResponse f : facilities) {
                    if (f.isVerifiedByAdmin()) {
                        activeCount++;
                    } else {
                        pendingCount++;
                    }
                }
                binding.tvActiveFacilitiesCount.setText(String.valueOf(activeCount));

                if (pendingCount > 0) {
                    binding.cardPendingAlert.setVisibility(View.VISIBLE);
                    binding.tvPendingCountTitle.setText(pendingCount + (pendingCount == 1 ? " Center Awaiting Verification" : " Centers Awaiting Verification"));
                } else {
                    binding.cardPendingAlert.setVisibility(View.GONE);
                }
            }
        });

        viewModel.getDropOffPoints().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
                binding.tvActiveDropOffsCount.setText(String.valueOf(resource.data.size()));
            }
        });

        viewModel.getAdminStaff(null).observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
                binding.tvActiveStaffCount.setText(String.valueOf(resource.data.size()));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
