package com.ewaste.app.ui.facility;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.ewaste.app.data.model.ComponentRequestResponse;
import com.ewaste.app.databinding.ItemFacilityRequisitionBinding;
import com.ewaste.app.ui.common.StatusPillHelper;

public class FacilityRequisitionsAdapter extends ListAdapter<ComponentRequestResponse, FacilityRequisitionsAdapter.ViewHolder> {

    public interface OnActionListener {
        void onApprove(ComponentRequestResponse item);
        void onFulfill(ComponentRequestResponse item);
        void onItemClick(ComponentRequestResponse item);
    }

    private final OnActionListener actionListener;

    public FacilityRequisitionsAdapter(OnActionListener actionListener) {
        super(DIFF_CALLBACK);
        this.actionListener = actionListener;
    }

    private static final DiffUtil.ItemCallback<ComponentRequestResponse> DIFF_CALLBACK = new DiffUtil.ItemCallback<ComponentRequestResponse>() {
        @Override
        public boolean areItemsTheSame(@NonNull ComponentRequestResponse oldItem, @NonNull ComponentRequestResponse newItem) {
            return oldItem.getId() != null && oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ComponentRequestResponse oldItem, @NonNull ComponentRequestResponse newItem) {
            return (oldItem.getStatus() != null && oldItem.getStatus().equals(newItem.getStatus()))
                    && (oldItem.getRequestedAt() != null && oldItem.getRequestedAt().equals(newItem.getRequestedAt()));
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFacilityRequisitionBinding binding = ItemFacilityRequisitionBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ComponentRequestResponse item = getItem(position);
        holder.bind(item, actionListener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemFacilityRequisitionBinding binding;

        public ViewHolder(ItemFacilityRequisitionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ComponentRequestResponse item, OnActionListener listener) {
            long reqId = item.getId() != null ? item.getId() : 0;
            String cleanDate = "";
            if (item.getRequestedAt() != null) {
                cleanDate = item.getRequestedAt().replace("T", " ");
                if (cleanDate.length() > 16) cleanDate = cleanDate.substring(0, 16);
            }
            binding.tvRequestMeta.setText(String.format("Requisition #%02d • %s", reqId, cleanDate));

            String status = item.getStatus() != null ? item.getStatus() : "PENDING";
            binding.tvStatus.setText(status);
            StatusPillHelper.applyStatus(binding.tvStatus, status);

            binding.tvComponentName.setText(item.getComponentName() != null ? item.getComponentName() : "Hardware Component");

            String type = item.getComponentType() != null ? item.getComponentType() : "Part";
            String cond = item.getComponentCondition() != null ? item.getComponentCondition() : "GOOD";
            binding.tvTypeAndCondition.setText(String.format("Type: %s • Condition: %s", type, cond));

            String business = item.getBusinessName() != null ? item.getBusinessName() : "Circular Enterprise";
            binding.tvBusinessName.setText("🏢 Requisitioner: " + business);

            String donorDesc = item.getOriginalItemDescription();
            Long donorId = item.getOriginalItemId();
            if (donorDesc != null && !donorDesc.trim().isEmpty()) {
                binding.tvDonorDevice.setText(String.format("📦 Donor Passport #%02d • %s", donorId != null ? donorId : 1, donorDesc));
            } else {
                binding.tvDonorDevice.setText("📦 Donor: Municipal salvage intake donor");
            }

            // Status-driven action controls
            if ("PENDING".equalsIgnoreCase(status)) {
                binding.btnApprove.setVisibility(View.VISIBLE);
                binding.btnApprove.setOnClickListener(v -> {
                    if (listener != null) listener.onApprove(item);
                });
                binding.btnFulfill.setVisibility(View.GONE);
                binding.tvTransferCompleteBadge.setVisibility(View.GONE);
            } else if ("APPROVED".equalsIgnoreCase(status)) {
                binding.btnApprove.setVisibility(View.GONE);
                binding.btnFulfill.setVisibility(View.VISIBLE);
                binding.btnFulfill.setOnClickListener(v -> {
                    if (listener != null) listener.onFulfill(item);
                });
                binding.tvTransferCompleteBadge.setVisibility(View.GONE);
            } else if ("FULFILLED".equalsIgnoreCase(status)) {
                binding.btnApprove.setVisibility(View.GONE);
                binding.btnFulfill.setVisibility(View.GONE);
                binding.tvTransferCompleteBadge.setVisibility(View.VISIBLE);
            } else {
                binding.btnApprove.setVisibility(View.GONE);
                binding.btnFulfill.setVisibility(View.GONE);
                binding.tvTransferCompleteBadge.setVisibility(View.GONE);
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(item);
            });
        }
    }
}
