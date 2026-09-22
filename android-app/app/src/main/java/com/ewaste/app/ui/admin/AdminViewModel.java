package com.ewaste.app.ui.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.ewaste.app.data.model.FacilityResponse;
import com.ewaste.app.data.model.ImpactStatsResponse;
import com.ewaste.app.data.repository.AdminRepository;
import com.ewaste.app.ui.common.Resource;

import java.util.List;

public class AdminViewModel extends ViewModel {

    private final AdminRepository repository;

    public AdminViewModel() {
        this.repository = new AdminRepository();
    }

    public LiveData<Resource<ImpactStatsResponse>> getImpactStats() {
        return repository.getImpactStats();
    }

    public LiveData<Resource<List<FacilityResponse>>> getFacilities() {
        return repository.getFacilities();
    }

    public LiveData<Resource<FacilityResponse>> verifyFacility(Long id, boolean verified) {
        return repository.verifyFacility(id, verified);
    }
}
