package com.ewaste.app.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import com.ewaste.app.data.model.FacilityResponse;
import com.ewaste.app.data.model.UpdateFacilityRequest;
import com.ewaste.app.databinding.DialogEditFacilityBinding;
import com.ewaste.app.ui.common.Resource;
import com.ewaste.app.ui.common.StatusPillHelper;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

public class EditFacilityBottomSheet extends BottomSheetDialogFragment {

    public interface OnFacilityUpdatedListener {
        void onFacilityUpdated();
    }

    private static final String ARG_FACILITY_ID = "arg_facility_id";
    private static final String ARG_NAME = "arg_name";
    private static final String ARG_ADDRESS = "arg_address";
    private static final String ARG_LAT = "arg_lat";
    private static final String ARG_LNG = "arg_lng";
    private static final String ARG_VERIFIED = "arg_verified";
    private static final String ARG_ACTIVE = "arg_active";
    private static final String ARG_PHONE = "arg_phone";
    private static final String ARG_STAFF_COUNT = "arg_staff_count";
    private static final String ARG_DROPOFF_COUNT = "arg_dropoff_count";

    private DialogEditFacilityBinding binding;
    private AdminViewModel viewModel;
    private OnFacilityUpdatedListener listener;
    private Long facilityId;

    public static EditFacilityBottomSheet newInstance(FacilityResponse f, OnFacilityUpdatedListener listener) {
        EditFacilityBottomSheet sheet = new EditFacilityBottomSheet();
        sheet.listener = listener;
        Bundle args = new Bundle();
        args.putLong(ARG_FACILITY_ID, f.getId() != null ? f.getId() : 0);
        args.putString(ARG_NAME, f.getName());
        args.putString(ARG_ADDRESS, f.getAddress());
        args.putDouble(ARG_LAT, f.getLat() != null ? f.getLat() : 0.0);
        args.putDouble(ARG_LNG, f.getLng() != null ? f.getLng() : 0.0);
        args.putBoolean(ARG_VERIFIED, f.isVerifiedByAdmin());
        args.putBoolean(ARG_ACTIVE, f.isActive());
        args.putString(ARG_PHONE, f.getContactPhone());
        args.putInt(ARG_STAFF_COUNT, f.getStaffCount());
        args.putInt(ARG_DROPOFF_COUNT, f.getDropOffCount());
        sheet.setArguments(args);
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogEditFacilityBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);

        Bundle args = getArguments();
        if (args == null) return;

        facilityId = args.getLong(ARG_FACILITY_ID);
        String name = args.getString(ARG_NAME, "");
        String address = args.getString(ARG_ADDRESS, "");
        double lat = args.getDouble(ARG_LAT, 0.0);
        double lng = args.getDouble(ARG_LNG, 0.0);
        boolean verified = args.getBoolean(ARG_VERIFIED, false);
        boolean active = args.getBoolean(ARG_ACTIVE, true);
        String phone = args.getString(ARG_PHONE, "");
        int staffCount = args.getInt(ARG_STAFF_COUNT, 0);
        int dropOffCount = args.getInt(ARG_DROPOFF_COUNT, 0);

        binding.tvSheetCenterTitle.setText(name);
        binding.tvSheetCenterSubtitle.setText(String.format("Center #%02d • %s", facilityId, address));
        binding.tvStatusBadge.setText(verified ? "VERIFIED" : "PENDING REVIEW");
        StatusPillHelper.applyStatus(binding.tvStatusBadge, verified ? "PROCESSED" : "PENDING");

        binding.tvStaffCountPill.setText(String.format("👥 %d Staff %s", staffCount, staffCount == 1 ? "Member" : "Members"));
        binding.tvDropOffCountPill.setText(String.format("📍 %d Collection %s", dropOffCount, dropOffCount == 1 ? "Kiosk" : "Kiosks"));

        binding.etEditFacilityName.setText(name);
        binding.etEditFacilityAddress.setText(address);
        binding.etEditLat.setText(String.valueOf(lat));
        binding.etEditLng.setText(String.valueOf(lng));
        binding.etEditContactPhone.setText(phone != null ? phone : "");

        binding.switchVerified.setChecked(verified);
        binding.switchActive.setChecked(active);

        binding.btnUpdateFacility.setOnClickListener(v -> submitUpdate());
        binding.btnDeleteFacility.setOnClickListener(v -> confirmDelete());
    }

    private void submitUpdate() {
        String name = binding.etEditFacilityName.getText() != null ? binding.etEditFacilityName.getText().toString().trim() : "";
        String address = binding.etEditFacilityAddress.getText() != null ? binding.etEditFacilityAddress.getText().toString().trim() : "";
        String latStr = binding.etEditLat.getText() != null ? binding.etEditLat.getText().toString().trim() : "";
        String lngStr = binding.etEditLng.getText() != null ? binding.etEditLng.getText().toString().trim() : "";
        String phone = binding.etEditContactPhone.getText() != null ? binding.etEditContactPhone.getText().toString().trim() : null;
        boolean verified = binding.switchVerified.isChecked();
        boolean active = binding.switchActive.isChecked();

        if (name.isEmpty()) {
            binding.etEditFacilityName.setError("Name required");
            return;
        }
        if (address.isEmpty()) {
            binding.etEditFacilityAddress.setError("Address required");
            return;
        }

        double lat, lng;
        try {
            lat = Double.parseDouble(latStr);
            lng = Double.parseDouble(lngStr);
        } catch (Exception e) {
            binding.etEditLat.setError("Valid coords required");
            return;
        }

        binding.btnUpdateFacility.setEnabled(false);
        UpdateFacilityRequest req = new UpdateFacilityRequest(name, address, lat, lng, verified, active, phone);

        viewModel.updateFacility(facilityId, req).observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            if (resource.status == Resource.Status.SUCCESS) {
                if (listener != null) listener.onFacilityUpdated();
                dismiss();
            } else if (resource.status == Resource.Status.ERROR) {
                binding.btnUpdateFacility.setEnabled(true);
                String msg = resource.message != null ? resource.message : "Failed to update center";
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void confirmDelete() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Recycling Center")
                .setMessage("Are you sure you want to delete this facility and decommission its linked drop-off stations? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteFacility(facilityId).observe(getViewLifecycleOwner(), resource -> {
                        if (resource != null && resource.status == Resource.Status.SUCCESS) {
                            if (listener != null) listener.onFacilityUpdated();
                            dismiss();
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
