package com.ewaste.tracking.controller;

import com.ewaste.tracking.dto.admin.*;
import com.ewaste.tracking.dto.common.DropOffPointResponse;
import com.ewaste.tracking.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Administrator Oversight", description = "Endpoints for platform administrators to manage centers, drop-off locations, and staff access")
@SecurityRequirement(name = "BearerAuth")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ==========================================
    // RECYCLING FACILITIES / CENTERS
    // ==========================================

    @GetMapping("/facilities")
    @Operation(summary = "List all recycling facilities", description = "Returns all registered recycling facilities with certification and staff/drop-off counts.")
    public ResponseEntity<List<FacilityResponse>> getAllFacilities() {
        List<FacilityResponse> facilities = adminService.getAllFacilities();
        return ResponseEntity.ok(facilities);
    }

    @GetMapping("/facilities/{id}")
    @Operation(summary = "Get recycling facility by ID", description = "Returns facility metadata.")
    public ResponseEntity<FacilityResponse> getFacilityById(@PathVariable Long id) {
        FacilityResponse facility = adminService.getFacilityById(id);
        return ResponseEntity.ok(facility);
    }

    @PostMapping("/facilities")
    @Operation(summary = "Provision a new recycling facility", description = "Creates a new certified recycling center in the network.")
    public ResponseEntity<FacilityResponse> createFacility(@Valid @RequestBody CreateFacilityRequest request) {
        FacilityResponse response = adminService.createFacility(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/facilities/{id}")
    @Operation(summary = "Update recycling facility metadata", description = "Updates center coordinates, address, verification, and active status.")
    public ResponseEntity<FacilityResponse> updateFacility(
            @PathVariable Long id,
            @Valid @RequestBody UpdateFacilityRequest request) {
        FacilityResponse response = adminService.updateFacility(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/facilities/{id}/verify")
    @Operation(summary = "Verify or unverify a recycling facility", description = "Toggles regulatory admin verification for a recycling facility.")
    public ResponseEntity<FacilityResponse> verifyFacility(
            @PathVariable Long id,
            @Valid @RequestBody VerifyFacilityRequest request) {
        FacilityResponse response = adminService.verifyFacility(id, request.getVerified());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/facilities/{id}")
    @Operation(summary = "Delete recycling facility", description = "Removes a recycling center and associated drop-off stations.")
    public ResponseEntity<Void> deleteFacility(@PathVariable Long id) {
        adminService.deleteFacility(id);
        return ResponseEntity.noContent().build();
    }

    // ==========================================
    // DROP-OFF NETWORK LOCATIONS
    // ==========================================

    @PostMapping("/drop-off-points")
    @Operation(summary = "Add a new drop-off collection point", description = "Creates a public smart drop box, kiosk, or depot linked to a parent center.")
    public ResponseEntity<DropOffPointResponse> createDropOffPoint(@Valid @RequestBody CreateDropOffPointRequest request) {
        DropOffPointResponse response = adminService.createDropOffPoint(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/drop-off-points/{id}")
    @Operation(summary = "Update drop-off collection point", description = "Modifies location details, hours, accepted materials, or reassigns parent center.")
    public ResponseEntity<DropOffPointResponse> updateDropOffPoint(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDropOffPointRequest request) {
        DropOffPointResponse response = adminService.updateDropOffPoint(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/drop-off-points/{id}")
    @Operation(summary = "Delete drop-off point", description = "Decommissions a public collection point.")
    public ResponseEntity<Void> deleteDropOffPoint(@PathVariable Long id) {
        adminService.deleteDropOffPoint(id);
        return ResponseEntity.noContent().build();
    }

    // ==========================================
    // STAFF ACCESS GOVERNANCE
    // ==========================================

    @GetMapping("/staff")
    @Operation(summary = "List facility staff members", description = "Returns staff members optionally filtered by facility ID.")
    public ResponseEntity<List<StaffMemberResponse>> getAllStaff(@RequestParam(required = false) Long facilityId) {
        List<StaffMemberResponse> staff = adminService.getAllStaff(facilityId);
        return ResponseEntity.ok(staff);
    }

    @PutMapping("/staff/{id}")
    @Operation(summary = "Update staff access and assignment", description = "Reassigns staff to another center or toggles active authorization.")
    public ResponseEntity<StaffMemberResponse> updateStaffAccess(
            @PathVariable Long id,
            @RequestBody UpdateStaffAccessRequest request) {
        StaffMemberResponse response = adminService.updateStaffAccess(id, request);
        return ResponseEntity.ok(response);
    }
}
