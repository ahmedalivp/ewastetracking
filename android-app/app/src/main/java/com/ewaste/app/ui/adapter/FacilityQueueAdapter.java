package com.ewaste.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.ewaste.app.data.model.EWasteItemResponse;
import com.ewaste.app.databinding.ItemIntakeQueueBinding;
import com.ewaste.app.ui.common.StatusPillHelper;

public class FacilityQueueAdapter extends ListAdapter<EWasteItemResponse, FacilityQueueAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(EWasteItemResponse item);
    }

    private final OnItemClickListener listener;

    public FacilityQueueAdapter(OnItemClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<EWasteItemResponse> DIFF_CALLBACK = new DiffUtil.ItemCallback<EWasteItemResponse>() {
        @Override
        public boolean areItemsTheSame(@NonNull EWasteItemResponse oldItem, @NonNull EWasteItemResponse newItem) {
            return oldItem.getId() != null && oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull EWasteItemResponse oldItem, @NonNull EWasteItemResponse newItem) {
            return (oldItem.getStatus() != null && oldItem.getStatus().equals(newItem.getStatus()))
                    && (oldItem.getComponentCount() == newItem.getComponentCount())
                    && (oldItem.getHazardousMaterialCount() == newItem.getHazardousMaterialCount());
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemIntakeQueueBinding binding = ItemIntakeQueueBinding.inflate(
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
        private final ItemIntakeQueueBinding binding;

        public ViewHolder(ItemIntakeQueueBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(EWasteItemResponse item, OnItemClickListener listener) {
            binding.tvCategory.setText(item.getCategoryName() != null ? item.getCategoryName() : "E-Waste");
            binding.tvSubmitter.setText(item.getConsumerName() != null ? "by " + item.getConsumerName() : "by Consumer");
            binding.tvDescription.setText(item.getDeviceDescription() != null ? item.getDeviceDescription() : "Electronic Device");
            binding.tvLocation.setText(item.getDropOffPointLabel() != null ? "📍 " + item.getDropOffPointLabel() : "📍 Drop-off Center");

            if (item.getSubmittedAt() != null) {
                String cleanDate = item.getSubmittedAt().replace("T", " ");
                if (cleanDate.length() > 16) cleanDate = cleanDate.substring(0, 16);
                binding.tvSubmittedDate.setText(cleanDate);
            } else {
                binding.tvSubmittedDate.setText("");
            }

            String status = item.getStatus() != null ? item.getStatus() : "SUBMITTED";
            binding.tvStatus.setText(status);
            StatusPillHelper.applyStatus(binding.tvStatus, status);

            binding.tvStats.setText(String.format("⚙️ %d parts • ☣️ %d toxic", item.getComponentCount(), item.getHazardousMaterialCount()));

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
