package com.ewaste.app.ui.admin;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.ewaste.app.R;
import com.ewaste.app.data.model.StaffMemberResponse;
import com.ewaste.app.databinding.ItemAdminStaffBinding;

import java.util.Objects;

public class AdminStaffAdapter extends ListAdapter<StaffMemberResponse, AdminStaffAdapter.ViewHolder> {

    public interface OnStaffActionListener {
        void onToggleActive(StaffMemberResponse staff, boolean active);
        void onReassignFacility(StaffMemberResponse staff);
    }

    private final OnStaffActionListener listener;

    public AdminStaffAdapter(OnStaffActionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<StaffMemberResponse> DIFF_CALLBACK = new DiffUtil.ItemCallback<StaffMemberResponse>() {
        @Override
        public boolean areItemsTheSame(@NonNull StaffMemberResponse oldItem, @NonNull StaffMemberResponse newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull StaffMemberResponse oldItem, @NonNull StaffMemberResponse newItem) {
            return oldItem.isActive() == newItem.isActive()
                    && Objects.equals(oldItem.getFullName(), newItem.getFullName())
                    && Objects.equals(oldItem.getEmail(), newItem.getEmail())
                    && Objects.equals(oldItem.getFacilityId(), newItem.getFacilityId())
                    && Objects.equals(oldItem.getFacilityName(), newItem.getFacilityName());
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminStaffBinding binding = ItemAdminStaffBinding.inflate(
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
        private final ItemAdminStaffBinding binding;

        public ViewHolder(ItemAdminStaffBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(StaffMemberResponse staff, OnStaffActionListener listener) {
            binding.tvStaffName.setText(staff.getFullName() != null ? staff.getFullName() : "Staff Member");
            binding.tvStaffEmail.setText(staff.getEmail() != null ? staff.getEmail() : "No email");

            String facilityName = staff.getFacilityName();
            if (facilityName != null && !facilityName.isEmpty()) {
                binding.tvStaffFacility.setText("🏢 Assigned: " + facilityName);
            } else {
                binding.tvStaffFacility.setText("🏢 Unassigned");
            }

            if (staff.getId() != null) {
                binding.tvStaffJoined.setText("Staff ID #" + staff.getId());
            } else {
                binding.tvStaffJoined.setText("");
            }

            if (staff.isActive()) {
                binding.tvStaffStatus.setText("AUTHORIZED");
                binding.tvStaffStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.status_completed_text));
                binding.tvStaffStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.status_completed_bg)));
            } else {
                binding.tvStaffStatus.setText("SUSPENDED");
                binding.tvStaffStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.status_hazardous_text));
                binding.tvStaffStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.status_hazardous_bg)));
            }

            // Temporarily clear listener to prevent firing while binding
            binding.switchStaffActive.setOnCheckedChangeListener(null);
            binding.switchStaffActive.setChecked(staff.isActive());
            binding.switchStaffActive.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onToggleActive(staff, isChecked);
                }
            });

            binding.btnReassignStaff.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onReassignFacility(staff);
                }
            });
        }
    }
}
