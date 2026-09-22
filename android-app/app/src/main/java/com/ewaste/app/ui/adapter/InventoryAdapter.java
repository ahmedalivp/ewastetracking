package com.ewaste.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.ewaste.app.data.model.ComponentResponse;
import com.ewaste.app.databinding.ItemInventoryComponentBinding;
import com.ewaste.app.ui.common.StatusPillHelper;

public class InventoryAdapter extends ListAdapter<ComponentResponse, InventoryAdapter.ViewHolder> {

    public InventoryAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<ComponentResponse> DIFF_CALLBACK = new DiffUtil.ItemCallback<ComponentResponse>() {
        @Override
        public boolean areItemsTheSame(@NonNull ComponentResponse oldItem, @NonNull ComponentResponse newItem) {
            return oldItem.getId() != null && oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ComponentResponse oldItem, @NonNull ComponentResponse newItem) {
            return (oldItem.getName() != null && oldItem.getName().equals(newItem.getName()))
                    && (oldItem.getStatus() != null && oldItem.getStatus().equals(newItem.getStatus()))
                    && (oldItem.getCondition() != null && oldItem.getCondition().equals(newItem.getCondition()));
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemInventoryComponentBinding binding = ItemInventoryComponentBinding.inflate(
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
        private final ItemInventoryComponentBinding binding;

        public ViewHolder(ItemInventoryComponentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ComponentResponse item) {
            binding.tvComponentName.setText(item.getName() != null ? item.getName() : "Hardware Component");
            binding.tvType.setText(item.getType() != null ? "Type: " + item.getType() : "Component");
            binding.tvOrigin.setText(String.format("Salvaged from Item #%d (%s)", item.getEwasteItemId(), item.getCategoryName() != null ? item.getCategoryName() : "E-Waste"));

            String condition = item.getCondition() != null ? item.getCondition() : "FAIR";
            binding.tvCondition.setText(condition);
            StatusPillHelper.applyCondition(binding.tvCondition, condition);

            String status = item.getStatus() != null ? item.getStatus() : "AVAILABLE";
            binding.tvStatus.setText(status);
            StatusPillHelper.applyStatus(binding.tvStatus, status);

            if (item.getHarvestedAt() != null) {
                String cleanDate = item.getHarvestedAt().replace("T", " ");
                if (cleanDate.length() > 16) cleanDate = cleanDate.substring(0, 16);
                binding.tvDate.setText("Harvested: " + cleanDate);
            } else {
                binding.tvDate.setText("");
            }
        }
    }
}
