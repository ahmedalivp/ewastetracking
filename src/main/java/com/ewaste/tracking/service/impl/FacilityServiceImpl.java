package com.ewaste.tracking.service.impl;

import com.ewaste.tracking.dto.business.ComponentRequestResponse;
import com.ewaste.tracking.dto.consumer.EWasteItemResponse;
import com.ewaste.tracking.dto.facility.*;
import com.ewaste.tracking.entity.*;
import com.ewaste.tracking.enums.*;
import com.ewaste.tracking.exception.BadRequestException;
import com.ewaste.tracking.exception.ResourceNotFoundException;
import com.ewaste.tracking.repository.*;
import com.ewaste.tracking.service.CreditService;
import com.ewaste.tracking.service.FacilityService;
import com.ewaste.tracking.service.TrackingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * OOP PRINCIPLE: ABSTRACTION & COMPOSITION
 * 
 * - Abstraction: Concrete implementation of FacilityService.
 * - Composition: Operates directly upon composed EWasteItem structures,
 *   adding harvested Component entities and flagged HazardousMaterial objects.
 */
@Service
public class FacilityServiceImpl implements FacilityService {

    private final FacilityStaffRepository staffRepository;
    private final EWasteItemRepository ewasteItemRepository;
    private final EWasteCategoryRepository categoryRepository;
    private final ComponentRepository componentRepository;
    private final HazardousMaterialRepository hazardousMaterialRepository;
    private final ComponentRequestRepository componentRequestRepository;
    private final TrackingService trackingService;
    private final CreditService creditService;

    public FacilityServiceImpl(FacilityStaffRepository staffRepository,
                               EWasteItemRepository ewasteItemRepository,
                               EWasteCategoryRepository categoryRepository,
                               ComponentRepository componentRepository,
                               HazardousMaterialRepository hazardousMaterialRepository,
                               ComponentRequestRepository componentRequestRepository,
                               TrackingService trackingService,
                               CreditService creditService) {
        this.staffRepository = staffRepository;
        this.ewasteItemRepository = ewasteItemRepository;
        this.categoryRepository = categoryRepository;
        this.componentRepository = componentRepository;
        this.hazardousMaterialRepository = hazardousMaterialRepository;
        this.componentRequestRepository = componentRequestRepository;
        this.trackingService = trackingService;
        this.creditService = creditService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EWasteItemResponse> getIntakeQueue(EWasteStatus status) {
        List<EWasteItem> items;
        if (status != null) {
            items = ewasteItemRepository.findByStatusOrderBySubmittedAtDesc(status);
        } else {
            items = ewasteItemRepository.findAllByOrderBySubmittedAtDesc();
        }
        return items.stream().map(this::mapToItemResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EWasteItemResponse receiveItem(String staffEmail, Long itemId) {
        FacilityStaff staff = getStaffByEmail(staffEmail);
        EWasteItem item = getItemById(itemId);

        item.setStatus(EWasteStatus.RECEIVED);
        EWasteItem updated = ewasteItemRepository.save(item);

        trackingService.recordTracking(
                EntityType.EWASTE_ITEM,
                item.getId(),
                EWasteStatus.RECEIVED.name(),
                staff,
                "Item received and checked in at facility: " + (staff.getFacility() != null ? staff.getFacility().getName() : "Facility")
        );

        return mapToItemResponse(updated);
    }

    @Override
    @Transactional
    public EWasteItemResponse categorizeItem(String staffEmail, Long itemId, UpdateCategorizeRequest request) {
        FacilityStaff staff = getStaffByEmail(staffEmail);
        EWasteItem item = getItemById(itemId);

        EWasteCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));

        item.setCategory(category);
        item.setStatus(EWasteStatus.CATEGORIZED);
        EWasteItem updated = ewasteItemRepository.save(item);

        String notes = "Item categorized as " + category.getName();
        if (request.getTriageNotes() != null && !request.getTriageNotes().isBlank()) {
            notes += " | Notes: " + request.getTriageNotes();
        }

        trackingService.recordTracking(
                EntityType.EWASTE_ITEM,
                item.getId(),
                EWasteStatus.CATEGORIZED.name(),
                staff,
                notes
        );

        return mapToItemResponse(updated);
    }

    @Override
    @Transactional
    public ComponentResponse harvestComponent(String staffEmail, Long itemId, HarvestComponentRequest request) {
        FacilityStaff staff = getStaffByEmail(staffEmail);
        EWasteItem item = getItemById(itemId);

        // Advance item status to PROCESSED
        item.setStatus(EWasteStatus.PROCESSED);

        Component component = new Component(
                item,
                request.getName().trim(),
                request.getType().trim(),
                request.getCondition(),
                ComponentStatus.AVAILABLE
        );

        // Composition: add component to item's list
        item.addComponent(component);
        ewasteItemRepository.save(item);
        Component savedComponent = componentRepository.save(component);

        // Record tracking for the harvested component
        trackingService.recordTracking(
                EntityType.COMPONENT,
                savedComponent.getId(),
                ComponentStatus.AVAILABLE.name(),
                staff,
                String.format("Harvested component [%s - %s] condition: %s",
                        savedComponent.getName(), savedComponent.getType(), savedComponent.getCondition())
        );

        // Record tracking for parent e-waste item
        trackingService.recordTracking(
                EntityType.EWASTE_ITEM,
                item.getId(),
                EWasteStatus.PROCESSED.name(),
                staff,
                "Component salvaged: " + savedComponent.getName()
        );

        // Auto-award recycling credits to the consumer using the Strategy pattern
        creditService.awardRecyclingCredits(
                item,
                "Recycling credit for salvaged component: " + savedComponent.getName()
        );

        return mapToComponentResponse(savedComponent);
    }

    @Override
    @Transactional
    public HazardousMaterialResponse flagHazardousMaterial(String staffEmail, Long itemId, FlagHazardousMaterialRequest request) {
        FacilityStaff staff = getStaffByEmail(staffEmail);
        EWasteItem item = getItemById(itemId);

        HazardousMaterial material = new HazardousMaterial(item, request.getType(), DiversionStatus.FLAGGED);
        item.addHazardousMaterial(material);
        ewasteItemRepository.save(item);
        HazardousMaterial savedMaterial = hazardousMaterialRepository.save(material);

        trackingService.recordTracking(
                EntityType.EWASTE_ITEM,
                item.getId(),
                item.getStatus().name(),
                staff,
                "Flagged hazardous substance: " + request.getType()
        );

        return new HazardousMaterialResponse(
                savedMaterial.getId(),
                item.getId(),
                savedMaterial.getType(),
                savedMaterial.getDiversionStatus()
        );
    }

    @Override
    @Transactional
    public HazardousMaterialResponse divertHazardousMaterial(String staffEmail, Long materialId) {
        FacilityStaff staff = getStaffByEmail(staffEmail);
        HazardousMaterial material = hazardousMaterialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Hazardous material not found with ID: " + materialId));

        material.setDiversionStatus(DiversionStatus.DIVERTED);
        HazardousMaterial updated = hazardousMaterialRepository.save(material);

        EWasteItem item = material.getEwasteItem();
        trackingService.recordTracking(
                EntityType.EWASTE_ITEM,
                item.getId(),
                item.getStatus().name(),
                staff,
                "Hazardous substance safely diverted from landfill: " + material.getType()
        );

        return new HazardousMaterialResponse(
                updated.getId(),
                item.getId(),
                updated.getType(),
                updated.getDiversionStatus()
        );
    }

    @Override
    @Transactional
    public ComponentRequestResponse updateComponentRequestStatus(String staffEmail, Long requestId, UpdateComponentRequestStatusRequest request) {
        FacilityStaff staff = getStaffByEmail(staffEmail);
        ComponentRequest componentRequest = componentRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Component request not found with ID: " + requestId));

        RequestStatus newStatus = request.getStatus();
        componentRequest.setStatus(newStatus);

        Component component = componentRequest.getComponent();
        if (newStatus == RequestStatus.FULFILLED) {
            component.setStatus(ComponentStatus.FULFILLED);
            componentRepository.save(component);
        } else if (newStatus == RequestStatus.REJECTED) {
            component.setStatus(ComponentStatus.AVAILABLE);
            componentRepository.save(component);
        }

        ComponentRequest updatedRequest = componentRequestRepository.save(componentRequest);

        trackingService.recordTracking(
                EntityType.COMPONENT,
                component.getId(),
                component.getStatus().name(),
                staff,
                "Component request status updated to: " + newStatus
        );

        return mapToComponentRequestResponse(updatedRequest);
    }

    private FacilityStaff getStaffByEmail(String email) {
        return staffRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("Facility staff account not found: " + email));
    }

    private EWasteItem getItemById(Long id) {
        return ewasteItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EWasteItem not found with ID: " + id));
    }

    private EWasteItemResponse mapToItemResponse(EWasteItem item) {
        EWasteItemResponse response = new EWasteItemResponse();
        response.setId(item.getId());
        if (item.getConsumer() != null) {
            response.setConsumerId(item.getConsumer().getId());
            response.setConsumerName(item.getConsumer().getFullName());
        }
        if (item.getCategory() != null) {
            response.setCategoryId(item.getCategory().getId());
            response.setCategoryName(item.getCategory().getName());
        }
        if (item.getDropOffPoint() != null) {
            response.setDropOffPointId(item.getDropOffPoint().getId());
            response.setDropOffPointLabel(item.getDropOffPoint().getLabel());
        }
        response.setDeviceDescription(item.getDeviceDescription());
        response.setStatus(item.getStatus());
        response.setSubmittedAt(item.getSubmittedAt());
        response.setComponentCount(item.getComponents() != null ? item.getComponents().size() : 0);
        response.setHazardousMaterialCount(item.getHazardousMaterials() != null ? item.getHazardousMaterials().size() : 0);
        return response;
    }

    private ComponentResponse mapToComponentResponse(Component component) {
        String categoryName = (component.getEwasteItem() != null && component.getEwasteItem().getCategory() != null)
                ? component.getEwasteItem().getCategory().getName() : null;

        return new ComponentResponse(
                component.getId(),
                component.getEwasteItem() != null ? component.getEwasteItem().getId() : null,
                categoryName,
                component.getName(),
                component.getType(),
                component.getCondition(),
                component.getStatus(),
                component.getHarvestedAt()
        );
    }

    private ComponentRequestResponse mapToComponentRequestResponse(ComponentRequest req) {
        return new ComponentRequestResponse(
                req.getId(),
                req.getBusiness().getId(),
                req.getBusiness().getBusinessName(),
                req.getComponent().getId(),
                req.getComponent().getName(),
                req.getComponent().getType(),
                req.getComponent().getCondition(),
                req.getStatus(),
                req.getRequestedAt()
        );
    }
}
