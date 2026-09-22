package com.ewaste.tracking.service.impl;

import com.ewaste.tracking.dto.common.CategoryResponse;
import com.ewaste.tracking.dto.common.DropOffPointResponse;
import com.ewaste.tracking.entity.DropOffPoint;
import com.ewaste.tracking.entity.RecyclingCredit;
import com.ewaste.tracking.repository.DropOffPointRepository;
import com.ewaste.tracking.repository.EWasteCategoryRepository;
import com.ewaste.tracking.service.PublicService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import com.ewaste.tracking.dto.common.ImpactStatsResponse;
import com.ewaste.tracking.repository.ComponentRepository;
import com.ewaste.tracking.repository.EWasteItemRepository;
import com.ewaste.tracking.repository.HazardousMaterialRepository;
import com.ewaste.tracking.repository.RecyclingCreditRepository;
import com.ewaste.tracking.repository.RecyclingFacilityRepository;

@Service
public class PublicServiceImpl implements PublicService {

    private final DropOffPointRepository dropOffPointRepository;
    private final EWasteCategoryRepository categoryRepository;
    private final EWasteItemRepository ewasteItemRepository;
    private final ComponentRepository componentRepository;
    private final HazardousMaterialRepository hazardousMaterialRepository;
    private final RecyclingCreditRepository creditRepository;
    private final RecyclingFacilityRepository facilityRepository;

    public PublicServiceImpl(DropOffPointRepository dropOffPointRepository,
                             EWasteCategoryRepository categoryRepository,
                             EWasteItemRepository ewasteItemRepository,
                             ComponentRepository componentRepository,
                             HazardousMaterialRepository hazardousMaterialRepository,
                             RecyclingCreditRepository creditRepository,
                             RecyclingFacilityRepository facilityRepository) {
        this.dropOffPointRepository = dropOffPointRepository;
        this.categoryRepository = categoryRepository;
        this.ewasteItemRepository = ewasteItemRepository;
        this.componentRepository = componentRepository;
        this.hazardousMaterialRepository = hazardousMaterialRepository;
        this.creditRepository = creditRepository;
        this.facilityRepository = facilityRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ImpactStatsResponse getImpactStats() {
        long totalSubmissions = ewasteItemRepository.count();
        long totalHarvested = componentRepository.count();
        long totalHazardous = hazardousMaterialRepository.count();
        long activeFacilities = facilityRepository.count();

        long totalCredits = creditRepository.findAll().stream()
                .mapToLong(RecyclingCredit::getAmount)
                .sum();

        // Estimated metrics: ~4.5kg average consumer device diverted, ~1.2kg per harvested component
        double divertedKg = (totalSubmissions * 4.5) + (totalHarvested * 1.2);
        double co2Kg = Math.round(divertedKg * 2.8 * 10.0) / 10.0;
        divertedKg = Math.round(divertedKg * 10.0) / 10.0;

        return new ImpactStatsResponse(
                totalSubmissions,
                totalHarvested,
                totalHazardous,
                totalCredits,
                divertedKg,
                co2Kg,
                activeFacilities
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<DropOffPointResponse> getAllDropOffPoints() {
        return dropOffPointRepository.findAll().stream()
                .map(this::mapDropOffPoint)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(c -> new CategoryResponse(c.getId(), c.getName(), c.getDescription()))
                .collect(Collectors.toList());
    }

    private DropOffPointResponse mapDropOffPoint(DropOffPoint point) {
        Long facilityId = point.getFacility() != null ? point.getFacility().getId() : null;
        String facilityName = point.getFacility() != null ? point.getFacility().getName() : null;

        return new DropOffPointResponse(
                point.getId(),
                point.getLabel(),
                point.getLat(),
                point.getLng(),
                facilityId,
                facilityName
        );
    }
}
