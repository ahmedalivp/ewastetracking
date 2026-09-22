package com.ewaste.app.ui.adapter;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.ewaste.app.R;
import com.ewaste.app.data.model.FacilityResponse;
import com.ewaste.app.databinding.ItemAdminFacilityBinding;

public class AdminFacilitiesAdapter extends ListAdapter<FacilityResponse, AdminFacilitiesAdapter.ViewHolder> {

    public interface OnVerifyToggleListener {
        void onToggle(FacilityResponse facility, boolean newStatus);
    }

    private final OnVerifyToggleListener listener;

    public AdminFacilitiesAdapter(OnVerifyToggleListener listener) {
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
                    && (oldItem.getName() != null && oldItem.getName().equals(newItem.getName()));
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminFacilityBinding binding = ItemAdminFacilityBinding.inflate(
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
        private final ItemAdminFacilityBinding binding;

        public ViewHolder(ItemAdminFacilityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(FacilityResponse facility, OnVerifyToggleListener listener) {
            binding.tvFacilityName.setText(facility.getName() != null ? facility.getName() : "Facility");
            binding.tvFacilityAddress.setText(facility.getAddress() != null ? facility.getAddress() : "Location Unspecified");

            if (facility.getLat() != null && facility.getLng() != null) {
                binding.tvGpsCoords.setText(String.format("📍 %.4f° N, %.4f° E", facility.getLat(), facility.getLng()));
            } else {
                binding.tvGpsCoords.setText("📍 GPS Coordinates Pending");
            }

            boolean isVerified = facility.isVerifiedByAdmin();
            if (isVerified) {
                binding.tvVerifiedPill.setText("VERIFIED");
                binding.tvVerifiedPill.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.status_completed_text));
                binding.tvVerifiedPill.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.status_completed_bg)));
                binding.btnToggleVerify.setText("Revoke");
                binding.btnToggleVerify.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.status_hazardous_text));
            } else {
                binding.tvVerifiedPill.setText("PENDING");
                binding.tvVerifiedPill.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.status_pending_text));
                binding.tvVerifiedPill.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.status_pending_bg)));
                binding.btnToggleVerify.setText("Verify");
                binding.btnToggleVerify.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.primary_green));
            }

            binding.btnToggleVerify.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onToggle(facility, !isVerified);
                }
            });
        }
    }
}
