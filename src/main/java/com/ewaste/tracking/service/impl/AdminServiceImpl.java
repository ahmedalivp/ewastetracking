package com.ewaste.tracking.service.impl;

import com.ewaste.tracking.dto.admin.FacilityResponse;
import com.ewaste.tracking.entity.RecyclingFacility;
import com.ewaste.tracking.exception.ResourceNotFoundException;
import com.ewaste.tracking.repository.RecyclingFacilityRepository;
import com.ewaste.tracking.service.AdminService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * OOP PRINCIPLE: ABSTRACTION
 * 
 * Implements AdminService for facility auditing and verification.
 */
@Service
public class AdminServiceImpl implements AdminService {

    private final RecyclingFacilityRepository facilityRepository;

    public AdminServiceImpl(RecyclingFacilityRepository facilityRepository) {
        this.facilityRepository = facilityRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FacilityResponse> getAllFacilities() {
        return facilityRepository.findAll().stream()
                .map(this::mapFacility)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FacilityResponse verifyFacility(Long facilityId, boolean verified) {
        RecyclingFacility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Recycling facility not found with ID: " + facilityId));

        facility.setVerifiedByAdmin(verified);
        RecyclingFacility updated = facilityRepository.save(facility);

        return mapFacility(updated);
    }

    private FacilityResponse mapFacility(RecyclingFacility f) {
        return new FacilityResponse(
                f.getId(),
                f.getName(),
                f.getAddress(),
                f.getLat(),
                f.getLng(),
                f.isVerifiedByAdmin()
        );
    }
}
