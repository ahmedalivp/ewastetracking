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

public class BusinessRepository {

    private final ApiService apiService;

    public BusinessRepository() {
        this.apiService = ApiClient.getApiService();
    }

    public LiveData<Resource<List<ComponentResponse>>> getAvailableComponents(String category) {
        MutableLiveData<Resource<List<ComponentResponse>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.getAvailableComponents(category, "AVAILABLE").enqueue(new Callback<List<ComponentResponse>>() {
            @Override
            public void onResponse(Call<List<ComponentResponse>> call, Response<List<ComponentResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Failed to load components (" + response.code() + ")", null));
                }
            }

            @Override
            public void onFailure(Call<List<ComponentResponse>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<ComponentRequestResponse>> requestComponent(Long componentId) {
        MutableLiveData<Resource<ComponentRequestResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        CreateComponentRequestDto request = new CreateComponentRequestDto(componentId);
        apiService.requestComponent(request).enqueue(new Callback<ComponentRequestResponse>() {
            @Override
            public void onResponse(Call<ComponentRequestResponse> call, Response<ComponentRequestResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Component requisition failed (" + response.code() + ")", null));
                }
            }

            @Override
            public void onFailure(Call<ComponentRequestResponse> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<List<ComponentRequestResponse>>> getMyRequests() {
        MutableLiveData<Resource<List<ComponentRequestResponse>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.getMyComponentRequests().enqueue(new Callback<List<ComponentRequestResponse>>() {
            @Override
            public void onResponse(Call<List<ComponentRequestResponse>> call, Response<List<ComponentRequestResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Failed to load requests (" + response.code() + ")", null));
                }
            }

            @Override
            public void onFailure(Call<List<ComponentRequestResponse>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }
}
