package com.ewaste.app.ui.consumer;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.ewaste.app.EWasteApplication;
import com.ewaste.app.data.model.*;
import com.ewaste.app.data.repository.AuthRepository;
import com.ewaste.app.data.repository.ConsumerRepository;
import com.ewaste.app.ui.common.Resource;

import java.util.List;

public class ConsumerViewModel extends AndroidViewModel {

    private final ConsumerRepository consumerRepository;
    private final AuthRepository authRepository;

    private final MutableLiveData<Resource<List<EWasteItemResponse>>> submissionsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<CreditResponse>>> creditsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Resource<UserProfileResponse>> profileLiveData = new MutableLiveData<>();

    public ConsumerViewModel(@NonNull Application application) {
        super(application);
        this.consumerRepository = new ConsumerRepository();
        this.authRepository = new AuthRepository(EWasteApplication.getInstance().getSessionManager());
    }

    public LiveData<Resource<List<CategoryResponse>>> getCategories() {
        return consumerRepository.getCategories();
    }

    public LiveData<Resource<List<DropOffPointResponse>>> getDropOffPoints() {
        return consumerRepository.getDropOffPoints();
    }

    public LiveData<Resource<EWasteItemResponse>> submitItem(Long categoryId, Long dropOffPointId, String description) {
        SubmitEWasteItemRequest request = new SubmitEWasteItemRequest(categoryId, dropOffPointId, description);
        return consumerRepository.submitItem(request);
    }

    public LiveData<Resource<List<EWasteItemResponse>>> getMySubmissions() {
        return consumerRepository.getMySubmissions();
    }

    public LiveData<Resource<List<TrackingRecordResponse>>> getItemTracking(Long itemId) {
        return consumerRepository.getItemTracking(itemId);
    }

    public LiveData<Resource<List<CreditResponse>>> getMyCredits() {
        return consumerRepository.getMyCredits();
    }

    public LiveData<Resource<CreditRedemptionResponse>> redeemCredits(int amount, String redeemedFor) {
        RedeemCreditRequest request = new RedeemCreditRequest(amount, redeemedFor);
        return consumerRepository.redeemCredits(request);
    }

    public LiveData<Resource<UserProfileResponse>> getProfile() {
        return authRepository.getProfile();
    }
}
