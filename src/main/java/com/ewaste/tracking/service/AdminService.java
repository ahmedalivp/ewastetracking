package com.ewaste.tracking.service;

import com.ewaste.tracking.dto.admin.FacilityResponse;

import java.util.List;

/**
 * OOP PRINCIPLE: ABSTRACTION
 * 
 * Defines governance operations reserved for platform administrators.
 */
public interface AdminService {

    List<FacilityResponse> getAllFacilities();

    FacilityResponse verifyFacility(Long facilityId, boolean verified);
}
