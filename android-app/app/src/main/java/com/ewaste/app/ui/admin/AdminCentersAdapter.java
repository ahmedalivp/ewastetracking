package com.ewaste.app.ui.admin;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.ewaste.app.R;
import com.ewaste.app.data.model.FacilityResponse;
import com.ewaste.app.databinding.ItemAdminCenterBinding;

public class AdminCentersAdapter extends ListAdapter<FacilityResponse, AdminCentersAdapter.ViewHolder> {

    public interface OnCenterActionListener {
        void onManageAccess(FacilityResponse facility);
        void onQuickApprove(FacilityResponse facility);
    }

    private final OnCenterActionListener listener;

    public AdminCentersAdapter(OnCenterActionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<FacilityResponse> DIFF_CALLBACK = new DiffUtil.ItemCallback<FacilityResponse>() {
        @Override
        public boolean areItemsTheSame(@NonNull FacilityResponse oldItem, @NonNull FacilityResponse newItem) {
            return oldItem.getId() != null && oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull FacilityResponse oldItem, @NonNull FacilityResponse newItem) {
            return oldItem.isVerifiedByAdmin() == newItem.isVerifiedByAdmin()
                    && oldItem.isActive() == newItem.isActive()
                    && (oldItem.getName() != null && oldItem.getName().equals(newItem.getName()))
                    && (oldItem.getAddress() != null && oldItem.getAddress().equals(newItem.getAddress()))
                    && oldItem.getStaffCount() == newItem.getStaffCount()
                    && oldItem.getDropOffCount() == newItem.getDropOffCount();
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminCenterBinding binding = ItemAdminCenterBinding.inflate(
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
        private final ItemAdminCenterBinding binding;

        public ViewHolder(ItemAdminCenterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(FacilityResponse facility, OnCenterActionListener listener) {
            binding.tvCenterName.setText(facility.getName() != null ? facility.getName() : "Recycling Facility");
            binding.tvCenterAddress.setText(facility.getAddress() != null ? facility.getAddress() : "Location Unspecified");

            if (facility.getLat() != null && facility.getLng() != null) {
                binding.tvCenterCoords.setText(String.format("%.4f° N, %.4f° E", facility.getLat(), facility.getLng()));
            } else {
                binding.tvCenterCoords.setText("GPS Pending");
            }

            int staffCount = facility.getStaffCount();
            binding.tvCenterStaffCount.setText("👥 " + staffCount + (staffCount == 1 ? " Staff" : " Staff"));

            int dropOffCount = facility.getDropOffCount();
            binding.tvCenterDropOffCount.setText("📍 " + dropOffCount + (dropOffCount == 1 ? " Kiosk" : " Kiosks"));

            if (!facility.isActive()) {
                binding.tvCenterStatus.setText("SUSPENDED");
                binding.tvCenterStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.status_hazardous_text));
                binding.tvCenterStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.status_hazardous_bg)));
                binding.btnQuickApprove.setVisibility(View.GONE);
            } else if (!facility.isVerifiedByAdmin()) {
                binding.tvCenterStatus.setText("PENDING REVIEW");
                binding.tvCenterStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.status_pending_text));
                binding.tvCenterStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.status_pending_bg)));
                binding.btnQuickApprove.setVisibility(View.VISIBLE);
            } else {
                binding.tvCenterStatus.setText("VERIFIED ACTIVE");
                binding.tvCenterStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.status_completed_text));
                binding.tvCenterStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.status_completed_bg)));
                binding.btnQuickApprove.setVisibility(View.GONE);
            }

            binding.btnQuickApprove.setOnClickListener(v -> {
                if (listener != null) listener.onQuickApprove(facility);
            });

            binding.btnEditAccess.setOnClickListener(v -> {
                if (listener != null) listener.onManageAccess(facility);
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onManageAccess(facility);
            });
        }
    }
}
