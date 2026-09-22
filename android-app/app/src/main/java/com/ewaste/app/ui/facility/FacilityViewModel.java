package com.ewaste.app.ui.facility;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.ewaste.app.data.model.*;
import com.ewaste.app.data.repository.ConsumerRepository;
import com.ewaste.app.data.repository.FacilityRepository;
import com.ewaste.app.ui.common.Resource;

import java.util.List;

public class FacilityViewModel extends AndroidViewModel {

    private final FacilityRepository facilityRepository;
    private final ConsumerRepository consumerRepository;

    public FacilityViewModel(@NonNull Application application) {
        super(application);
        this.facilityRepository = new FacilityRepository();
        this.consumerRepository = new ConsumerRepository();
    }

    public LiveData<Resource<List<EWasteItemResponse>>> getIntakeQueue(String status) {
        return facilityRepository.getIntakeQueue(status);
    }

    public LiveData<Resource<EWasteItemResponse>> receiveItem(Long itemId) {
        return facilityRepository.receiveItem(itemId);
    }

    public LiveData<Resource<EWasteItemResponse>> categorizeItem(Long itemId, Long categoryId, String notes) {
        UpdateCategorizeRequest request = new UpdateCategorizeRequest(categoryId, notes);
        return facilityRepository.categorizeItem(itemId, request);
    }

    public LiveData<Resource<ComponentResponse>> harvestComponent(Long itemId, String name, String type, String condition) {
        HarvestComponentRequest request = new HarvestComponentRequest(name, type, condition);
        return facilityRepository.harvestComponent(itemId, request);
    }

    public LiveData<Resource<HazardousMaterialResponse>> flagHazardousMaterial(Long itemId, String materialType, String instructions) {
        FlagHazardousMaterialRequest request = new FlagHazardousMaterialRequest(materialType);
        return facilityRepository.flagHazardousMaterial(itemId, request);
    }

    public LiveData<Resource<HazardousMaterialResponse>> divertHazardousMaterial(Long materialId) {
        return facilityRepository.divertHazardousMaterial(materialId);
    }

    public LiveData<Resource<List<ComponentResponse>>> getInventoryComponents() {
        return facilityRepository.getInventoryComponents();
    }

    public LiveData<Resource<List<CategoryResponse>>> getCategories() {
        return consumerRepository.getCategories();
    }

    public LiveData<Resource<List<TrackingRecordResponse>>> getItemTracking(Long itemId) {
        return consumerRepository.getItemTracking(itemId);
    }
}
