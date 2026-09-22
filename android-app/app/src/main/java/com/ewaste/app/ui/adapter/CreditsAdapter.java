package com.ewaste.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.ewaste.app.data.model.CreditResponse;
import com.ewaste.app.databinding.ItemCreditBinding;

public class CreditsAdapter extends ListAdapter<CreditResponse, CreditsAdapter.ViewHolder> {

    public CreditsAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<CreditResponse> DIFF_CALLBACK = new DiffUtil.ItemCallback<CreditResponse>() {
        @Override
        public boolean areItemsTheSame(@NonNull CreditResponse oldItem, @NonNull CreditResponse newItem) {
            return oldItem.getId() != null && oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull CreditResponse oldItem, @NonNull CreditResponse newItem) {
            return oldItem.getAmount() == newItem.getAmount()
                    && (oldItem.getReason() != null && oldItem.getReason().equals(newItem.getReason()))
                    && (oldItem.getAwardedAt() != null && oldItem.getAwardedAt().equals(newItem.getAwardedAt()));
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCreditBinding binding = ItemCreditBinding.inflate(
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
        private final ItemCreditBinding binding;

        public ViewHolder(ItemCreditBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CreditResponse item) {
            binding.tvCreditReason.setText(item.getReason() != null ? item.getReason() : "Recycling Participation Award");
            binding.tvCreditPoints.setText(String.format("+%d pts", item.getAmount()));

            if (item.getAwardedAt() != null) {
                String cleanDate = item.getAwardedAt().replace("T", " ");
                if (cleanDate.length() > 16) cleanDate = cleanDate.substring(0, 16);
                binding.tvCreditDate.setText(cleanDate);
            } else {
                binding.tvCreditDate.setText("");
            }
        }
    }
}
