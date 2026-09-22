package com.ewaste.tracking.entity;

import com.ewaste.tracking.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * OOP PRINCIPLE: INHERITANCE & POLYMORPHISM
 * 
 * - Inheritance: Subclass of User for administrative oversight.
 * - Polymorphism: Overrides getDashboardSummary() displaying administrative authority context.
 */
@Entity
@Table(name = "admin_users")
public class AdminUser extends User {

    @Column(nullable = false)
    private String department;

    public AdminUser() {
        super();
        this.setRole(Role.ADMIN);
        this.department = "Platform Operations";
    }

    public AdminUser(String fullName, String email, String passwordHash, String department) {
        super(fullName, email, passwordHash, Role.ADMIN);
        this.department = department != null ? department : "Platform Operations";
    }

    @Override
    public String getDashboardSummary() {
        return String.format("System Administrator [%s] - Department: %s | System Oversight Status: Active",
                getFullName(), department);
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
