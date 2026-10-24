package com.ewaste.app.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import com.ewaste.app.data.model.CreateDropOffPointRequest;
import com.ewaste.app.data.model.DropOffPointResponse;
import com.ewaste.app.data.model.FacilityResponse;
import com.ewaste.app.data.model.UpdateDropOffPointRequest;
import com.ewaste.app.databinding.DialogAddEditDropoffBinding;
import com.ewaste.app.ui.common.Resource;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class AddEditDropOffBottomSheet extends BottomSheetDialogFragment {

    public interface OnDropOffUpdatedListener {
        void onDropOffUpdated();
    }

    private static final String ARG_DROPOFF = "arg_dropoff";

    private DialogAddEditDropoffBinding binding;
    private AdminViewModel viewModel;
    private OnDropOffUpdatedListener listener;
    private DropOffPointResponse existingPoint;
    private final List<FacilityResponse> facilityList = new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;

    public static AddEditDropOffBottomSheet newInstance(DropOffPointResponse point, OnDropOffUpdatedListener listener) {
        AddEditDropOffBottomSheet sheet = new AddEditDropOffBottomSheet();
        sheet.listener = listener;
        if (point != null) {
            Bundle args = new Bundle();
            args.putSerializable(ARG_DROPOFF, point);
            sheet.setArguments(args);
        }
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAddEditDropoffBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);

        if (getArguments() != null) {
            existingPoint = (DropOffPointResponse) getArguments().getSerializable(ARG_DROPOFF);
        }

        setupFacilitySpinner();

        if (existingPoint != null) {
            binding.tvSheetDropOffTitle.setText("Edit Drop-Off Location");
            binding.etDropOffLabel.setText(existingPoint.getLabel());
            binding.etDropOffAddress.setText(existingPoint.getAddress() != null ? existingPoint.getAddress() : "");
            binding.etDropOffLat.setText(String.valueOf(existingPoint.getLat() != null ? existingPoint.getLat() : 0.0));
            binding.etDropOffLng.setText(String.valueOf(existingPoint.getLng() != null ? existingPoint.getLng() : 0.0));
            binding.etDropOffHours.setText(existingPoint.getOperatingHours() != null ? existingPoint.getOperatingHours() : "Mon-Sat 8:00 AM - 6:00 PM");
            binding.etDropOffAcceptedTypes.setText(existingPoint.getAcceptedTypes() != null ? existingPoint.getAcceptedTypes() : "All Categories");

            binding.btnDeleteDropOff.setVisibility(View.VISIBLE);
            binding.btnDeleteDropOff.setOnClickListener(v -> confirmDelete());
        } else {
            binding.tvSheetDropOffTitle.setText("Add Drop-Off Location");
            binding.btnDeleteDropOff.setVisibility(View.GONE);
            binding.etDropOffHours.setText("24/7 Smart Automated Drop Box");
            binding.etDropOffAcceptedTypes.setText("Smartphones, Tablets, Batteries, Laptops");
        }

        binding.btnSaveDropOff.setOnClickListener(v -> submitDropOff());
    }

    private void setupFacilitySpinner() {
        spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, new ArrayList<>());
        binding.spinnerParentFacility.setAdapter(spinnerAdapter);

        viewModel.getFacilities().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
                facilityList.clear();
                facilityList.addAll(resource.data);

                List<String> names = new ArrayList<>();
                int selectedPos = 0;
                for (int i = 0; i < facilityList.size(); i++) {
                    FacilityResponse f = facilityList.get(i);
                    names.add(f.getName() + " (" + f.getAddress() + ")");
                    if (existingPoint != null && existingPoint.getFacilityId() != null && existingPoint.getFacilityId().equals(f.getId())) {
                        selectedPos = i;
                    }
                }
                spinnerAdapter.clear();
                spinnerAdapter.addAll(names);
                spinnerAdapter.notifyDataSetChanged();

                if (!facilityList.isEmpty()) {
                    binding.spinnerParentFacility.setSelection(selectedPos);
                }
            }
        });
    }

    private void submitDropOff() {
        if (facilityList.isEmpty()) {
            Snackbar.make(binding.getRoot(), "Please wait for facilities to load", Snackbar.LENGTH_SHORT).show();
            return;
        }

        int selectedIndex = binding.spinnerParentFacility.getSelectedItemPosition();
        if (selectedIndex < 0 || selectedIndex >= facilityList.size()) return;
        Long facilityId = facilityList.get(selectedIndex).getId();

        String label = binding.etDropOffLabel.getText() != null ? binding.etDropOffLabel.getText().toString().trim() : "";
        String address = binding.etDropOffAddress.getText() != null ? binding.etDropOffAddress.getText().toString().trim() : "";
        String latStr = binding.etDropOffLat.getText() != null ? binding.etDropOffLat.getText().toString().trim() : "";
        String lngStr = binding.etDropOffLng.getText() != null ? binding.etDropOffLng.getText().toString().trim() : "";
        String hours = binding.etDropOffHours.getText() != null ? binding.etDropOffHours.getText().toString().trim() : "24/7 Smart Automated Box";
        String accepted = binding.etDropOffAcceptedTypes.getText() != null ? binding.etDropOffAcceptedTypes.getText().toString().trim() : "All Categories";

        if (label.isEmpty()) {
            binding.etDropOffLabel.setError("Label is required");
            return;
        }

        double lat, lng;
        try {
            lat = Double.parseDouble(latStr);
            lng = Double.parseDouble(lngStr);
        } catch (Exception e) {
            binding.etDropOffLat.setError("Valid coords required");
            return;
        }

        binding.btnSaveDropOff.setEnabled(false);

        if (existingPoint != null) {
            UpdateDropOffPointRequest req = new UpdateDropOffPointRequest(facilityId, label, lat, lng, address, existingPoint.getImageUrl(), hours, existingPoint.getContactPhone(), accepted);
            viewModel.updateDropOffPoint(existingPoint.getId(), req).observe(getViewLifecycleOwner(), resource -> {
                if (resource == null) return;
                if (resource.status == Resource.Status.SUCCESS) {
                    if (listener != null) listener.onDropOffUpdated();
                    dismiss();
                } else if (resource.status == Resource.Status.ERROR) {
                    binding.btnSaveDropOff.setEnabled(true);
                    Snackbar.make(binding.getRoot(), resource.message != null ? resource.message : "Update failed", Snackbar.LENGTH_LONG).show();
                }
            });
        } else {
            CreateDropOffPointRequest req = new CreateDropOffPointRequest(facilityId, label, lat, lng, address, null, hours, null, accepted);
            viewModel.createDropOffPoint(req).observe(getViewLifecycleOwner(), resource -> {
                if (resource == null) return;
                if (resource.status == Resource.Status.SUCCESS) {
                    if (listener != null) listener.onDropOffUpdated();
                    dismiss();
                } else if (resource.status == Resource.Status.ERROR) {
                    binding.btnSaveDropOff.setEnabled(true);
                    Snackbar.make(binding.getRoot(), resource.message != null ? resource.message : "Creation failed", Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }

    private void confirmDelete() {
        if (existingPoint == null) return;
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Drop-Off Station")
                .setMessage("Are you sure you want to remove this public collection point from the consumer map?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteDropOffPoint(existingPoint.getId()).observe(getViewLifecycleOwner(), resource -> {
                        if (resource != null && resource.status == Resource.Status.SUCCESS) {
                            if (listener != null) listener.onDropOffUpdated();
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
