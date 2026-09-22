package com.ewaste.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.ewaste.app.data.model.ComponentRequestResponse;
import com.ewaste.app.databinding.ItemComponentRequestBinding;
import com.ewaste.app.ui.common.StatusPillHelper;

public class ComponentRequestsAdapter extends ListAdapter<ComponentRequestResponse, ComponentRequestsAdapter.ViewHolder> {

    public ComponentRequestsAdapter() {
        super(DIFF_CALLBACK);
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
        ItemComponentRequestBinding binding = ItemComponentRequestBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemComponentRequestBinding binding;

        public ViewHolder(ItemComponentRequestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ComponentRequestResponse item) {
            binding.tvComponentName.setText(item.getComponentName() != null ? item.getComponentName() : "Hardware Component");

            String type = item.getComponentType() != null ? item.getComponentType() : "Part";
            String cond = item.getComponentCondition() != null ? item.getComponentCondition() : "GOOD";
            binding.tvTypeAndCondition.setText(String.format("Type: %s • Condition: %s", type, cond));

            String cleanDate = "";
            if (item.getRequestedAt() != null) {
                cleanDate = item.getRequestedAt().replace("T", " ");
                if (cleanDate.length() > 16) cleanDate = cleanDate.substring(0, 16);
            }
            binding.tvRequestMeta.setText(String.format("Requisition #%d • %s", item.getId(), cleanDate));

            String status = item.getStatus() != null ? item.getStatus() : "PENDING";
            binding.tvStatus.setText(status);
            StatusPillHelper.applyStatus(binding.tvStatus, status);

            if (item.getBusinessName() != null) {
                binding.tvRequesterName.setText("Requested by: " + item.getBusinessName());
            } else {
                binding.tvRequesterName.setText("");
            }
        }
    }
}
