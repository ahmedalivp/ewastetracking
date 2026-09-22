package com.ewaste.app.ui.facility;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.ewaste.app.R;
import com.ewaste.app.data.model.CategoryResponse;
import com.ewaste.app.data.model.EWasteItemResponse;
import com.ewaste.app.databinding.DialogCategorizeBinding;
import com.ewaste.app.databinding.DialogFlagHazardousBinding;
import com.ewaste.app.databinding.DialogHarvestComponentBinding;
import com.ewaste.app.databinding.FragmentFacilityItemDetailBinding;
import com.ewaste.app.ui.adapter.TrackingTimelineAdapter;
import com.ewaste.app.ui.common.Resource;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class FacilityItemDetailFragment extends Fragment {

    private static final String ARG_ITEM = "arg_item";

    private FragmentFacilityItemDetailBinding binding;
    private FacilityViewModel viewModel;
    private TrackingTimelineAdapter timelineAdapter;
    private EWasteItemResponse item;

    public static FacilityItemDetailFragment newInstance(EWasteItemResponse item) {
        FacilityItemDetailFragment fragment = new FacilityItemDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFacilityItemDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(FacilityViewModel.class);

        if (getArguments() != null) {
            item = (EWasteItemResponse) getArguments().getSerializable(ARG_ITEM);
        }

        if (item == null) return;

        setupViews();
        setupTimelineRecyclerView();
        setupActionButtons();
        loadTrackingTimeline();
    }

    private void setupViews() {
        binding.tvCategory.setText(item.getCategoryName() != null ? item.getCategoryName() : "E-Waste");
        binding.tvSubmitter.setText(item.getConsumerName() != null ? "by " + item.getConsumerName() : "by Consumer");
        binding.tvDescription.setText(item.getDeviceDescription() != null ? item.getDeviceDescription() : "Electronic Device");
        binding.tvDropOffLocation.setText(item.getDropOffPointLabel() != null ? "📍 Drop-off: " + item.getDropOffPointLabel() : "📍 Drop-off Center");

        String status = item.getStatus() != null ? item.getStatus() : "SUBMITTED";
        binding.tvStatus.setText(status);
        applyStatusColor(status);

        binding.tvComponentsCount.setText(String.format("⚙️ Harvested: %d", item.getComponentCount()));
        binding.tvHazardousCount.setText(String.format("☣️ Hazardous: %d", item.getHazardousMaterialCount()));

        // Hide "Mark Received" if already received or beyond
        if (!"SUBMITTED".equalsIgnoreCase(status) && !"DROPPED_OFF".equalsIgnoreCase(status)) {
            binding.btnMarkReceived.setVisibility(View.GONE);
        } else {
            binding.btnMarkReceived.setVisibility(View.VISIBLE);
        }

        binding.btnBack.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });
    }

    private void applyStatusColor(String status) {
        int color;
        switch (status) {
            case "RECEIVED":
                color = Color.parseColor("#7B1FA2");
                break;
            case "CATEGORIZED":
                color = Color.parseColor("#0288D1");
                break;
            case "PROCESSED":
                color = Color.parseColor("#F57C00");
                break;
            case "COMPLETED":
                color = Color.parseColor("#388E3C");
                break;
            case "SUBMITTED":
            default:
                color = Color.parseColor("#1976D2");
                break;
        }
        binding.tvStatus.setBackgroundColor(color);
    }

    private void setupTimelineRecyclerView() {
        timelineAdapter = new TrackingTimelineAdapter();
        binding.rvTimeline.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvTimeline.setAdapter(timelineAdapter);
    }

    private void setupActionButtons() {
        binding.btnMarkReceived.setOnClickListener(v -> performMarkReceived());
        binding.btnCategorize.setOnClickListener(v -> showCategorizeDialog());
        binding.btnHarvestComponent.setOnClickListener(v -> showHarvestDialog());
        binding.btnFlagHazardous.setOnClickListener(v -> showFlagHazardousDialog());
    }

    private void performMarkReceived() {
        binding.btnMarkReceived.setEnabled(false);
        viewModel.receiveItem(item.getId()).observe(getViewLifecycleOwner(), resource -> {
            binding.btnMarkReceived.setEnabled(true);
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
                this.item = resource.data;
                setupViews();
                loadTrackingTimeline();
                Snackbar.make(binding.getRoot(), "✅ Device marked as RECEIVED at facility.", Snackbar.LENGTH_LONG).show();
            } else if (resource != null && resource.status == Resource.Status.ERROR) {
                String msg = resource.message != null ? resource.message : "Failed to receive item";
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void showCategorizeDialog() {
        if (getContext() == null) return;

        DialogCategorizeBinding dialogBinding = DialogCategorizeBinding.inflate(getLayoutInflater());
        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogBinding.getRoot())
                .create();

        final List<CategoryResponse> categories = new ArrayList<>();
        final Long[] selectedCatId = new Long[]{item.getCategoryId()};

        viewModel.getCategories().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
                categories.clear();
                categories.addAll(resource.data);

                List<String> names = new ArrayList<>();
                int initialIndex = 0;
                for (int i = 0; i < categories.size(); i++) {
                    CategoryResponse cat = categories.get(i);
                    names.add(cat.getName() + " (" + cat.getDescription() + ")");
                    if (cat.getId().equals(item.getCategoryId())) {
                        initialIndex = i;
                    }
                }

                if (!names.isEmpty() && getContext() != null) {
                    ArrayAdapter<String> catAdapter = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            names
                    );
                    dialogBinding.actvCategory.setAdapter(catAdapter);
                    dialogBinding.actvCategory.setText(names.get(initialIndex), false);
                    selectedCatId[0] = categories.get(initialIndex).getId();

                    dialogBinding.actvCategory.setOnItemClickListener((parent, v, position, id) -> {
                        selectedCatId[0] = categories.get(position).getId();
                    });
                }
            }
        });

        dialogBinding.btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialogBinding.btnConfirmCategorize.setOnClickListener(v -> {
            String notes = dialogBinding.etTriageNotes.getText() != null ? dialogBinding.etTriageNotes.getText().toString().trim() : "";
            if (notes.isEmpty()) {
                dialogBinding.tilTriageNotes.setError("Triage inspection notes required");
                return;
            }
            dialogBinding.tilTriageNotes.setError(null);
            dialogBinding.btnConfirmCategorize.setEnabled(false);

            viewModel.categorizeItem(item.getId(), selectedCatId[0], notes).observe(getViewLifecycleOwner(), resource -> {
                if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
                    dialog.dismiss();
                    this.item = resource.data;
                    setupViews();
                    loadTrackingTimeline();
                    Snackbar.make(binding.getRoot(), "✅ Item categorized & triage notes recorded.", Snackbar.LENGTH_LONG).show();
                } else if (resource != null && resource.status == Resource.Status.ERROR) {
                    dialogBinding.btnConfirmCategorize.setEnabled(true);
                    String msg = resource.message != null ? resource.message : "Categorization failed";
                    dialogBinding.tilTriageNotes.setError(msg);
                }
            });
        });

        dialog.show();
    }

    private void showHarvestDialog() {
        if (getContext() == null) return;

        DialogHarvestComponentBinding dialogBinding = DialogHarvestComponentBinding.inflate(getLayoutInflater());
        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogBinding.getRoot())
                .create();

        String[] conditions = new String[]{"GOOD", "FAIR", "POOR"};
        ArrayAdapter<String> condAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                conditions
        );
        dialogBinding.actvCondition.setAdapter(condAdapter);
        dialogBinding.actvCondition.setText(conditions[0], false);

        dialogBinding.btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialogBinding.btnConfirmHarvest.setOnClickListener(v -> {
            String name = dialogBinding.etComponentName.getText() != null ? dialogBinding.etComponentName.getText().toString().trim() : "";
            String type = dialogBinding.etComponentType.getText() != null ? dialogBinding.etComponentType.getText().toString().trim() : "";
            String condition = dialogBinding.actvCondition.getText().toString().trim();

            if (name.isEmpty()) {
                dialogBinding.tilComponentName.setError("Part name required");
                return;
            }
            if (type.isEmpty()) {
                dialogBinding.tilComponentType.setError("Part type required");
                return;
            }

            dialogBinding.btnConfirmHarvest.setEnabled(false);

            viewModel.harvestComponent(item.getId(), name, type, condition).observe(getViewLifecycleOwner(), resource -> {
                if (resource != null && resource.status == Resource.Status.SUCCESS) {
                    dialog.dismiss();
                    loadTrackingTimeline();
                    Snackbar.make(binding.getRoot(), "🎉 Harvested '" + name + "'! Credits automatically awarded.", Snackbar.LENGTH_LONG).show();
                    refreshItem();
                } else if (resource != null && resource.status == Resource.Status.ERROR) {
                    dialogBinding.btnConfirmHarvest.setEnabled(true);
                    String msg = resource.message != null ? resource.message : "Harvesting failed";
                    Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
                }
            });
        });

        dialog.show();
    }

    private void showFlagHazardousDialog() {
        if (getContext() == null) return;

        DialogFlagHazardousBinding dialogBinding = DialogFlagHazardousBinding.inflate(getLayoutInflater());
        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogBinding.getRoot())
                .create();

        String[] materials = new String[]{"BATTERY", "MERCURY", "LEAD", "CADMIUM", "OTHER"};
        ArrayAdapter<String> matAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                materials
        );
        dialogBinding.actvMaterialType.setAdapter(matAdapter);
        dialogBinding.actvMaterialType.setText(materials[0], false);

        dialogBinding.btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialogBinding.btnConfirmFlag.setOnClickListener(v -> {
            String materialType = dialogBinding.actvMaterialType.getText().toString().trim();
            String instructions = dialogBinding.etInstructions.getText() != null ? dialogBinding.etInstructions.getText().toString().trim() : "";

            if (instructions.isEmpty()) {
                dialogBinding.tilInstructions.setError("Handling instructions required");
                return;
            }

            dialogBinding.btnConfirmFlag.setEnabled(false);

            viewModel.flagHazardousMaterial(item.getId(), materialType, instructions).observe(getViewLifecycleOwner(), resource -> {
                if (resource != null && resource.status == Resource.Status.SUCCESS) {
                    dialog.dismiss();
                    loadTrackingTimeline();
                    Snackbar.make(binding.getRoot(), "⚠️ Flagged " + materialType + " for regulated diversion.", Snackbar.LENGTH_LONG).show();
                    refreshItem();
                } else if (resource != null && resource.status == Resource.Status.ERROR) {
                    dialogBinding.btnConfirmFlag.setEnabled(true);
                    String msg = resource.message != null ? resource.message : "Flagging failed";
                    Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
                }
            });
        });

        dialog.show();
    }

    private void refreshItem() {
        viewModel.getIntakeQueue(null).observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
                for (EWasteItemResponse it : resource.data) {
                    if (it.getId().equals(item.getId())) {
                        this.item = it;
                        setupViews();
                        break;
                    }
                }
            }
        });
    }

    private void loadTrackingTimeline() {
        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.getItemTracking(item.getId()).observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            binding.progressBar.setVisibility(View.GONE);
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                timelineAdapter.submitList(resource.data);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
