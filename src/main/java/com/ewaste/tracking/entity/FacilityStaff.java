package com.ewaste.tracking.entity;

import com.ewaste.tracking.enums.Role;
import jakarta.persistence.*;

/**
 * OOP PRINCIPLE: INHERITANCE, POLYMORPHISM & COMPOSITION
 * 
 * - Inheritance: Subclass of User for recycling plant staff members.
 * - Polymorphism: Overrides getDashboardSummary() displaying facility operations context.
 * - Composition: Has-a relationship with RecyclingFacility.
 */
@Entity
@Table(name = "facility_staff")
public class FacilityStaff extends User {

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "facility_id", nullable = false)
    private RecyclingFacility facility;

    public FacilityStaff() {
        super();
        this.setRole(Role.FACILITY_STAFF);
    }

    public FacilityStaff(String fullName, String email, String passwordHash, RecyclingFacility facility) {
        super(fullName, email, passwordHash, Role.FACILITY_STAFF);
        this.facility = facility;
    }

    @Override
    public String getDashboardSummary() {
        String facilityName = facility != null ? facility.getName() : "Unassigned";
        return String.format("Facility Staff [%s] - Operational Station: %s (Verified: %s)",
                getFullName(), facilityName, facility != null && facility.isVerifiedByAdmin());
    }

    public RecyclingFacility getFacility() {
        return facility;
    }

    public void setFacility(RecyclingFacility facility) {
        this.facility = facility;
    }
}
