package com.ewaste.tracking.entity;

import com.ewaste.tracking.enums.Role;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * OOP PRINCIPLE: INHERITANCE & ENCAPSULATION & POLYMORPHISM
 * 
 * - Inheritance: Abstract base class representing system actors. Subclasses
 *   (Consumer, FacilityStaff, BusinessUser, AdminUser) inherit identity, credentials,
 *   role, and lifecycle timestamps.
 * - Encapsulation: All fields are private with controlled access via getters/setters.
 * - Polymorphism: Declares the abstract method getDashboardSummary() which is polymorphically
 *   overridden with unique role-specific behaviors in each derived class.
 */
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public User() {
        this.createdAt = LocalDateTime.now();
    }

    public User(String fullName, String email, String passwordHash, Role role) {
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * OOP PRINCIPLE: POLYMORPHISM
     * Abstract method overridden by each concrete subclass to deliver
     * specialized dashboard statistics and context.
     *
     * @return contextual summary description for the user dashboard
     */
    public abstract String getDashboardSummary();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
