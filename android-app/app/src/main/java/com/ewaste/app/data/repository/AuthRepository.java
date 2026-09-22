package com.ewaste.app.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.ewaste.app.data.api.ApiClient;
import com.ewaste.app.data.api.ApiService;
import com.ewaste.app.data.local.SessionManager;
import com.ewaste.app.data.model.*;
import com.ewaste.app.ui.common.Resource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class AuthRepository {

    private final ApiService apiService;
    private final SessionManager sessionManager;

    public AuthRepository(SessionManager sessionManager) {
        this.apiService = ApiClient.getApiService();
        this.sessionManager = sessionManager;
    }

    public LiveData<Resource<AuthResponse>> login(String email, String password) {
        MutableLiveData<Resource<AuthResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        LoginRequest request = new LoginRequest(email, password);
        apiService.login(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse auth = response.body();
                    sessionManager.saveSession(
                            auth.getToken(),
                            auth.getId(),
                            auth.getEmail(),
                            auth.getFullName(),
                            auth.getRole(),
                            auth.getDashboardSummary()
                    );
                    result.setValue(Resource.success(auth));
                } else {
                    String errorMsg = "Login failed: Invalid credentials or server error (" + response.code() + ")";
                    result.setValue(Resource.error(errorMsg, null));
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                result.setValue(Resource.error("Network connection error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<AuthResponse>> register(RegisterRequest request) {
        MutableLiveData<Resource<AuthResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.register(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse auth = response.body();
                    sessionManager.saveSession(
                            auth.getToken(),
                            auth.getId(),
                            auth.getEmail(),
                            auth.getFullName(),
                            auth.getRole(),
                            auth.getDashboardSummary()
                    );
                    result.setValue(Resource.success(auth));
                } else {
                    String errorMsg = "Registration failed: (" + response.code() + ") Please verify details";
                    result.setValue(Resource.error(errorMsg, null));
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                result.setValue(Resource.error("Network connection error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<UserProfileResponse>> getProfile() {
        MutableLiveData<Resource<UserProfileResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.getProfile().enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfileResponse profile = response.body();
                    sessionManager.setDashboardSummary(profile.getDashboardSummary());
                    result.setValue(Resource.success(profile));
                } else {
                    result.setValue(Resource.error("Failed to load profile (" + response.code() + ")", null));
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
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
                } else {
                    result.setValue(Resource.error("Failed to fetch drop-off locations", null));
                }
            }

            @Override
            public void onFailure(Call<List<DropOffPointResponse>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }
}
