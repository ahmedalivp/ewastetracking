package com.ewaste.app.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import com.ewaste.app.data.model.FacilityResponse;
import com.ewaste.app.data.model.UpdateStaffAccessRequest;
import com.ewaste.app.databinding.DialogReassignStaffBinding;
import com.ewaste.app.ui.common.Resource;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class ReassignStaffBottomSheet extends BottomSheetDialogFragment {

    private DialogReassignStaffBinding binding;
    private AdminViewModel viewModel;
    private Long staffId;
    private String staffName;
    private Long currentFacilityId;
    private final List<FacilityResponse> facilities = new ArrayList<>();
    private Long selectedFacilityId = null;
    private Runnable onReassignedListener;

    public static ReassignStaffBottomSheet newInstance(Long staffId, String staffName, Long currentFacilityId) {
        ReassignStaffBottomSheet sheet = new ReassignStaffBottomSheet();
        Bundle args = new Bundle();
        if (staffId != null) args.putLong("staff_id", staffId);
        args.putString("staff_name", staffName);
        if (currentFacilityId != null) args.putLong("facility_id", currentFacilityId);
        sheet.setArguments(args);
        return sheet;
    }

    public void setOnReassignedListener(Runnable listener) {
        this.onReassignedListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogReassignStaffBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);

        if (getArguments() != null) {
            staffId = getArguments().getLong("staff_id");
            staffName = getArguments().getString("staff_name", "Operator");
            if (getArguments().containsKey("facility_id")) {
                currentFacilityId = getArguments().getLong("facility_id");
            }
        }

        binding.tvStaffSubheader.setText("Assign " + staffName + " to a processing facility");

        loadFacilities();

        binding.btnCancelReassign.setOnClickListener(v -> dismiss());
        binding.btnConfirmReassign.setOnClickListener(v -> submitReassignment());
    }

    private void loadFacilities() {
        viewModel.getFacilities().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
                facilities.clear();
                facilities.addAll(resource.data);

                List<String> names = new ArrayList<>();
                int preselectIndex = -1;
                for (int i = 0; i < facilities.size(); i++) {
                    FacilityResponse f = facilities.get(i);
                    names.add(f.getName());
                    if (currentFacilityId != null && currentFacilityId.equals(f.getId())) {
                        preselectIndex = i;
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, names);
                binding.actvFacilitySelect.setAdapter(adapter);

                if (preselectIndex >= 0) {
                    binding.actvFacilitySelect.setText(names.get(preselectIndex), false);
                    selectedFacilityId = facilities.get(preselectIndex).getId();
                }

                binding.actvFacilitySelect.setOnItemClickListener((parent, v, position, id) -> {
                    selectedFacilityId = facilities.get(position).getId();
                });
            }
        });
    }

    private void submitReassignment() {
        if (selectedFacilityId == null) {
            binding.tilFacilitySelect.setError("Please choose a target facility");
            return;
        }

        binding.btnConfirmReassign.setEnabled(false);
        UpdateStaffAccessRequest req = new UpdateStaffAccessRequest(selectedFacilityId, null);

        viewModel.updateStaffAccess(staffId, req).observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;
            if (resource.status == Resource.Status.SUCCESS) {
                if (onReassignedListener != null) onReassignedListener.run();
                dismiss();
            } else if (resource.status == Resource.Status.ERROR) {
                binding.btnConfirmReassign.setEnabled(true);
                Snackbar.make(binding.getRoot(), "Failed to reassign facility", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
