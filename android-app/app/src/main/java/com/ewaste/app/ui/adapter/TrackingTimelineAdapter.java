package com.ewaste.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.ewaste.app.data.model.TrackingRecordResponse;
import com.ewaste.app.databinding.ItemTrackingRecordBinding;
import com.ewaste.app.ui.common.StatusPillHelper;

public class TrackingTimelineAdapter extends ListAdapter<TrackingRecordResponse, TrackingTimelineAdapter.ViewHolder> {

    public TrackingTimelineAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<TrackingRecordResponse> DIFF_CALLBACK = new DiffUtil.ItemCallback<TrackingRecordResponse>() {
        @Override
        public boolean areItemsTheSame(@NonNull TrackingRecordResponse oldItem, @NonNull TrackingRecordResponse newItem) {
            return oldItem.getId() != null && oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull TrackingRecordResponse oldItem, @NonNull TrackingRecordResponse newItem) {
            return (oldItem.getStatus() != null && oldItem.getStatus().equals(newItem.getStatus()))
                    && (oldItem.getTimestamp() != null && oldItem.getTimestamp().equals(newItem.getTimestamp()))
                    && (oldItem.getNotes() != null && oldItem.getNotes().equals(newItem.getNotes()));
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTrackingRecordBinding binding = ItemTrackingRecordBinding.inflate(
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
        private final ItemTrackingRecordBinding binding;

        public ViewHolder(ItemTrackingRecordBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(TrackingRecordResponse item) {
            String status = item.getStatus() != null ? item.getStatus() : "EVENT";
            binding.tvTimelineStatus.setText(status);
            StatusPillHelper.applyStatus(binding.tvTimelineStatus, status);

            if (item.getTimestamp() != null) {
                String cleanDate = item.getTimestamp().replace("T", " ");
                if (cleanDate.length() > 16) cleanDate = cleanDate.substring(0, 16);
                binding.tvTimelineTimestamp.setText(cleanDate);
            } else {
                binding.tvTimelineTimestamp.setText("");
            }

            String actorName = item.getActorName() != null ? item.getActorName() : (item.getActorEmail() != null ? item.getActorEmail() : "System");
            binding.tvTimelineActor.setText("By: " + actorName);

            String notes = item.getNotes() != null ? item.getNotes() : "Status updated in immutable audit ledger.";
            binding.tvTimelineNotes.setText(notes);
        }
    }
}
