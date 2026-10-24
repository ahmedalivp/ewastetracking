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

    public LiveData<Resource<FacilityResponse>> createFacility(CreateFacilityRequest request) {
        MutableLiveData<Resource<FacilityResponse>> data = new MutableLiveData<>();
        data.setValue(Resource.loading());

        apiService.createFacility(request).enqueue(new Callback<FacilityResponse>() {
            @Override
            public void onResponse(Call<FacilityResponse> call, Response<FacilityResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(Resource.success(response.body()));
                } else {
                    data.setValue(Resource.error("Failed to create facility (" + response.code() + ")", null));
                }
            }

            @Override
            public void onFailure(Call<FacilityResponse> call, Throwable t) {
                data.setValue(Resource.error(t.getMessage() != null ? t.getMessage() : "Network error", null));
            }
        });

        return data;
    }

    public LiveData<Resource<FacilityResponse>> updateFacility(Long id, UpdateFacilityRequest request) {
        MutableLiveData<Resource<FacilityResponse>> data = new MutableLiveData<>();
        data.setValue(Resource.loading());

        apiService.updateFacility(id, request).enqueue(new Callback<FacilityResponse>() {
            @Override
            public void onResponse(Call<FacilityResponse> call, Response<FacilityResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(Resource.success(response.body()));
                } else {
                    data.setValue(Resource.error("Failed to update facility (" + response.code() + ")", null));
                }
            }

            @Override
            public void onFailure(Call<FacilityResponse> call, Throwable t) {
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

    public LiveData<Resource<Void>> deleteFacility(Long id) {
        MutableLiveData<Resource<Void>> data = new MutableLiveData<>();
        data.setValue(Resource.loading());

        apiService.deleteFacility(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    data.setValue(Resource.success(null));
                } else {
                    data.setValue(Resource.error("Failed to delete facility (" + response.code() + ")", null));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                data.setValue(Resource.error(t.getMessage() != null ? t.getMessage() : "Network error", null));
            }
        });

        return data;
    }

    public LiveData<Resource<List<DropOffPointResponse>>> getDropOffPoints() {
        MutableLiveData<Resource<List<DropOffPointResponse>>> data = new MutableLiveData<>();
        data.setValue(Resource.loading());

        apiService.getDropOffPoints().enqueue(new Callback<List<DropOffPointResponse>>() {
            @Override
            public void onResponse(Call<List<DropOffPointResponse>> call, Response<List<DropOffPointResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(Resource.success(response.body()));
                } else {
                    data.setValue(Resource.error("Failed to load drop-off locations", null));
                }
            }

            @Override
            public void onFailure(Call<List<DropOffPointResponse>> call, Throwable t) {
                data.setValue(Resource.error(t.getMessage() != null ? t.getMessage() : "Network error", null));
            }
        });

        return data;
    }

    public LiveData<Resource<DropOffPointResponse>> createDropOffPoint(CreateDropOffPointRequest request) {
        MutableLiveData<Resource<DropOffPointResponse>> data = new MutableLiveData<>();
        data.setValue(Resource.loading());

        apiService.createDropOffPoint(request).enqueue(new Callback<DropOffPointResponse>() {
            @Override
            public void onResponse(Call<DropOffPointResponse> call, Response<DropOffPointResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(Resource.success(response.body()));
                } else {
                    data.setValue(Resource.error("Failed to create drop-off point (" + response.code() + ")", null));
                }
            }

            @Override
            public void onFailure(Call<DropOffPointResponse> call, Throwable t) {
                data.setValue(Resource.error(t.getMessage() != null ? t.getMessage() : "Network error", null));
            }
        });

        return data;
    }

    public LiveData<Resource<DropOffPointResponse>> updateDropOffPoint(Long id, UpdateDropOffPointRequest request) {
        MutableLiveData<Resource<DropOffPointResponse>> data = new MutableLiveData<>();
        data.setValue(Resource.loading());

        apiService.updateDropOffPoint(id, request).enqueue(new Callback<DropOffPointResponse>() {
            @Override
            public void onResponse(Call<DropOffPointResponse> call, Response<DropOffPointResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(Resource.success(response.body()));
                } else {
                    data.setValue(Resource.error("Failed to update drop-off point (" + response.code() + ")", null));
                }
            }

            @Override
            public void onFailure(Call<DropOffPointResponse> call, Throwable t) {
                data.setValue(Resource.error(t.getMessage() != null ? t.getMessage() : "Network error", null));
            }
        });

        return data;
    }

    public LiveData<Resource<Void>> deleteDropOffPoint(Long id) {
        MutableLiveData<Resource<Void>> data = new MutableLiveData<>();
        data.setValue(Resource.loading());

        apiService.deleteDropOffPoint(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    data.setValue(Resource.success(null));
                } else {
                    data.setValue(Resource.error("Failed to delete drop-off point (" + response.code() + ")", null));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                data.setValue(Resource.error(t.getMessage() != null ? t.getMessage() : "Network error", null));
            }
        });

        return data;
    }

    public LiveData<Resource<List<StaffMemberResponse>>> getAdminStaff(Long facilityId) {
        MutableLiveData<Resource<List<StaffMemberResponse>>> data = new MutableLiveData<>();
        data.setValue(Resource.loading());

        apiService.getAdminStaff(facilityId).enqueue(new Callback<List<StaffMemberResponse>>() {
            @Override
            public void onResponse(Call<List<StaffMemberResponse>> call, Response<List<StaffMemberResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(Resource.success(response.body()));
                } else {
                    data.setValue(Resource.error("Failed to load staff list", null));
                }
            }

            @Override
            public void onFailure(Call<List<StaffMemberResponse>> call, Throwable t) {
                data.setValue(Resource.error(t.getMessage() != null ? t.getMessage() : "Network error", null));
            }
        });

        return data;
    }

    public LiveData<Resource<StaffMemberResponse>> updateStaffAccess(Long id, UpdateStaffAccessRequest request) {
        MutableLiveData<Resource<StaffMemberResponse>> data = new MutableLiveData<>();
        data.setValue(Resource.loading());

        apiService.updateStaffAccess(id, request).enqueue(new Callback<StaffMemberResponse>() {
            @Override
            public void onResponse(Call<StaffMemberResponse> call, Response<StaffMemberResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(Resource.success(response.body()));
                } else {
                    data.setValue(Resource.error("Failed to update staff access (" + response.code() + ")", null));
                }
            }

            @Override
            public void onFailure(Call<StaffMemberResponse> call, Throwable t) {
                data.setValue(Resource.error(t.getMessage() != null ? t.getMessage() : "Network error", null));
            }
        });

        return data;
    }
}
