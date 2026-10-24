package com.ewaste.tracking.service;

import com.ewaste.tracking.dto.admin.*;
import com.ewaste.tracking.dto.common.DropOffPointResponse;

import java.util.List;

/**
 * OOP PRINCIPLE: ABSTRACTION
 * 
 * Defines governance operations reserved for platform administrators.
 */
public interface AdminService {

    List<FacilityResponse> getAllFacilities();

    FacilityResponse getFacilityById(Long facilityId);

    FacilityResponse createFacility(CreateFacilityRequest request);

    FacilityResponse updateFacility(Long facilityId, UpdateFacilityRequest request);

    FacilityResponse verifyFacility(Long facilityId, boolean verified);

    void deleteFacility(Long facilityId);

    DropOffPointResponse createDropOffPoint(CreateDropOffPointRequest request);

    DropOffPointResponse updateDropOffPoint(Long id, UpdateDropOffPointRequest request);

    void deleteDropOffPoint(Long id);

    List<StaffMemberResponse> getAllStaff(Long facilityId);

    StaffMemberResponse updateStaffAccess(Long staffId, UpdateStaffAccessRequest request);
}
