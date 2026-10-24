package com.ewaste.app.ui.consumer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.ewaste.app.R;
import com.ewaste.app.data.model.DropOffPointResponse;
import com.ewaste.app.databinding.DialogHubActionSheetBinding;
import com.ewaste.app.util.MapHelper;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class HubActionBottomSheet extends BottomSheetDialogFragment {

    public interface OnHubActionListener {
        void onDropOffSelected(DropOffPointResponse hub);
    }

    private DialogHubActionSheetBinding binding;
    private DropOffPointResponse hub;
    private OnHubActionListener listener;

    public static HubActionBottomSheet newInstance(DropOffPointResponse hub, OnHubActionListener listener) {
        HubActionBottomSheet fragment = new HubActionBottomSheet();
        fragment.hub = hub;
        fragment.listener = listener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogHubActionSheetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (hub == null) {
            dismiss();
            return;
        }

        binding.tvSheetHubName.setText(hub.getLabel() != null ? hub.getLabel() : "Drop-Off Hub");
        binding.tvSheetFacility.setText(hub.getFacilityName() != null ? "Managed by " + hub.getFacilityName() : "Certified Facility");
        binding.tvSheetAddress.setText(hub.getAddress() != null && !hub.getAddress().isEmpty() ? hub.getAddress() : "San Francisco, CA");
        binding.tvSheetHours.setText(hub.getOperatingHours() != null && !hub.getOperatingHours().isEmpty() ? hub.getOperatingHours() : "Open for drop-off");

        if (hub.getAcceptedTypes() != null && !hub.getAcceptedTypes().isEmpty()) {
            binding.tvSheetAccepted.setText("Accepts: " + hub.getAcceptedTypes());
            binding.tvSheetAccepted.setVisibility(View.VISIBLE);
        } else {
            binding.tvSheetAccepted.setVisibility(View.GONE);
        }

        if (hub.getImageUrl() != null && !hub.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(hub.getImageUrl())
                    .placeholder(R.drawable.img_hub_placeholder)
                    .error(R.drawable.img_hub_placeholder)
                    .into(binding.ivSheetHubPhoto);
        }

        binding.btnSheetDirections.setOnClickListener(v -> {
            if (hub.getLat() != null && hub.getLng() != null) {
                MapHelper.launchNavigation(requireContext(), hub.getLat(), hub.getLng(), hub.getLabel());
            }
            dismiss();
        });

        binding.btnSheetDropOff.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDropOffSelected(hub);
            }
            dismiss();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
