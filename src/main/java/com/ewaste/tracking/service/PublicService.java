package com.ewaste.tracking.service;

import com.ewaste.tracking.dto.common.CategoryResponse;
import com.ewaste.tracking.dto.common.DropOffPointResponse;

import java.util.List;

/**
 * OOP PRINCIPLE: ABSTRACTION
 * 
 * Public service interface for retrieving non-authenticated catalog data.
 */
public interface PublicService {

    List<DropOffPointResponse> getAllDropOffPoints();

    List<CategoryResponse> getAllCategories();

    com.ewaste.tracking.dto.common.ImpactStatsResponse getImpactStats();
}
