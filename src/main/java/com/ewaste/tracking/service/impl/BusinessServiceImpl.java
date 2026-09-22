package com.ewaste.tracking.service.impl;

import com.ewaste.tracking.dto.business.ComponentRequestResponse;
import com.ewaste.tracking.dto.business.CreateComponentRequestDto;
import com.ewaste.tracking.dto.facility.ComponentResponse;
import com.ewaste.tracking.entity.BusinessUser;
import com.ewaste.tracking.entity.Component;
import com.ewaste.tracking.entity.ComponentRequest;
import com.ewaste.tracking.enums.ComponentStatus;
import com.ewaste.tracking.enums.EntityType;
import com.ewaste.tracking.enums.RequestStatus;
import com.ewaste.tracking.exception.BadRequestException;
import com.ewaste.tracking.exception.ResourceNotFoundException;
import com.ewaste.tracking.repository.BusinessUserRepository;
import com.ewaste.tracking.repository.ComponentRepository;
import com.ewaste.tracking.repository.ComponentRequestRepository;
import com.ewaste.tracking.service.BusinessService;
import com.ewaste.tracking.service.TrackingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * OOP PRINCIPLE: ABSTRACTION
 * 
 * Implements BusinessService contract for circular component re-use transactions.
 */
@Service
public class BusinessServiceImpl implements BusinessService {

    private final BusinessUserRepository businessUserRepository;
    private final ComponentRepository componentRepository;
    private final ComponentRequestRepository componentRequestRepository;
    private final TrackingService trackingService;

    public BusinessServiceImpl(BusinessUserRepository businessUserRepository,
                               ComponentRepository componentRepository,
                               ComponentRequestRepository componentRequestRepository,
                               TrackingService trackingService) {
        this.businessUserRepository = businessUserRepository;
        this.componentRepository = componentRepository;
        this.componentRequestRepository = componentRequestRepository;
        this.trackingService = trackingService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComponentResponse> getAvailableComponents(String categoryName) {
        List<Component> components;
        if (categoryName != null && !categoryName.trim().isEmpty()) {
            components = componentRepository.findByEwasteItemCategoryNameIgnoreCaseAndStatus(
                    categoryName.trim(), ComponentStatus.AVAILABLE);
        } else {
            components = componentRepository.findByStatus(ComponentStatus.AVAILABLE);
        }

        return components.stream()
                .map(this::mapToComponentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ComponentRequestResponse requestComponent(String businessEmail, CreateComponentRequestDto request) {
        BusinessUser business = businessUserRepository.findByEmail(businessEmail.toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("Business account not found: " + businessEmail));

        Component component = componentRepository.findById(request.getComponentId())
                .orElseThrow(() -> new ResourceNotFoundException("Component not found with ID: " + request.getComponentId()));

        if (component.getStatus() != ComponentStatus.AVAILABLE) {
            throw new BadRequestException("Component is not available for request. Current status: " + component.getStatus());
        }

        // Mark component as REQUESTED
        component.setStatus(ComponentStatus.REQUESTED);
        componentRepository.save(component);

        ComponentRequest componentRequest = new ComponentRequest(business, component);
        ComponentRequest savedRequest = componentRequestRepository.save(componentRequest);

        // Record tracking in immutable audit trail
        trackingService.recordTracking(
                EntityType.COMPONENT,
                component.getId(),
                ComponentStatus.REQUESTED.name(),
                business,
                "Component requested by commercial entity: " + business.getBusinessName()
        );

        return mapToComponentRequestResponse(savedRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComponentRequestResponse> getMyComponentRequests(String businessEmail) {
        BusinessUser business = businessUserRepository.findByEmail(businessEmail.toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("Business account not found: " + businessEmail));

        return componentRequestRepository.findByBusinessIdOrderByRequestedAtDesc(business.getId()).stream()
                .map(this::mapToComponentRequestResponse)
                .collect(Collectors.toList());
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
