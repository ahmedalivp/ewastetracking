package com.ewaste.tracking.controller;

import com.ewaste.tracking.dto.common.CategoryResponse;
import com.ewaste.tracking.dto.common.DropOffPointResponse;
import com.ewaste.tracking.service.PublicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Public Information", description = "Public endpoints for categories and recycling drop-off locations")
public class PublicController {

    private final PublicService publicService;

    public PublicController(PublicService publicService) {
        this.publicService = publicService;
    }

    @GetMapping("/drop-off-points")
    @Operation(summary = "List all drop-off collection stations", description = "Publicly accessible list of drop-off points with geographic coordinates and facility links.")
    public ResponseEntity<List<DropOffPointResponse>> getDropOffPoints() {
        return ResponseEntity.ok(publicService.getAllDropOffPoints());
    }

    @GetMapping("/categories")
    @Operation(summary = "List all e-waste categories", description = "Publicly accessible list of standard e-waste device categories.")
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        return ResponseEntity.ok(publicService.getAllCategories());
    }

    @GetMapping("/impact")
    @Operation(summary = "Get platform environmental impact stats", description = "Aggregate statistics on diverted landfill weight, salvaged components, neutralized toxins, and awarded credits.")
    public ResponseEntity<com.ewaste.tracking.dto.common.ImpactStatsResponse> getImpactStats() {
        return ResponseEntity.ok(publicService.getImpactStats());
    }
}
