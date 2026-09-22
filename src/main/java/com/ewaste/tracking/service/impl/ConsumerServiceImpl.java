package com.ewaste.tracking.service.impl;

import com.ewaste.tracking.dto.consumer.*;
import com.ewaste.tracking.entity.Consumer;
import com.ewaste.tracking.entity.DropOffPoint;
import com.ewaste.tracking.entity.EWasteCategory;
import com.ewaste.tracking.entity.EWasteItem;
import com.ewaste.tracking.entity.builder.EWasteItemBuilder;
import com.ewaste.tracking.enums.EntityType;
import com.ewaste.tracking.enums.EWasteStatus;
import com.ewaste.tracking.exception.ResourceNotFoundException;
import com.ewaste.tracking.exception.UnauthorizedException;
import com.ewaste.tracking.repository.ConsumerRepository;
import com.ewaste.tracking.repository.DropOffPointRepository;
import com.ewaste.tracking.repository.EWasteCategoryRepository;
import com.ewaste.tracking.repository.EWasteItemRepository;
import com.ewaste.tracking.service.ConsumerService;
import com.ewaste.tracking.service.CreditService;
import com.ewaste.tracking.service.TrackingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * OOP PRINCIPLES: ABSTRACTION & BUILDER PATTERN INTEGRATION
 * 
 * - Abstraction: Implements ConsumerService interface.
 * - Builder Pattern: Demonstrates construction of complex EWasteItem instances
 *   using the hand-written EWasteItemBuilder with fluent chaining.
 */
@Service
public class ConsumerServiceImpl implements ConsumerService {

    private final ConsumerRepository consumerRepository;
    private final EWasteCategoryRepository categoryRepository;
    private final DropOffPointRepository dropOffPointRepository;
    private final EWasteItemRepository ewasteItemRepository;
    private final TrackingService trackingService;
    private final CreditService creditService;

    public ConsumerServiceImpl(ConsumerRepository consumerRepository,
                               EWasteCategoryRepository categoryRepository,
                               DropOffPointRepository dropOffPointRepository,
                               EWasteItemRepository ewasteItemRepository,
                               TrackingService trackingService,
                               CreditService creditService) {
        this.consumerRepository = consumerRepository;
        this.categoryRepository = categoryRepository;
        this.dropOffPointRepository = dropOffPointRepository;
        this.ewasteItemRepository = ewasteItemRepository;
        this.trackingService = trackingService;
        this.creditService = creditService;
    }

    @Override
    @Transactional
    public EWasteItemResponse submitEWasteItem(String consumerEmail, SubmitEWasteItemRequest request) {
        Consumer consumer = consumerRepository.findByEmail(consumerEmail.toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("Consumer account not found: " + consumerEmail));

        EWasteCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));

        DropOffPoint dropOffPoint = null;
        if (request.getDropOffPointId() != null) {
            dropOffPoint = dropOffPointRepository.findById(request.getDropOffPointId())
                    .orElseThrow(() -> new ResourceNotFoundException("Drop-off point not found with ID: " + request.getDropOffPointId()));
        }

        // OOP DESIGN PATTERN: HAND-WRITTEN BUILDER PATTERN UTILIZATION
        EWasteItem ewasteItem = new EWasteItemBuilder()
                .consumer(consumer)
                .category(category)
                .dropOffPoint(dropOffPoint)
                .deviceDescription(request.getDeviceDescription())
                .status(EWasteStatus.SUBMITTED)
                .submittedAt(LocalDateTime.now())
                .build();

        EWasteItem savedItem = ewasteItemRepository.save(ewasteItem);

        // Record initial state transition in immutable audit trail
        trackingService.recordTracking(
                EntityType.EWASTE_ITEM,
                savedItem.getId(),
                EWasteStatus.SUBMITTED.name(),
                consumer,
                "E-waste device registered in system for intake"
        );

        return mapToResponse(savedItem);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EWasteItemResponse> getMySubmissions(String consumerEmail) {
        Consumer consumer = consumerRepository.findByEmail(consumerEmail.toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("Consumer account not found: " + consumerEmail));

        return ewasteItemRepository.findByConsumerIdOrderBySubmittedAtDesc(consumer.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrackingRecordResponse> getItemTrackingTimeline(String consumerEmail, Long itemId) {
        Consumer consumer = consumerRepository.findByEmail(consumerEmail.toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("Consumer account not found: " + consumerEmail));

        EWasteItem item = ewasteItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("EWasteItem not found with ID: " + itemId));

        if (!item.getConsumer().getId().equals(consumer.getId())) {
            throw new UnauthorizedException("You are not authorized to view the tracking history of this item");
        }

        return trackingService.getTimeline(EntityType.EWASTE_ITEM, itemId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CreditResponse> getMyCredits(String consumerEmail) {
        Consumer consumer = consumerRepository.findByEmail(consumerEmail.toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("Consumer account not found: " + consumerEmail));

        return creditService.getConsumerCredits(consumer.getId());
    }

    @Override
    @Transactional
    public CreditRedemptionResponse redeemCredits(String consumerEmail, RedeemCreditRequest request) {
        Consumer consumer = consumerRepository.findByEmail(consumerEmail.toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("Consumer account not found: " + consumerEmail));

        return creditService.redeemCredits(consumer, request.getAmount(), request.getRedeemedFor());
    }

    private EWasteItemResponse mapToResponse(EWasteItem item) {
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
}
