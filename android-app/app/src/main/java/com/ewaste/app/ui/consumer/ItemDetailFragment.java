package com.ewaste.app.ui.consumer;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.ewaste.app.data.model.EWasteItemResponse;
import com.ewaste.app.databinding.FragmentItemDetailBinding;
import com.ewaste.app.ui.adapter.TrackingTimelineAdapter;
import com.ewaste.app.ui.common.Resource;

public class ItemDetailFragment extends Fragment {

    private static final String ARG_ITEM = "arg_item";

    private FragmentItemDetailBinding binding;
    private ConsumerViewModel viewModel;
    private TrackingTimelineAdapter timelineAdapter;
    private EWasteItemResponse item;

    public static ItemDetailFragment newInstance(EWasteItemResponse item) {
        ItemDetailFragment fragment = new ItemDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentItemDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ConsumerViewModel.class);

        if (getArguments() != null) {
            item = (EWasteItemResponse) getArguments().getSerializable(ARG_ITEM);
        }

        if (item == null) return;

        setupViews();
        setupTimelineRecyclerView();
        loadTrackingTimeline();
    }

    private void setupViews() {
        binding.tvCategory.setText(item.getCategoryName() != null ? item.getCategoryName() : "E-Waste");
        binding.tvDescription.setText(item.getDeviceDescription() != null ? item.getDeviceDescription() : "Electronic Device");
        binding.tvDropOff.setText(item.getDropOffPointLabel() != null ? "📍 Drop-off: " + item.getDropOffPointLabel() : "📍 Drop-off Point");

        if (item.getSubmittedAt() != null) {
            String cleanDate = item.getSubmittedAt().replace("T", " ");
            if (cleanDate.length() > 16) cleanDate = cleanDate.substring(0, 16);
            binding.tvSubmittedAt.setText("Submitted: " + cleanDate);
        }

        String status = item.getStatus() != null ? item.getStatus() : "SUBMITTED";
        binding.tvStatus.setText(status);
        applyStatusColor(status);

        binding.tvComponentsCount.setText(String.format("⚙️ Harvested: %d", item.getComponentCount()));
        binding.tvHazardousCount.setText(String.format("☣️ Hazardous: %d", item.getHazardousMaterialCount()));

        binding.btnBack.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else if (getActivity() instanceof ConsumerMainActivity) {
                ((ConsumerMainActivity) getActivity()).navigateToSubmissions();
            }
        });
    }

    private void applyStatusColor(String status) {
        int color;
        switch (status) {
            case "RECEIVED":
                color = Color.parseColor("#7B1FA2");
                break;
            case "CATEGORIZED":
                color = Color.parseColor("#0288D1");
                break;
            case "PROCESSED":
                color = Color.parseColor("#F57C00");
                break;
            case "COMPLETED":
                color = Color.parseColor("#388E3C");
                break;
            case "SUBMITTED":
            default:
                color = Color.parseColor("#1976D2");
                break;
        }
        binding.tvStatus.setBackgroundColor(color);
    }

    private void setupTimelineRecyclerView() {
        timelineAdapter = new TrackingTimelineAdapter();
        binding.rvTimeline.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvTimeline.setAdapter(timelineAdapter);
    }

    private void loadTrackingTimeline() {
        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.getItemTracking(item.getId()).observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            binding.progressBar.setVisibility(View.GONE);
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                if (resource.data.isEmpty()) {
                    binding.tvEmptyTimeline.setVisibility(View.VISIBLE);
                } else {
                    binding.tvEmptyTimeline.setVisibility(View.GONE);
                    timelineAdapter.submitList(resource.data);
                }
            } else if (resource.status == Resource.Status.ERROR) {
                binding.tvEmptyTimeline.setText("Error loading tracking history.");
                binding.tvEmptyTimeline.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
