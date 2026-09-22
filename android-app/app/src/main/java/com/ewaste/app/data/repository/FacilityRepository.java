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

public class FacilityRepository {

    private final ApiService apiService;

    public FacilityRepository() {
        this.apiService = ApiClient.getApiService();
    }

    public LiveData<Resource<List<EWasteItemResponse>>> getIntakeQueue(String status) {
        MutableLiveData<Resource<List<EWasteItemResponse>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.getIntakeQueue(status).enqueue(new Callback<List<EWasteItemResponse>>() {
            @Override
            public void onResponse(Call<List<EWasteItemResponse>> call, Response<List<EWasteItemResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Failed to load queue (" + response.code() + ")", null));
                }
            }

            @Override
            public void onFailure(Call<List<EWasteItemResponse>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<EWasteItemResponse>> receiveItem(Long itemId) {
        MutableLiveData<Resource<EWasteItemResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.receiveItem(itemId).enqueue(new Callback<EWasteItemResponse>() {
            @Override
            public void onResponse(Call<EWasteItemResponse> call, Response<EWasteItemResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Failed to mark received (" + response.code() + ")", null));
                }
            }

            @Override
            public void onFailure(Call<EWasteItemResponse> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<EWasteItemResponse>> categorizeItem(Long itemId, UpdateCategorizeRequest request) {
        MutableLiveData<Resource<EWasteItemResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.categorizeItem(itemId, request).enqueue(new Callback<EWasteItemResponse>() {
            @Override
            public void onResponse(Call<EWasteItemResponse> call, Response<EWasteItemResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Failed to categorize item (" + response.code() + ")", null));
                }
            }

            @Override
            public void onFailure(Call<EWasteItemResponse> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<ComponentResponse>> harvestComponent(Long itemId, HarvestComponentRequest request) {
        MutableLiveData<Resource<ComponentResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.harvestComponent(itemId, request).enqueue(new Callback<ComponentResponse>() {
            @Override
            public void onResponse(Call<ComponentResponse> call, Response<ComponentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Harvesting failed (" + response.code() + ")", null));
                }
            }

            @Override
            public void onFailure(Call<ComponentResponse> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<HazardousMaterialResponse>> flagHazardousMaterial(Long itemId, FlagHazardousMaterialRequest request) {
        MutableLiveData<Resource<HazardousMaterialResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.flagHazardousMaterial(itemId, request).enqueue(new Callback<HazardousMaterialResponse>() {
            @Override
            public void onResponse(Call<HazardousMaterialResponse> call, Response<HazardousMaterialResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Failed to flag hazardous material (" + response.code() + ")", null));
                }
            }

            @Override
            public void onFailure(Call<HazardousMaterialResponse> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<HazardousMaterialResponse>> divertHazardousMaterial(Long materialId) {
        MutableLiveData<Resource<HazardousMaterialResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.divertHazardousMaterial(materialId).enqueue(new Callback<HazardousMaterialResponse>() {
            @Override
            public void onResponse(Call<HazardousMaterialResponse> call, Response<HazardousMaterialResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Failed to divert material (" + response.code() + ")", null));
                }
            }

            @Override
            public void onFailure(Call<HazardousMaterialResponse> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<List<ComponentResponse>>> getInventoryComponents() {
        MutableLiveData<Resource<List<ComponentResponse>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.getFacilityComponents(null).enqueue(new Callback<List<ComponentResponse>>() {
            @Override
            public void onResponse(Call<List<ComponentResponse>> call, Response<List<ComponentResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Failed to load inventory (" + response.code() + ")", null));
                }
            }

            @Override
            public void onFailure(Call<List<ComponentResponse>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }
}
