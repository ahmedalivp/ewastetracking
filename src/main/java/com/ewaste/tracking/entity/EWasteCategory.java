package com.ewaste.tracking.entity;

import jakarta.persistence.*;

/**
 * OOP PRINCIPLE: ENCAPSULATION
 * 
 * Classification category for e-waste items (e.g., Laptop, Smartphone, Battery).
 */
@Entity
@Table(name = "ewaste_categories")
public class EWasteCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 1000)
    private String description;

    public EWasteCategory() {}

    public EWasteCategory(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
