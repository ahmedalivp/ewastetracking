package com.ewaste.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.ewaste.app.data.model.EWasteItemResponse;
import com.ewaste.app.databinding.ItemSubmissionBinding;
import com.ewaste.app.ui.common.StatusPillHelper;

public class SubmissionsAdapter extends ListAdapter<EWasteItemResponse, SubmissionsAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(EWasteItemResponse item);
        void onQrClick(EWasteItemResponse item);
    }

    private final OnItemClickListener listener;

    public SubmissionsAdapter(OnItemClickListener listener) {
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
        ItemSubmissionBinding binding = ItemSubmissionBinding.inflate(
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
        private final ItemSubmissionBinding binding;

        public ViewHolder(ItemSubmissionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(EWasteItemResponse item, OnItemClickListener listener) {
            binding.tvCategory.setText(item.getCategoryName() != null ? item.getCategoryName() : "E-Waste");
            binding.tvDescription.setText(item.getDeviceDescription() != null ? item.getDeviceDescription() : "Electronic Device");
            binding.tvDropOffLocation.setText(item.getDropOffPointLabel() != null ? "📍 " + item.getDropOffPointLabel() : "📍 Drop-off Center");

            if (item.getSubmittedAt() != null) {
                String cleanDate = item.getSubmittedAt().replace("T", " ");
                if (cleanDate.length() > 16) cleanDate = cleanDate.substring(0, 16);
                binding.tvDate.setText(cleanDate);
            } else {
                binding.tvDate.setText("");
            }

            // Apply modern pastel status pill
            String status = item.getStatus() != null ? item.getStatus() : "SUBMITTED";
            binding.tvStatus.setText(status);
            StatusPillHelper.applyStatus(binding.tvStatus, status);

            // Stats
            binding.tvStats.setText(String.format("⚙️ %d parts • ☣️ %d toxic", item.getComponentCount(), item.getHazardousMaterialCount()));

            binding.btnQrPassport.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onQrClick(item);
                }
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
