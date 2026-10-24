package com.ewaste.app.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.ewaste.app.R;
import com.ewaste.app.data.model.DropOffPointResponse;
import com.ewaste.app.databinding.ItemDropOffGridCardBinding;

public class DropOffPointsAdapter extends ListAdapter<DropOffPointResponse, DropOffPointsAdapter.ViewHolder> {

    public interface OnDropOffCardClickListener {
        void onCardClick(DropOffPointResponse item);
    }

    private final OnDropOffCardClickListener listener;
    private Long selectedPointId = null;

    public DropOffPointsAdapter(OnDropOffCardClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    public void setSelectedPointId(Long pointId) {
        this.selectedPointId = pointId;
        notifyDataSetChanged();
    }

    public int getPositionForId(long id) {
        for (int i = 0; i < getItemCount(); i++) {
            DropOffPointResponse item = getItem(i);
            if (item != null && item.getId() != null && item.getId() == id) {
                return i;
            }
        }
        return -1;
    }

    private static final DiffUtil.ItemCallback<DropOffPointResponse> DIFF_CALLBACK = new DiffUtil.ItemCallback<DropOffPointResponse>() {
        @Override
        public boolean areItemsTheSame(@NonNull DropOffPointResponse oldItem, @NonNull DropOffPointResponse newItem) {
            return oldItem.getId() != null && oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull DropOffPointResponse oldItem, @NonNull DropOffPointResponse newItem) {
            return (oldItem.getLabel() != null && oldItem.getLabel().equals(newItem.getLabel()))
                    && (oldItem.getFacilityName() != null && oldItem.getFacilityName().equals(newItem.getFacilityName()))
                    && (oldItem.getAddress() != null && oldItem.getAddress().equals(newItem.getAddress()))
                    && (oldItem.getImageUrl() != null && oldItem.getImageUrl().equals(newItem.getImageUrl()));
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDropOffGridCardBinding binding = ItemDropOffGridCardBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DropOffPointResponse item = getItem(position);
        boolean isSelected = item != null && item.getId() != null && item.getId().equals(selectedPointId);
        holder.bind(item, listener, isSelected);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemDropOffGridCardBinding binding;

        public ViewHolder(ItemDropOffGridCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(DropOffPointResponse item, OnDropOffCardClickListener listener, boolean isSelected) {
            if (item == null) return;

            binding.tvHubName.setText(item.getLabel() != null ? item.getLabel() : "Drop-Off Hub");
            binding.tvFacilityName.setText(item.getFacilityName() != null ? item.getFacilityName() : "Recycling Partner");

            // Pseudo calculated distance indicator
            double dist = 0.8 + ((item.getId() != null ? item.getId() : 1) * 0.5);
            binding.tvDistance.setText(String.format("%.1f km", dist));

            // Status Badge
            String hours = item.getOperatingHours();
            if (hours != null && hours.toLowerCase().contains("24/7")) {
                binding.tvOpenStatus.setText("24/7 Box");
            } else {
                binding.tvOpenStatus.setText("Open");
            }

            // Tag
            String label = item.getLabel() != null ? item.getLabel().toLowerCase() : "";
            if (label.contains("box")) {
                binding.tvHubTypeTag.setText("Drop Box");
            } else if (label.contains("bay") || label.contains("depot")) {
                binding.tvHubTypeTag.setText("Intake Bay");
            } else {
                binding.tvHubTypeTag.setText("Recycling Hub");
            }

            // Load Place Image via Glide
            String imgUrl = item.getImageUrl();
            if (imgUrl != null && !imgUrl.isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(imgUrl)
                        .placeholder(R.drawable.img_hub_placeholder)
                        .error(R.drawable.img_hub_placeholder)
                        .centerCrop()
                        .into(binding.ivHubPhoto);
            } else {
                binding.ivHubPhoto.setImageResource(R.drawable.img_hub_placeholder);
            }

            // Highlight border if selected from map pin
            if (isSelected) {
                binding.cardDropOffPoint.setStrokeColor(androidx.core.content.ContextCompat.getColor(itemView.getContext(), R.color.primary_green));
                binding.cardDropOffPoint.setStrokeWidth(4);
            } else {
                binding.cardDropOffPoint.setStrokeColor(androidx.core.content.ContextCompat.getColor(itemView.getContext(), R.color.card_stroke));
                binding.cardDropOffPoint.setStrokeWidth(2);
            }

            // Click Handler
            binding.cardDropOffPoint.setOnClickListener(v -> {
                if (listener != null) listener.onCardClick(item);
            });
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onCardClick(item);
            });
        }
    }
}
