package com.ewaste.app.ui.consumer;

import android.graphics.Bitmap;
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
import com.bumptech.glide.Glide;
import com.ewaste.app.R;
import com.ewaste.app.data.model.CategoryResponse;
import com.ewaste.app.data.model.DropOffPointResponse;
import com.ewaste.app.data.model.EWasteItemResponse;
import com.ewaste.app.databinding.DialogSubmissionSuccessBinding;
import com.ewaste.app.databinding.FragmentSubmitEwasteBinding;
import com.ewaste.app.ui.common.Resource;
import com.ewaste.app.util.MapHelper;
import com.ewaste.app.util.QrCodeHelper;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class SubmitEWasteFragment extends Fragment {

    private static final String ARG_PRESELECTED_HUB = "arg_preselected_hub";

    private FragmentSubmitEwasteBinding binding;
    private ConsumerViewModel viewModel;

    private final List<CategoryResponse> categoryList = new ArrayList<>();
    private final List<DropOffPointResponse> dropOffPointList = new ArrayList<>();

    private Long selectedCategoryId = null;
    private Long selectedDropOffPointId = null;
    private DropOffPointResponse currentSelectedHub = null;

    public static SubmitEWasteFragment newInstance(DropOffPointResponse preselectedHub) {
        SubmitEWasteFragment fragment = new SubmitEWasteFragment();
        if (preselectedHub != null) {
            Bundle args = new Bundle();
            args.putSerializable(ARG_PRESELECTED_HUB, preselectedHub);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSubmitEwasteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ConsumerViewModel.class);

        if (getArguments() != null && getArguments().containsKey(ARG_PRESELECTED_HUB)) {
            currentSelectedHub = (DropOffPointResponse) getArguments().getSerializable(ARG_PRESELECTED_HUB);
        }

        if (currentSelectedHub != null) {
            displaySelectedHubCard(currentSelectedHub);
        }

        loadDropdownData();
        setupListeners();
    }

    private void displaySelectedHubCard(DropOffPointResponse hub) {
        currentSelectedHub = hub;
        selectedDropOffPointId = hub.getId();

        binding.cardSelectedHub.setVisibility(View.VISIBLE);
        binding.layoutDropOffSelector.setVisibility(View.GONE);

        binding.tvSelectedHubTitle.setText(hub.getLabel() != null ? hub.getLabel() : "Selected Hub");
        binding.tvSelectedHubFacility.setText(hub.getFacilityName() != null ? "Managed by " + hub.getFacilityName() : "Certified Facility");

        String addr = hub.getAddress() != null && !hub.getAddress().isEmpty() ? hub.getAddress() : "San Francisco, CA";
        binding.tvSelectedHubAddress.setText("📍 " + addr);

        String hours = hub.getOperatingHours() != null && !hub.getOperatingHours().isEmpty() ? hub.getOperatingHours() : "Mon - Sat: 8:00 AM - 6:00 PM";
        binding.tvSelectedHubHours.setText("⏰ Hours: " + hours);

        String imgUrl = hub.getImageUrl();
        if (imgUrl != null && !imgUrl.isEmpty()) {
            Glide.with(this)
                    .load(imgUrl)
                    .placeholder(R.drawable.img_hub_placeholder)
                    .error(R.drawable.img_hub_placeholder)
                    .centerCrop()
                    .into(binding.ivSelectedHubPhoto);
        } else {
            binding.ivSelectedHubPhoto.setImageResource(R.drawable.img_hub_placeholder);
        }
    }

    private void loadDropdownData() {
        // 1. Categories
        viewModel.getCategories().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
                categoryList.clear();
                categoryList.addAll(resource.data);

                List<String> names = new ArrayList<>();
                for (CategoryResponse cat : categoryList) {
                    names.add(cat.getName() + " - " + cat.getDescription());
                }

                if (!names.isEmpty() && getContext() != null) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            names
                    );
                    binding.actvCategory.setAdapter(adapter);
                    binding.actvCategory.setText(names.get(0), false);
                    selectedCategoryId = categoryList.get(0).getId();

                    binding.actvCategory.setOnItemClickListener((parent, v, position, id) -> {
                        if (position >= 0 && position < categoryList.size()) {
                            selectedCategoryId = categoryList.get(position).getId();
                        }
                    });
                }
            }
        });

        // 2. Drop-Off Points
        viewModel.getDropOffPoints().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
                dropOffPointList.clear();
                dropOffPointList.addAll(resource.data);

                List<String> names = new ArrayList<>();
                for (DropOffPointResponse pt : dropOffPointList) {
                    names.add(pt.getLabel() + (pt.getFacilityName() != null ? " (" + pt.getFacilityName() + ")" : ""));
                }

                if (!names.isEmpty() && getContext() != null) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            names
                    );
                    binding.actvDropOffPoint.setAdapter(adapter);

                    if (selectedDropOffPointId == null) {
                        binding.actvDropOffPoint.setText(names.get(0), false);
                        selectedDropOffPointId = dropOffPointList.get(0).getId();
                        currentSelectedHub = dropOffPointList.get(0);
                    }

                    binding.actvDropOffPoint.setOnItemClickListener((parent, v, position, id) -> {
                        if (position >= 0 && position < dropOffPointList.size()) {
                            selectedDropOffPointId = dropOffPointList.get(position).getId();
                            currentSelectedHub = dropOffPointList.get(position);
                        }
                    });
                }
            }
        });
    }

    private void setupListeners() {
        binding.btnTriangleMenu.setOnClickListener(v -> {
            QuickActionMenuSheet.newInstance()
                    .show(getParentFragmentManager(), "QuickActionMenuSheet");
        });

        binding.btnSubmitItem.setOnClickListener(v -> performSubmission());

        binding.btnChangeHub.setOnClickListener(v -> {
            binding.cardSelectedHub.setVisibility(View.GONE);
            binding.layoutDropOffSelector.setVisibility(View.VISIBLE);
        });

        binding.btnBrowseMap.setOnClickListener(v -> {
            if (getActivity() instanceof ConsumerMainActivity) {
                ((ConsumerMainActivity) getActivity()).navigateToDropOffPoints();
            }
        });
    }

    private void performSubmission() {
        if (selectedCategoryId == null) {
            Snackbar.make(binding.getRoot(), "Please select an e-waste category", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (selectedDropOffPointId == null) {
            Snackbar.make(binding.getRoot(), "Please select a drop-off location", Snackbar.LENGTH_SHORT).show();
            return;
        }

        String description = binding.etDescription.getText() != null ? binding.etDescription.getText().toString().trim() : "";
        if (description.isEmpty()) {
            binding.tilDescription.setError("Device description is required");
            return;
        }
        binding.tilDescription.setError(null);

        viewModel.submitItem(selectedCategoryId, selectedDropOffPointId, description)
                .observe(getViewLifecycleOwner(), this::handleSubmitResponse);
    }

    private void handleSubmitResponse(Resource<EWasteItemResponse> resource) {
        if (resource == null) return;

        switch (resource.status) {
            case LOADING:
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.btnSubmitItem.setEnabled(false);
                break;

            case SUCCESS:
                binding.progressBar.setVisibility(View.GONE);
                binding.btnSubmitItem.setEnabled(true);
                binding.etDescription.setText("");

                showSuccessPassportDialog(resource.data);
                break;

            case ERROR:
                binding.progressBar.setVisibility(View.GONE);
                binding.btnSubmitItem.setEnabled(true);
                String msg = resource.message != null ? resource.message : "Submission failed";
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
                break;
        }
    }

    private void showSuccessPassportDialog(EWasteItemResponse item) {
        if (item == null || getContext() == null) return;

        DialogSubmissionSuccessBinding dialogBinding =
                DialogSubmissionSuccessBinding.inflate(LayoutInflater.from(requireContext()));

        String passportCode = QrCodeHelper.formatPassportToken(item.getId());
        dialogBinding.tvTrackingCode.setText("Digital Passport: " + passportCode);

        // Generate QR bitmap
        Bitmap qrBitmap = QrCodeHelper.generateQrCode(passportCode, 400);
        if (qrBitmap != null) {
            dialogBinding.ivQrCode.setImageBitmap(qrBitmap);
        }

        // Designated Hub Info
        DropOffPointResponse hub = currentSelectedHub;
        if (hub != null) {
            dialogBinding.tvDialogHubName.setText(hub.getLabel() != null ? hub.getLabel() : "Drop-Off Station");
            dialogBinding.tvDialogHubAddress.setText(hub.getAddress() != null ? hub.getAddress() : "San Francisco, CA");
            dialogBinding.tvDialogHubHours.setText("Hours: " + (hub.getOperatingHours() != null ? hub.getOperatingHours() : "Mon - Sat 8am - 6pm"));
        } else {
            dialogBinding.tvDialogHubName.setText(item.getDropOffPointLabel() != null ? item.getDropOffPointLabel() : "Designated Hub");
            dialogBinding.tvDialogHubAddress.setText("Intake Station");
            dialogBinding.tvDialogHubHours.setText("Available during operating hours");
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogBinding.getRoot())
                .setCancelable(false)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialogBinding.btnViewInItems.setOnClickListener(v -> {
            dialog.dismiss();
            if (getActivity() instanceof ConsumerMainActivity) {
                ((ConsumerMainActivity) getActivity()).navigateToSubmissions();
            }
        });

        dialogBinding.btnNavigateHub.setOnClickListener(v -> {
            dialog.dismiss();
            if (hub != null && hub.getLat() != null && hub.getLng() != null) {
                MapHelper.launchNavigation(requireContext(), hub.getLat(), hub.getLng(), hub.getLabel());
            } else {
                if (getActivity() instanceof ConsumerMainActivity) {
                    ((ConsumerMainActivity) getActivity()).navigateToSubmissions();
                }
            }
        });

        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
