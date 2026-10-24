package com.ewaste.app.ui.admin;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.ewaste.app.data.model.DropOffPointResponse;
import com.ewaste.app.databinding.ItemAdminDropoffBinding;

import java.util.Objects;

public class AdminDropOffAdapter extends ListAdapter<DropOffPointResponse, AdminDropOffAdapter.ViewHolder> {

    public interface OnDropOffActionListener {
        void onEdit(DropOffPointResponse dropOff);
        void onDelete(DropOffPointResponse dropOff);
    }

    private final OnDropOffActionListener listener;

    public AdminDropOffAdapter(OnDropOffActionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<DropOffPointResponse> DIFF_CALLBACK = new DiffUtil.ItemCallback<DropOffPointResponse>() {
        @Override
        public boolean areItemsTheSame(@NonNull DropOffPointResponse oldItem, @NonNull DropOffPointResponse newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull DropOffPointResponse oldItem, @NonNull DropOffPointResponse newItem) {
            return Objects.equals(oldItem.getLabel(), newItem.getLabel())
                    && Objects.equals(oldItem.getAddress(), newItem.getAddress())
                    && Objects.equals(oldItem.getFacilityName(), newItem.getFacilityName())
                    && Objects.equals(oldItem.getOperatingHours(), newItem.getOperatingHours())
                    && Objects.equals(oldItem.getAcceptedTypes(), newItem.getAcceptedTypes())
                    && Objects.equals(oldItem.getLat(), newItem.getLat())
                    && Objects.equals(oldItem.getLng(), newItem.getLng());
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminDropoffBinding binding = ItemAdminDropoffBinding.inflate(
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
        private final ItemAdminDropoffBinding binding;

        public ViewHolder(ItemAdminDropoffBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(DropOffPointResponse dropOff, OnDropOffActionListener listener) {
            binding.tvStationName.setText(dropOff.getLabel() != null ? dropOff.getLabel() : "Collection Point");
            binding.tvStationAddress.setText(dropOff.getAddress() != null ? dropOff.getAddress() : "Location Unspecified");

            String facilityName = dropOff.getFacilityName();
            if (facilityName != null && !facilityName.isEmpty()) {
                binding.tvParentFacility.setText("Hub: " + facilityName);
            } else {
                binding.tvParentFacility.setText("Direct Point");
            }

            String hours = dropOff.getOperatingHours();
            binding.tvOperatingHours.setText("🕒 " + (hours != null && !hours.isEmpty() ? hours : "Daily 8am-8pm"));

            if (dropOff.getLat() != null && dropOff.getLng() != null) {
                binding.tvGpsCoords.setText(String.format("%.4f° N, %.4f° E", dropOff.getLat(), dropOff.getLng()));
            } else {
                binding.tvGpsCoords.setText("GPS Pending");
            }

            String types = dropOff.getAcceptedTypes();
            if (types != null && !types.isEmpty()) {
                binding.tvAcceptedTypes.setText("Accepted: " + types);
            } else {
                binding.tvAcceptedTypes.setText("Accepted: General Electronics & Batteries");
            }

            binding.btnEditDropOff.setOnClickListener(v -> {
                if (listener != null) listener.onEdit(dropOff);
            });

            binding.btnDeleteDropOff.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(dropOff);
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onEdit(dropOff);
            });
        }
    }
}
