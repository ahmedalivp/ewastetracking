package com.ewaste.app.ui.facility;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.ewaste.app.R;
import com.ewaste.app.data.model.EWasteItemResponse;
import com.ewaste.app.databinding.FragmentIntakeQueueBinding;
import com.ewaste.app.ui.adapter.FacilityQueueAdapter;
import com.ewaste.app.ui.common.Resource;
import com.google.android.material.snackbar.Snackbar;

import androidx.activity.result.ActivityResultLauncher;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.List;

public class IntakeQueueFragment extends Fragment {

    private FragmentIntakeQueueBinding binding;
    private FacilityViewModel viewModel;
    private FacilityQueueAdapter adapter;
    private String currentStatusFilter = null; // null = All
    private final List<EWasteItemResponse> fullQueueList = new ArrayList<>();

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result != null && result.getContents() != null) {
                    handleScannedPassport(result.getContents());
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentIntakeQueueBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(FacilityViewModel.class);

        setupRecyclerView();
        setupFilterChips();
        setupSearchBar();
        setupSwipeRefresh();
        setupScanner();
        binding.btnTriangleMenu.setOnClickListener(v -> {
            com.ewaste.app.ui.consumer.QuickActionMenuSheet.newInstance()
                    .show(getParentFragmentManager(), "QuickActionMenuSheet");
        });
        loadQueue();
    }

    private void setupScanner() {
        binding.btnScanPassport.setOnClickListener(v -> {
            ScanOptions options = new ScanOptions();
            options.setPrompt("Scan E-Waste Digital Passport");
            options.setBeepEnabled(true);
            options.setOrientationLocked(false);
            options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
            barcodeLauncher.launch(options);
        });

        binding.btnScanPassport.setOnLongClickListener(v -> {
            showManualIdInputDialog();
            return true;
        });
    }

    private void setupSearchBar() {
        binding.etSearchQueue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s != null ? s.toString().trim() : "";
                binding.btnClearSearch.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
                filterQueue(query);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.btnClearSearch.setOnClickListener(v -> binding.etSearchQueue.setText(""));
    }

    private void filterQueue(String query) {
        if (query.isEmpty()) {
            displayItems(fullQueueList);
            return;
        }

        String lowerQuery = query.toLowerCase();
        List<EWasteItemResponse> filtered = new ArrayList<>();
        for (EWasteItemResponse item : fullQueueList) {
            boolean matchDesc = item.getDeviceDescription() != null && item.getDeviceDescription().toLowerCase().contains(lowerQuery);
            boolean matchCat = item.getCategoryName() != null && item.getCategoryName().toLowerCase().contains(lowerQuery);
            boolean matchConsumer = item.getConsumerName() != null && item.getConsumerName().toLowerCase().contains(lowerQuery);
            boolean matchId = item.getId() != null && String.valueOf(item.getId()).contains(lowerQuery);
            boolean matchDropOff = item.getDropOffPointLabel() != null && item.getDropOffPointLabel().toLowerCase().contains(lowerQuery);

            if (matchDesc || matchCat || matchConsumer || matchId || matchDropOff) {
                filtered.add(item);
            }
        }
        displayItems(filtered);
    }

    private void showManualIdInputDialog() {
        android.widget.EditText input = new android.widget.EditText(requireContext());
        input.setHint("e.g. 1 or EWASTE-0001");
        input.setPadding(48, 32, 48, 32);

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Enter Passport ID")
                .setMessage("Quick lookup device without camera scanning:")
                .setView(input)
                .setPositiveButton("Lookup", (d, w) -> {
                    handleScannedPassport(input.getText().toString());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void handleScannedPassport(String rawCode) {
        if (rawCode == null || rawCode.trim().isEmpty()) return;
        String clean = rawCode.trim();
        Long parsedId = com.ewaste.app.util.QrCodeHelper.parsePassportToken(clean);

        if (parsedId != null) {
            EWasteItemResponse match = null;
            for (EWasteItemResponse item : fullQueueList) {
                if (item.getId() != null && item.getId().equals(parsedId)) {
                    match = item;
                    break;
                }
            }
            if (match != null) {
                onItemClicked(match);
            } else {
                Snackbar.make(binding.getRoot(), "Found Passport ID #" + parsedId + " (Clear filter to view)", Snackbar.LENGTH_LONG).show();
            }
        } else {
            Snackbar.make(binding.getRoot(), "Scanned code: " + clean, Snackbar.LENGTH_LONG).show();
        }
    }

    private void setupRecyclerView() {
        adapter = new FacilityQueueAdapter(this::onItemClicked);
        binding.rvIntakeQueue.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvIntakeQueue.setAdapter(adapter);
    }

    private void setupFilterChips() {
        binding.chipGroupFilter.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chipAll) {
                currentStatusFilter = null;
            } else if (id == R.id.chipSubmitted) {
                currentStatusFilter = "SUBMITTED";
            } else if (id == R.id.chipReceived) {
                currentStatusFilter = "RECEIVED";
            } else if (id == R.id.chipCategorized) {
                currentStatusFilter = "CATEGORIZED";
            } else if (id == R.id.chipProcessed) {
                currentStatusFilter = "PROCESSED";
            }
            loadQueue();
        });
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(this::loadQueue);
    }

    private void loadQueue() {
        viewModel.getIntakeQueue(currentStatusFilter).observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            binding.swipeRefresh.setRefreshing(false);

            if (resource.status == Resource.Status.LOADING) {
                if (adapter.getItemCount() == 0) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                }
            } else if (resource.status == Resource.Status.SUCCESS) {
                binding.progressBar.setVisibility(View.GONE);
                fullQueueList.clear();
                if (resource.data != null) {
                    fullQueueList.addAll(resource.data);
                }

                String currentQuery = binding.etSearchQueue.getText() != null ? binding.etSearchQueue.getText().toString().trim() : "";
                filterQueue(currentQuery);
            } else if (resource.status == Resource.Status.ERROR) {
                binding.progressBar.setVisibility(View.GONE);
                String msg = resource.message != null ? resource.message : "Failed to load intake queue";
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void displayItems(List<EWasteItemResponse> items) {
        if (items != null && !items.isEmpty()) {
            binding.layoutEmpty.setVisibility(View.GONE);
            binding.rvIntakeQueue.setVisibility(View.VISIBLE);
            adapter.submitList(new ArrayList<>(items));
            binding.tvQueueSummary.setText(String.format("Showing %d %s ready for triage", items.size(), items.size() == 1 ? "device" : "devices"));
        } else {
            binding.layoutEmpty.setVisibility(View.VISIBLE);
            binding.rvIntakeQueue.setVisibility(View.GONE);
            adapter.submitList(new ArrayList<>());
            binding.tvQueueSummary.setText("No devices found");
        }
    }

    private void onItemClicked(EWasteItemResponse item) {
        if (getActivity() instanceof FacilityMainActivity) {
            ((FacilityMainActivity) getActivity()).showItemDetail(item);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
