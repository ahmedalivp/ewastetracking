package com.ewaste.app.ui.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.ewaste.app.data.model.*;
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

    public LiveData<Resource<FacilityResponse>> createFacility(CreateFacilityRequest request) {
        return repository.createFacility(request);
    }

    public LiveData<Resource<FacilityResponse>> updateFacility(Long id, UpdateFacilityRequest request) {
        return repository.updateFacility(id, request);
    }

    public LiveData<Resource<FacilityResponse>> verifyFacility(Long id, boolean verified) {
        return repository.verifyFacility(id, verified);
    }

    public LiveData<Resource<Void>> deleteFacility(Long id) {
        return repository.deleteFacility(id);
    }

    public LiveData<Resource<List<DropOffPointResponse>>> getDropOffPoints() {
        return repository.getDropOffPoints();
    }

    public LiveData<Resource<DropOffPointResponse>> createDropOffPoint(CreateDropOffPointRequest request) {
        return repository.createDropOffPoint(request);
    }

    public LiveData<Resource<DropOffPointResponse>> updateDropOffPoint(Long id, UpdateDropOffPointRequest request) {
        return repository.updateDropOffPoint(id, request);
    }

    public LiveData<Resource<Void>> deleteDropOffPoint(Long id) {
        return repository.deleteDropOffPoint(id);
    }

    public LiveData<Resource<List<StaffMemberResponse>>> getAdminStaff(Long facilityId) {
        return repository.getAdminStaff(facilityId);
    }

    public LiveData<Resource<StaffMemberResponse>> updateStaffAccess(Long id, UpdateStaffAccessRequest request) {
        return repository.updateStaffAccess(id, request);
    }
}
