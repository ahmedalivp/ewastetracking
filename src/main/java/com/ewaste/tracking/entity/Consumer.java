package com.ewaste.tracking.entity;

import com.ewaste.tracking.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * OOP PRINCIPLE: INHERITANCE & POLYMORPHISM
 * 
 * - Inheritance: Extends abstract base class User, acquiring shared user attributes.
 * - Polymorphism: Overrides getDashboardSummary() to present consumer-specific metrics
 *   such as recycling credit balance.
 */
@Entity
@Table(name = "consumers")
public class Consumer extends User {

    @Column(nullable = false)
    private int creditBalance;

    public Consumer() {
        super();
        this.setRole(Role.CONSUMER);
        this.creditBalance = 0;
    }

    public Consumer(String fullName, String email, String passwordHash) {
        super(fullName, email, passwordHash, Role.CONSUMER);
        this.creditBalance = 0;
    }

    public Consumer(String fullName, String email, String passwordHash, int initialCreditBalance) {
        super(fullName, email, passwordHash, Role.CONSUMER);
        this.creditBalance = initialCreditBalance;
    }

    @Override
    public String getDashboardSummary() {
        return String.format("Consumer [%s] - Available Recycling Credits: %d pts",
                getFullName(), creditBalance);
    }

    public int getCreditBalance() {
        return creditBalance;
    }

    public void setCreditBalance(int creditBalance) {
        this.creditBalance = creditBalance;
    }

    public void addCredits(int points) {
        this.creditBalance += points;
    }

    public void deductCredits(int points) {
        if (points > this.creditBalance) {
            throw new IllegalArgumentException("Insufficient credit balance");
        }
        this.creditBalance -= points;
    }
}
