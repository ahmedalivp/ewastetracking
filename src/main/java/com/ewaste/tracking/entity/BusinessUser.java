package com.ewaste.tracking.entity;

import com.ewaste.tracking.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * OOP PRINCIPLE: INHERITANCE & POLYMORPHISM
 * 
 * - Inheritance: Subclass of User for secondary market entities (refurbishers, recyclers).
 * - Polymorphism: Overrides getDashboardSummary() showing commercial business profile.
 */
@Entity
@Table(name = "business_users")
public class BusinessUser extends User {

    @Column(nullable = false)
    private String businessName;

    @Column(nullable = false)
    private String businessType;

    public BusinessUser() {
        super();
        this.setRole(Role.BUSINESS);
    }

    public BusinessUser(String fullName, String email, String passwordHash, String businessName, String businessType) {
        super(fullName, email, passwordHash, Role.BUSINESS);
        this.businessName = businessName;
        this.businessType = businessType;
    }

    @Override
    public String getDashboardSummary() {
        return String.format("Business Enterprise [%s] - Legal Name: %s | Sector: %s",
                getFullName(), businessName, businessType);
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }
}
