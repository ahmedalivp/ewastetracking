package com.ewaste.app.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.ewaste.app.data.api.ApiClient;
import com.ewaste.app.data.api.ApiService;
import com.ewaste.app.data.model.FacilityResponse;
import com.ewaste.app.data.model.ImpactStatsResponse;
import com.ewaste.app.data.model.VerifyFacilityRequest;
import com.ewaste.app.ui.common.Resource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class AdminRepository {

    private final ApiService apiService;

    public AdminRepository() {
        this.apiService = ApiClient.getApiService();
    }

    public LiveData<Resource<ImpactStatsResponse>> getImpactStats() {
        MutableLiveData<Resource<ImpactStatsResponse>> data = new MutableLiveData<>();
        data.setValue(Resource.loading());

        apiService.getImpactStats().enqueue(new Callback<ImpactStatsResponse>() {
            @Override
            public void onResponse(Call<ImpactStatsResponse> call, Response<ImpactStatsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(Resource.success(response.body()));
                } else {
                    data.setValue(Resource.error("Failed to load impact stats", null));
                }
            }

            @Override
            public void onFailure(Call<ImpactStatsResponse> call, Throwable t) {
                data.setValue(Resource.error(t.getMessage() != null ? t.getMessage() : "Network error", null));
            }
        });

        return data;
    }

    public LiveData<Resource<List<FacilityResponse>>> getFacilities() {
        MutableLiveData<Resource<List<FacilityResponse>>> data = new MutableLiveData<>();
        data.setValue(Resource.loading());

        apiService.getAdminFacilities().enqueue(new Callback<List<FacilityResponse>>() {
            @Override
            public void onResponse(Call<List<FacilityResponse>> call, Response<List<FacilityResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(Resource.success(response.body()));
                } else {
                    data.setValue(Resource.error("Failed to load facilities", null));
                }
            }

            @Override
            public void onFailure(Call<List<FacilityResponse>> call, Throwable t) {
                data.setValue(Resource.error(t.getMessage() != null ? t.getMessage() : "Network error", null));
            }
        });

        return data;
    }

    public LiveData<Resource<FacilityResponse>> verifyFacility(Long id, boolean verified) {
        MutableLiveData<Resource<FacilityResponse>> data = new MutableLiveData<>();
        data.setValue(Resource.loading());

        apiService.verifyFacility(id, new VerifyFacilityRequest(verified)).enqueue(new Callback<FacilityResponse>() {
            @Override
            public void onResponse(Call<FacilityResponse> call, Response<FacilityResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(Resource.success(response.body()));
                } else {
                    data.setValue(Resource.error("Failed to update facility verification", null));
                }
            }

            @Override
            public void onFailure(Call<FacilityResponse> call, Throwable t) {
                data.setValue(Resource.error(t.getMessage() != null ? t.getMessage() : "Network error", null));
            }
        });

        return data;
    }
}
