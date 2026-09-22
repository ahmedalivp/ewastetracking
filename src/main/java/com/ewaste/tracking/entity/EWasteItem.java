package com.ewaste.tracking.entity;

import com.ewaste.tracking.enums.EWasteStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * OOP PRINCIPLE: COMPOSITION & ENCAPSULATION
 * 
 * - Composition: EWasteItem demonstrates a "has-a" structural composition:
 *   * has-a EWasteCategory (category classification)
 *   * has-a Consumer (submitting owner)
 *   * has-a DropOffPoint (selected drop-off location)
 *   * has-a List<Component> (salvaged sub-components)
 *   * has-a List<HazardousMaterial> (toxic materials requiring safe diversion)
 *   It does NOT inherit from device or hardware classes; composition allows flexible,
 *   decoupled, runtime assembly of sub-parts and attributes.
 * - Encapsulation: State is private and accessed through getters and validated mutators.
 */
@Entity
@Table(name = "ewaste_items")
public class EWasteItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "consumer_id", nullable = false)
    private Consumer consumer;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private EWasteCategory category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "drop_off_point_id")
    private DropOffPoint dropOffPoint;

    @Column(nullable = false, length = 1000)
    private String deviceDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EWasteStatus status;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    // Composition: "has-a" list of salvageable components
    @OneToMany(mappedBy = "ewasteItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Component> components = new ArrayList<>();

    // Composition: "has-a" list of hazardous materials
    @OneToMany(mappedBy = "ewasteItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HazardousMaterial> hazardousMaterials = new ArrayList<>();

    public EWasteItem() {
        this.status = EWasteStatus.SUBMITTED;
        this.submittedAt = LocalDateTime.now();
    }

    public EWasteItem(Consumer consumer, EWasteCategory category, DropOffPoint dropOffPoint,
                      String deviceDescription, EWasteStatus status, LocalDateTime submittedAt) {
        this.consumer = consumer;
        this.category = category;
        this.dropOffPoint = dropOffPoint;
        this.deviceDescription = deviceDescription;
        this.status = status != null ? status : EWasteStatus.SUBMITTED;
        this.submittedAt = submittedAt != null ? submittedAt : LocalDateTime.now();
    }

    public void addComponent(Component component) {
        components.add(component);
        component.setEwasteItem(this);
    }

    public void removeComponent(Component component) {
        components.remove(component);
        component.setEwasteItem(null);
    }

    public void addHazardousMaterial(HazardousMaterial material) {
        hazardousMaterials.add(material);
        material.setEwasteItem(this);
    }

    public void removeHazardousMaterial(HazardousMaterial material) {
        hazardousMaterials.remove(material);
        material.setEwasteItem(null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    public EWasteCategory getCategory() {
        return category;
    }

    public void setCategory(EWasteCategory category) {
        this.category = category;
    }

    public DropOffPoint getDropOffPoint() {
        return dropOffPoint;
    }

    public void setDropOffPoint(DropOffPoint dropOffPoint) {
        this.dropOffPoint = dropOffPoint;
    }

    public String getDeviceDescription() {
        return deviceDescription;
    }

    public void setDeviceDescription(String deviceDescription) {
        this.deviceDescription = deviceDescription;
    }

    public EWasteStatus getStatus() {
        return status;
    }

    public void setStatus(EWasteStatus status) {
        this.status = status;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public List<Component> getComponents() {
        return components;
    }

    public void setComponents(List<Component> components) {
        this.components = components;
    }

    public List<HazardousMaterial> getHazardousMaterials() {
        return hazardousMaterials;
    }

    public void setHazardousMaterials(List<HazardousMaterial> hazardousMaterials) {
        this.hazardousMaterials = hazardousMaterials;
    }
}
