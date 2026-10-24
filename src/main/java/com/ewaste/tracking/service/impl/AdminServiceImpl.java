package com.ewaste.tracking.service.impl;

import com.ewaste.tracking.dto.admin.*;
import com.ewaste.tracking.dto.common.DropOffPointResponse;
import com.ewaste.tracking.entity.DropOffPoint;
import com.ewaste.tracking.entity.FacilityStaff;
import com.ewaste.tracking.entity.RecyclingFacility;
import com.ewaste.tracking.exception.BadRequestException;
import com.ewaste.tracking.exception.ResourceNotFoundException;
import com.ewaste.tracking.repository.DropOffPointRepository;
import com.ewaste.tracking.repository.EWasteItemRepository;
import com.ewaste.tracking.repository.FacilityStaffRepository;
import com.ewaste.tracking.repository.RecyclingFacilityRepository;
import com.ewaste.tracking.service.AdminService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * OOP PRINCIPLE: ABSTRACTION & ENCAPSULATION
 * 
 * Implements AdminService for facility auditing, drop-off location provisioning,
 * and center staff access governance.
 */
@Service
public class AdminServiceImpl implements AdminService {

    private final RecyclingFacilityRepository facilityRepository;
    private final DropOffPointRepository dropOffPointRepository;
    private final FacilityStaffRepository facilityStaffRepository;
    private final EWasteItemRepository ewasteItemRepository;

    public AdminServiceImpl(RecyclingFacilityRepository facilityRepository,
                            DropOffPointRepository dropOffPointRepository,
                            FacilityStaffRepository facilityStaffRepository,
                            EWasteItemRepository ewasteItemRepository) {
        this.facilityRepository = facilityRepository;
        this.dropOffPointRepository = dropOffPointRepository;
        this.facilityStaffRepository = facilityStaffRepository;
        this.ewasteItemRepository = ewasteItemRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FacilityResponse> getAllFacilities() {
        return facilityRepository.findAll().stream()
                .map(this::mapFacility)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FacilityResponse getFacilityById(Long facilityId) {
        RecyclingFacility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Recycling facility not found with ID: " + facilityId));
        return mapFacility(facility);
    }

    @Override
    @Transactional
    public FacilityResponse createFacility(CreateFacilityRequest request) {
        RecyclingFacility facility = new RecyclingFacility(
                request.getName().trim(),
                request.getAddress().trim(),
                request.getLat(),
                request.getLng(),
                request.isVerified(),
                request.getContactPhone() != null ? request.getContactPhone().trim() : null
        );
        RecyclingFacility saved = facilityRepository.save(facility);
        return mapFacility(saved);
    }

    @Override
    @Transactional
    public FacilityResponse updateFacility(Long facilityId, UpdateFacilityRequest request) {
        RecyclingFacility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Recycling facility not found with ID: " + facilityId));

        facility.setName(request.getName().trim());
        facility.setAddress(request.getAddress().trim());
        facility.setLat(request.getLat());
        facility.setLng(request.getLng());
        facility.setVerifiedByAdmin(request.isVerified());
        facility.setActive(request.isActive());
        if (request.getContactPhone() != null) {
            facility.setContactPhone(request.getContactPhone().trim());
        }

        RecyclingFacility updated = facilityRepository.save(facility);
        return mapFacility(updated);
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

    @Override
    @Transactional
    public void deleteFacility(Long facilityId) {
        RecyclingFacility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Recycling facility not found with ID: " + facilityId));

        // Check if there are linked drop-off points
        List<DropOffPoint> dropOffs = dropOffPointRepository.findByFacilityId(facilityId);
        if (!dropOffs.isEmpty()) {
            dropOffPointRepository.deleteAll(dropOffs);
        }

        facilityRepository.delete(facility);
    }

    @Override
    @Transactional
    public DropOffPointResponse createDropOffPoint(CreateDropOffPointRequest request) {
        RecyclingFacility facility = facilityRepository.findById(request.getFacilityId())
                .orElseThrow(() -> new ResourceNotFoundException("Parent facility not found with ID: " + request.getFacilityId()));

        DropOffPoint point = new DropOffPoint(
                facility,
                request.getLabel().trim(),
                request.getLat(),
                request.getLng(),
                request.getAddress() != null ? request.getAddress().trim() : null,
                request.getImageUrl() != null ? request.getImageUrl().trim() : null,
                request.getOperatingHours() != null ? request.getOperatingHours().trim() : "Mon-Sat 8:00 AM - 6:00 PM",
                request.getContactPhone() != null ? request.getContactPhone().trim() : null,
                request.getAcceptedTypes() != null ? request.getAcceptedTypes().trim() : "All Categories"
        );

        DropOffPoint saved = dropOffPointRepository.save(point);
        return mapDropOffPoint(saved);
    }

    @Override
    @Transactional
    public DropOffPointResponse updateDropOffPoint(Long id, UpdateDropOffPointRequest request) {
        DropOffPoint point = dropOffPointRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Drop-off point not found with ID: " + id));

        if (request.getFacilityId() != null && !request.getFacilityId().equals(point.getFacility().getId())) {
            RecyclingFacility newFacility = facilityRepository.findById(request.getFacilityId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent facility not found with ID: " + request.getFacilityId()));
            point.setFacility(newFacility);
        }

        point.setLabel(request.getLabel().trim());
        point.setLat(request.getLat());
        point.setLng(request.getLng());
        if (request.getAddress() != null) point.setAddress(request.getAddress().trim());
        if (request.getImageUrl() != null) point.setImageUrl(request.getImageUrl().trim());
        if (request.getOperatingHours() != null) point.setOperatingHours(request.getOperatingHours().trim());
        if (request.getContactPhone() != null) point.setContactPhone(request.getContactPhone().trim());
        if (request.getAcceptedTypes() != null) point.setAcceptedTypes(request.getAcceptedTypes().trim());

        DropOffPoint updated = dropOffPointRepository.save(point);
        return mapDropOffPoint(updated);
    }

    @Override
    @Transactional
    public void deleteDropOffPoint(Long id) {
        DropOffPoint point = dropOffPointRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Drop-off point not found with ID: " + id));
        dropOffPointRepository.delete(point);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffMemberResponse> getAllStaff(Long facilityId) {
        List<FacilityStaff> staffList;
        if (facilityId != null) {
            staffList = facilityStaffRepository.findByFacilityId(facilityId);
        } else {
            staffList = facilityStaffRepository.findAll();
        }

        return staffList.stream()
                .map(this::mapStaff)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StaffMemberResponse updateStaffAccess(Long staffId, UpdateStaffAccessRequest request) {
        FacilityStaff staff = facilityStaffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Facility staff not found with ID: " + staffId));

        if (request.getFacilityId() != null && !request.getFacilityId().equals(staff.getFacility().getId())) {
            RecyclingFacility facility = facilityRepository.findById(request.getFacilityId())
                    .orElseThrow(() -> new ResourceNotFoundException("Target facility not found with ID: " + request.getFacilityId()));
            staff.setFacility(facility);
        }

        if (request.getActive() != null) {
            staff.setActive(request.getActive());
        }

        FacilityStaff updated = facilityStaffRepository.save(staff);
        return mapStaff(updated);
    }

    private FacilityResponse mapFacility(RecyclingFacility f) {
        int staffCount = facilityStaffRepository.findByFacilityId(f.getId()).size();
        int dropOffCount = dropOffPointRepository.findByFacilityId(f.getId()).size();
        return new FacilityResponse(
                f.getId(),
                f.getName(),
                f.getAddress(),
                f.getLat(),
                f.getLng(),
                f.isVerifiedByAdmin(),
                f.isActive(),
                f.getContactPhone(),
                staffCount,
                dropOffCount
        );
    }

    private DropOffPointResponse mapDropOffPoint(DropOffPoint d) {
        return new DropOffPointResponse(
                d.getId(),
                d.getLabel(),
                d.getLat(),
                d.getLng(),
                d.getFacility() != null ? d.getFacility().getId() : null,
                d.getFacility() != null ? d.getFacility().getName() : "Unassigned",
                d.getAddress(),
                d.getImageUrl(),
                d.getOperatingHours(),
                d.getContactPhone(),
                d.getAcceptedTypes()
        );
    }

    private StaffMemberResponse mapStaff(FacilityStaff s) {
        return new StaffMemberResponse(
                s.getId(),
                s.getFullName(),
                s.getEmail(),
                s.getFacility() != null ? s.getFacility().getId() : null,
                s.getFacility() != null ? s.getFacility().getName() : "Unassigned",
                s.isActive(),
                s.getCreatedAt()
        );
    }
}
