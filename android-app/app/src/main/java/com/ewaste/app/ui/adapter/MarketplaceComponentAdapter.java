package com.ewaste.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.ewaste.app.data.model.ComponentResponse;
import com.ewaste.app.databinding.ItemMarketplaceComponentBinding;
import com.ewaste.app.ui.common.StatusPillHelper;

public class MarketplaceComponentAdapter extends ListAdapter<ComponentResponse, MarketplaceComponentAdapter.ViewHolder> {

    public interface OnRequestClickListener {
        void onRequestClick(ComponentResponse item);
    }

    private final OnRequestClickListener listener;

    public MarketplaceComponentAdapter(OnRequestClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
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
        ItemMarketplaceComponentBinding binding = ItemMarketplaceComponentBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemMarketplaceComponentBinding binding;

        public ViewHolder(ItemMarketplaceComponentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ComponentResponse item, OnRequestClickListener listener) {
            binding.tvComponentName.setText(item.getName() != null ? item.getName() : "Hardware Component");
            binding.tvType.setText(item.getType() != null ? "Type: " + item.getType() : "Component");
            binding.tvCategory.setText(item.getCategoryName() != null ? item.getCategoryName() : "Electronics");
            binding.tvOrigin.setText(String.format("Salvaged from E-Waste Item #%d", item.getEwasteItemId()));

            String condition = item.getCondition() != null ? item.getCondition() : "FAIR";
            binding.tvCondition.setText(condition);
            StatusPillHelper.applyCondition(binding.tvCondition, condition);

            binding.btnAcquirePart.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRequestClick(item);
                }
            });
        }
    }
}
