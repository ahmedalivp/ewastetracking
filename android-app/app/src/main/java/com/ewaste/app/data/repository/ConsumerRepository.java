package com.ewaste.app.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.ewaste.app.data.api.ApiClient;
import com.ewaste.app.data.api.ApiService;
import com.ewaste.app.data.model.*;
import com.ewaste.app.ui.common.Resource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class ConsumerRepository {

    private final ApiService apiService;

    public ConsumerRepository() {
        this.apiService = ApiClient.getApiService();
    }

    public LiveData<Resource<List<CategoryResponse>>> getCategories() {
        MutableLiveData<Resource<List<CategoryResponse>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.getCategories().enqueue(new Callback<List<CategoryResponse>>() {
            @Override
            public void onResponse(Call<List<CategoryResponse>> call, Response<List<CategoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Failed to load categories (" + response.code() + ")", null));
                }
            }

            @Override
            public void onFailure(Call<List<CategoryResponse>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<List<DropOffPointResponse>>> getDropOffPoints() {
        MutableLiveData<Resource<List<DropOffPointResponse>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.getDropOffPoints().enqueue(new Callback<List<DropOffPointResponse>>() {
            @Override
            public void onResponse(Call<List<DropOffPointResponse>> call, Response<List<DropOffPointResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                    java.util.concurrent.Executors.newSingleThreadExecutor().execute(() -> {
                        try {
                            com.ewaste.app.data.local.db.AppDatabase db = com.ewaste.app.data.local.db.AppDatabase.getInstance(com.ewaste.app.EWasteApplication.getInstance());
                            java.util.List<com.ewaste.app.data.local.db.CachedDropOffPoint> entities = new java.util.ArrayList<>();
                            for (DropOffPointResponse p : response.body()) {
                                entities.add(new com.ewaste.app.data.local.db.CachedDropOffPoint(p.getId(), p.getLabel(), p.getLat(), p.getLng(), p.getFacilityId(), p.getFacilityName()));
                            }
                            db.dropOffPointDao().clearAll();
                            db.dropOffPointDao().insertAll(entities);
                        } catch (Exception ignored) {}
                    });
                } else {
                    loadCachedDropOffPoints(result, "Failed to load drop-off locations (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<List<DropOffPointResponse>> call, Throwable t) {
                loadCachedDropOffPoints(result, "Offline: loaded cached locations");
            }
        });

        return result;
    }

    private void loadCachedDropOffPoints(MutableLiveData<Resource<List<DropOffPointResponse>>> result, String fallbackMsg) {
        java.util.concurrent.Executors.newSingleThreadExecutor().execute(() -> {
            try {
                com.ewaste.app.data.local.db.AppDatabase db = com.ewaste.app.data.local.db.AppDatabase.getInstance(com.ewaste.app.EWasteApplication.getInstance());
                java.util.List<com.ewaste.app.data.local.db.CachedDropOffPoint> cached = db.dropOffPointDao().getAll();
                if (cached != null && !cached.isEmpty()) {
                    java.util.List<DropOffPointResponse> mapped = new java.util.ArrayList<>();
                    for (com.ewaste.app.data.local.db.CachedDropOffPoint c : cached) {
                        mapped.add(new DropOffPointResponse(c.getId(), c.getLabel(), c.getLat(), c.getLng(), c.getFacilityId(), c.getFacilityName()));
                    }
                    result.postValue(Resource.success(mapped));
                    return;
                }
            } catch (Exception ignored) {}
            result.postValue(Resource.error(fallbackMsg, null));
        });
    }

    public LiveData<Resource<EWasteItemResponse>> submitItem(SubmitEWasteItemRequest request) {
        MutableLiveData<Resource<EWasteItemResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.submitEWasteItem(request).enqueue(new Callback<EWasteItemResponse>() {
            @Override
            public void onResponse(Call<EWasteItemResponse> call, Response<EWasteItemResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Submission failed (" + response.code() + ")", null));
                }
            }

            @Override
            public void onFailure(Call<EWasteItemResponse> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<List<EWasteItemResponse>>> getMySubmissions() {
        MutableLiveData<Resource<List<EWasteItemResponse>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.getMySubmissions().enqueue(new Callback<List<EWasteItemResponse>>() {
            @Override
            public void onResponse(Call<List<EWasteItemResponse>> call, Response<List<EWasteItemResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                    java.util.concurrent.Executors.newSingleThreadExecutor().execute(() -> {
                        try {
                            com.ewaste.app.data.local.db.AppDatabase db = com.ewaste.app.data.local.db.AppDatabase.getInstance(com.ewaste.app.EWasteApplication.getInstance());
                            java.util.List<com.ewaste.app.data.local.db.CachedSubmission> entities = new java.util.ArrayList<>();
                            for (EWasteItemResponse item : response.body()) {
                                entities.add(new com.ewaste.app.data.local.db.CachedSubmission(
                                        item.getId(),
                                        item.getCategoryName(),
                                        item.getDeviceDescription(),
                                        item.getDropOffPointLabel(),
                                        item.getStatus(),
                                        item.getSubmittedAt(),
                                        item.getComponentCount(),
                                        item.getHazardousMaterialCount()
                                ));
                            }
                            db.submissionDao().clearAll();
                            db.submissionDao().insertAll(entities);
                        } catch (Exception ignored) {}
                    });
                } else {
                    loadCachedSubmissions(result, "Failed to fetch submissions (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<List<EWasteItemResponse>> call, Throwable t) {
                loadCachedSubmissions(result, "Offline: loaded cached submissions");
            }
        });

        return result;
    }

    private void loadCachedSubmissions(MutableLiveData<Resource<List<EWasteItemResponse>>> result, String fallbackMsg) {
        java.util.concurrent.Executors.newSingleThreadExecutor().execute(() -> {
            try {
                com.ewaste.app.data.local.db.AppDatabase db = com.ewaste.app.data.local.db.AppDatabase.getInstance(com.ewaste.app.EWasteApplication.getInstance());
                java.util.List<com.ewaste.app.data.local.db.CachedSubmission> cached = db.submissionDao().getAll();
                if (cached != null && !cached.isEmpty()) {
                    java.util.List<EWasteItemResponse> mapped = new java.util.ArrayList<>();
                    for (com.ewaste.app.data.local.db.CachedSubmission c : cached) {
                        EWasteItemResponse item = new EWasteItemResponse();
                        item.setId(c.getId());
                        item.setCategoryName(c.getCategoryName());
                        item.setDeviceDescription(c.getDeviceDescription());
                        item.setDropOffPointLabel(c.getDropOffPointLabel());
                        item.setStatus(c.getStatus());
                        item.setSubmittedAt(c.getSubmittedAt());
                        item.setComponentCount(c.getComponentCount());
                        item.setHazardousMaterialCount(c.getHazardousMaterialCount());
                        mapped.add(item);
                    }
                    result.postValue(Resource.success(mapped));
                    return;
                }
            } catch (Exception ignored) {}
            result.postValue(Resource.error(fallbackMsg, null));
        });
    }

    public LiveData<Resource<List<TrackingRecordResponse>>> getItemTracking(Long itemId) {
        MutableLiveData<Resource<List<TrackingRecordResponse>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.getItemTracking(itemId).enqueue(new Callback<List<TrackingRecordResponse>>() {
            @Override
            public void onResponse(Call<List<TrackingRecordResponse>> call, Response<List<TrackingRecordResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Failed to fetch tracking history (" + response.code() + ")", null));
                }
            }

            @Override
            public void onFailure(Call<List<TrackingRecordResponse>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<List<CreditResponse>>> getMyCredits() {
        MutableLiveData<Resource<List<CreditResponse>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.getMyCredits().enqueue(new Callback<List<CreditResponse>>() {
            @Override
            public void onResponse(Call<List<CreditResponse>> call, Response<List<CreditResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Failed to load credits (" + response.code() + ")", null));
                }
            }

            @Override
            public void onFailure(Call<List<CreditResponse>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<CreditRedemptionResponse>> redeemCredits(RedeemCreditRequest request) {
        MutableLiveData<Resource<CreditRedemptionResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.redeemCredits(request).enqueue(new Callback<CreditRedemptionResponse>() {
            @Override
            public void onResponse(Call<CreditRedemptionResponse> call, Response<CreditRedemptionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Redemption failed: Insufficient credits or error (" + response.code() + ")", null));
                }
            }

            @Override
            public void onFailure(Call<CreditRedemptionResponse> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }
}
