package com.ewaste.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.ewaste.app.data.model.DropOffPointResponse;
import com.ewaste.app.databinding.ItemDropOffPointBinding;

public class DropOffPointsAdapter extends ListAdapter<DropOffPointResponse, DropOffPointsAdapter.ViewHolder> {

    public interface OnDropOffClickListener {
        void onMapClick(DropOffPointResponse item);
        void onNavigateClick(DropOffPointResponse item);
    }

    private final OnDropOffClickListener listener;

    public DropOffPointsAdapter(OnDropOffClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<DropOffPointResponse> DIFF_CALLBACK = new DiffUtil.ItemCallback<DropOffPointResponse>() {
        @Override
        public boolean areItemsTheSame(@NonNull DropOffPointResponse oldItem, @NonNull DropOffPointResponse newItem) {
            return oldItem.getId() != null && oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull DropOffPointResponse oldItem, @NonNull DropOffPointResponse newItem) {
            return (oldItem.getLabel() != null && oldItem.getLabel().equals(newItem.getLabel()))
                    && (oldItem.getFacilityName() != null && oldItem.getFacilityName().equals(newItem.getFacilityName()));
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDropOffPointBinding binding = ItemDropOffPointBinding.inflate(
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
        private final ItemDropOffPointBinding binding;

        public ViewHolder(ItemDropOffPointBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(DropOffPointResponse item, OnDropOffClickListener listener) {
            binding.tvPointLabel.setText(item.getLabel() != null ? item.getLabel() : "Collection Point");
            binding.tvFacilityName.setText(item.getFacilityName() != null ? "Facility: " + item.getFacilityName() : "Partner Recycling Center");

            if (item.getLat() != null && item.getLng() != null) {
                binding.tvCoordinates.setText(String.format("GPS: %.4f° N, %.4f° E", item.getLat(), item.getLng()));
            } else {
                binding.tvCoordinates.setText("GPS: Coordinates Available on Site");
            }

            binding.btnNavigate.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onNavigateClick(item);
                }
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMapClick(item);
                }
            });
        }
    }
}
