package com.ewaste.app.ui.consumer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.ewaste.app.R;
import com.ewaste.app.data.model.CategoryResponse;
import com.ewaste.app.data.model.DropOffPointResponse;
import com.ewaste.app.data.model.EWasteItemResponse;
import com.ewaste.app.databinding.FragmentSubmitEwasteBinding;
import com.ewaste.app.ui.common.Resource;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class SubmitEWasteFragment extends Fragment {

    private FragmentSubmitEwasteBinding binding;
    private ConsumerViewModel viewModel;

    private final List<CategoryResponse> categoryList = new ArrayList<>();
    private final List<DropOffPointResponse> dropOffPointList = new ArrayList<>();

    private Long selectedCategoryId = null;
    private Long selectedDropOffPointId = null;

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

        loadDropdownData();
        setupListeners();
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
                    binding.actvDropOffPoint.setText(names.get(0), false);
                    selectedDropOffPointId = dropOffPointList.get(0).getId();

                    binding.actvDropOffPoint.setOnItemClickListener((parent, v, position, id) -> {
                        if (position >= 0 && position < dropOffPointList.size()) {
                            selectedDropOffPointId = dropOffPointList.get(position).getId();
                        }
                    });
                }
            }
        });
    }

    private void setupListeners() {
        binding.btnSubmitItem.setOnClickListener(v -> performSubmission());
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

                Snackbar.make(binding.getRoot(), "Item submitted successfully! Tracking initiated.", Snackbar.LENGTH_LONG)
                        .setAction("View Items", v -> {
                            if (getActivity() instanceof ConsumerMainActivity) {
                                ((ConsumerMainActivity) getActivity()).navigateToSubmissions();
                            }
                        })
                        .show();
                break;

            case ERROR:
                binding.progressBar.setVisibility(View.GONE);
                binding.btnSubmitItem.setEnabled(true);
                String msg = resource.message != null ? resource.message : "Submission failed";
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
