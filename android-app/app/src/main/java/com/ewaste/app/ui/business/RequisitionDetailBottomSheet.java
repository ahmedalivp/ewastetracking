package com.ewaste.app.ui.business;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.ewaste.app.data.model.ComponentRequestResponse;
import com.ewaste.app.databinding.DialogRequisitionDetailBinding;
import com.ewaste.app.ui.common.StatusPillHelper;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class RequisitionDetailBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_REQUISITION = "arg_requisition";
    private DialogRequisitionDetailBinding binding;
    private ComponentRequestResponse requisition;

    public static RequisitionDetailBottomSheet newInstance(ComponentRequestResponse requisition) {
        RequisitionDetailBottomSheet fragment = new RequisitionDetailBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable(ARG_REQUISITION, requisition);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            requisition = (ComponentRequestResponse) getArguments().getSerializable(ARG_REQUISITION);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogRequisitionDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (requisition == null) return;

        binding.tvSheetRequisitionId.setText(String.format("REQUISITION #%02d", requisition.getId() != null ? requisition.getId() : 0));
        binding.tvSheetComponentName.setText(requisition.getComponentName() != null ? requisition.getComponentName() : "Hardware Component");

        String status = requisition.getStatus() != null ? requisition.getStatus() : "PENDING";
        binding.tvSheetStatus.setText(status);
        StatusPillHelper.applyStatus(binding.tvSheetStatus, status);

        binding.tvSheetType.setText("TYPE: " + (requisition.getComponentType() != null ? requisition.getComponentType() : "Component"));
        String condition = requisition.getComponentCondition() != null ? requisition.getComponentCondition() : "GOOD";
        binding.tvSheetCondition.setText("CONDITION: " + condition);

        String dateStr = requisition.getRequestedAt() != null ? requisition.getRequestedAt().replace("T", " ") : "2026-10-23 18:42";
        if (dateStr.length() > 16) dateStr = dateStr.substring(0, 16);
        binding.tvSheetDate.setText("📅 Requisition Placed: " + dateStr);

        binding.tvSheetRequester.setText("🏢 Enterprise: " + (requisition.getBusinessName() != null ? requisition.getBusinessName() : "Circular Partner"));

        // Circular Provenance & Origin Passport
        Long originItemId = requisition.getOriginalItemId();
        if (originItemId != null) {
            binding.tvSheetPassportTag.setText(String.format("DONOR PASSPORT #%02d", originItemId));
        } else {
            binding.tvSheetPassportTag.setText("CERTIFIED PROVENANCE PASSPORT");
        }

        String donorDesc = requisition.getOriginalItemDescription();
        if (donorDesc != null && !donorDesc.trim().isEmpty()) {
            binding.tvSheetDonorDevice.setText(donorDesc);
        } else {
            binding.tvSheetDonorDevice.setText("Salvaged from verified municipal circular intake donor device");
        }

        String facility = requisition.getFacilityName();
        if (facility != null && !facility.trim().isEmpty()) {
            binding.tvSheetFacilityOrigin.setText("📍 Harvest Facility: " + facility);
        } else {
            binding.tvSheetFacilityOrigin.setText("📍 Harvest Facility: GreenTech Certified Bay");
        }

        // Circular Transfer Certificate
        if ("FULFILLED".equalsIgnoreCase(status)) {
            binding.cardTransferCertificate.setVisibility(View.VISIBLE);
            long reqId = requisition.getId() != null ? requisition.getId() : 1;
            binding.tvCertificateCode.setText(String.format("CERT-CIRC-2026-R%04d • VERIFIED ZERO LANDFILL", reqId));
        } else {
            binding.cardTransferCertificate.setVisibility(View.GONE);
        }

        binding.btnSheetClose.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
