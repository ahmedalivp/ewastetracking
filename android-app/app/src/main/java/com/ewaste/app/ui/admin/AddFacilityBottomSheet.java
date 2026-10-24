package com.ewaste.app.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import com.ewaste.app.data.model.CreateFacilityRequest;
import com.ewaste.app.databinding.DialogAddFacilityBinding;
import com.ewaste.app.ui.common.Resource;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;

public class AddFacilityBottomSheet extends BottomSheetDialogFragment {

    public interface OnFacilityCreatedListener {
        void onFacilityCreated();
    }

    private DialogAddFacilityBinding binding;
    private AdminViewModel viewModel;
    private OnFacilityCreatedListener listener;

    public static AddFacilityBottomSheet newInstance(OnFacilityCreatedListener listener) {
        AddFacilityBottomSheet sheet = new AddFacilityBottomSheet();
        sheet.listener = listener;
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAddFacilityBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);

        binding.btnCancel.setOnClickListener(v -> dismiss());

        binding.btnSaveFacility.setOnClickListener(v -> submitFacility());
    }

    private void submitFacility() {
        String name = binding.etFacilityName.getText() != null ? binding.etFacilityName.getText().toString().trim() : "";
        String address = binding.etFacilityAddress.getText() != null ? binding.etFacilityAddress.getText().toString().trim() : "";
        String latStr = binding.etLat.getText() != null ? binding.etLat.getText().toString().trim() : "";
        String lngStr = binding.etLng.getText() != null ? binding.etLng.getText().toString().trim() : "";
        String phone = binding.etContactPhone.getText() != null ? binding.etContactPhone.getText().toString().trim() : null;
        boolean verified = binding.cbAutoVerify.isChecked();

        if (name.isEmpty()) {
            binding.etFacilityName.setError("Center name required");
            return;
        }
        if (address.isEmpty()) {
            binding.etFacilityAddress.setError("Address required");
            return;
        }

        double lat, lng;
        try {
            lat = Double.parseDouble(latStr);
        } catch (Exception e) {
            binding.etLat.setError("Valid latitude required");
            return;
        }

        try {
            lng = Double.parseDouble(lngStr);
        } catch (Exception e) {
            binding.etLng.setError("Valid longitude required");
            return;
        }

        binding.btnSaveFacility.setEnabled(false);
        CreateFacilityRequest req = new CreateFacilityRequest(name, address, lat, lng, verified, phone);

        viewModel.createFacility(req).observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            if (resource.status == Resource.Status.SUCCESS) {
                if (listener != null) listener.onFacilityCreated();
                dismiss();
            } else if (resource.status == Resource.Status.ERROR) {
                binding.btnSaveFacility.setEnabled(true);
                String msg = resource.message != null ? resource.message : "Failed to create center";
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
