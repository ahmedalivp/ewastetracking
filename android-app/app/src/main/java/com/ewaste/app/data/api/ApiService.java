package com.ewaste.app.data.api;

import com.ewaste.app.data.model.*;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

/**
 * Retrofit REST client interface reflecting the Spring Boot REST backend endpoints.
 */
public interface ApiService {

    // --- Authentication & Profile ---
    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    @GET("auth/profile")
    Call<UserProfileResponse> getProfile();

    // --- Public Discovery ---
    @GET("categories")
    Call<List<CategoryResponse>> getCategories();

    @GET("drop-off-points")
    Call<List<DropOffPointResponse>> getDropOffPoints();

    @GET("impact")
    Call<ImpactStatsResponse> getImpactStats();

    // --- Consumer Endpoints ---
    @POST("consumer/ewaste-items")
    Call<EWasteItemResponse> submitEWasteItem(@Body SubmitEWasteItemRequest request);

    @GET("consumer/ewaste-items")
    Call<List<EWasteItemResponse>> getMySubmissions();

    @GET("consumer/ewaste-items/{id}/tracking")
    Call<List<TrackingRecordResponse>> getItemTracking(@Path("id") Long id);

    @GET("consumer/credits")
    Call<List<CreditResponse>> getMyCredits();

    @POST("consumer/credits/redeem")
    Call<CreditRedemptionResponse> redeemCredits(@Body RedeemCreditRequest request);

    // --- Facility Staff Endpoints ---
    @GET("facility/ewaste-items")
    Call<List<EWasteItemResponse>> getIntakeQueue(@Query("status") String status);

    @PUT("facility/ewaste-items/{id}/receive")
    Call<EWasteItemResponse> receiveItem(@Path("id") Long id);

    @PUT("facility/ewaste-items/{id}/categorize")
    Call<EWasteItemResponse> categorizeItem(@Path("id") Long id, @Body UpdateCategorizeRequest request);

    @POST("facility/ewaste-items/{id}/components")
    Call<ComponentResponse> harvestComponent(@Path("id") Long id, @Body HarvestComponentRequest request);

    @POST("facility/ewaste-items/{id}/hazardous-materials")
    Call<HazardousMaterialResponse> flagHazardousMaterial(@Path("id") Long id, @Body FlagHazardousMaterialRequest request);

    @PUT("facility/hazardous-materials/{id}/divert")
    Call<HazardousMaterialResponse> divertHazardousMaterial(@Path("id") Long id);

    @PUT("facility/component-requests/{id}/status")
    Call<ComponentRequestResponse> updateComponentRequestStatus(@Path("id") Long id, @Body UpdateComponentRequestStatusRequest request);

    @GET("facility/components")
    Call<List<ComponentResponse>> getFacilityComponents(@Query("category") String category);

    // --- Business Endpoints ---
    @GET("business/components")
    Call<List<ComponentResponse>> getAvailableComponents(@Query("category") String category, @Query("status") String status);

    @POST("business/component-requests")
    Call<ComponentRequestResponse> requestComponent(@Body CreateComponentRequestDto request);

    @GET("business/component-requests")
    Call<List<ComponentRequestResponse>> getMyComponentRequests();

    // --- Admin Oversight Endpoints ---
    @GET("admin/facilities")
    Call<List<FacilityResponse>> getAdminFacilities();

    @PUT("admin/facilities/{id}/verify")
    Call<FacilityResponse> verifyFacility(@Path("id") Long id, @Body VerifyFacilityRequest request);
}
