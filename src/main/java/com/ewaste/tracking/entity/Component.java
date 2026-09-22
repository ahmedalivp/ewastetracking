package com.ewaste.tracking.entity;

import com.ewaste.tracking.enums.ComponentCondition;
import com.ewaste.tracking.enums.ComponentStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * OOP PRINCIPLE: ENCAPSULATION & COMPOSITION
 * 
 * Represents a discrete hardware component salvaged from an e-waste item.
 * Encapsulates component health, type, and secondary-market readiness status.
 */
@Entity
@Table(name = "components")
public class Component {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ewaste_item_id", nullable = false)
    private EWasteItem ewasteItem;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComponentCondition condition;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComponentStatus status;

    @Column(nullable = false)
    private LocalDateTime harvestedAt;

    public Component() {
        this.status = ComponentStatus.HARVESTED;
        this.harvestedAt = LocalDateTime.now();
    }

    public Component(EWasteItem ewasteItem, String name, String type, ComponentCondition condition, ComponentStatus status) {
        this.ewasteItem = ewasteItem;
        this.name = name;
        this.type = type;
        this.condition = condition;
        this.status = status != null ? status : ComponentStatus.HARVESTED;
        this.harvestedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EWasteItem getEwasteItem() {
        return ewasteItem;
    }

    public void setEwasteItem(EWasteItem ewasteItem) {
        this.ewasteItem = ewasteItem;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ComponentCondition getCondition() {
        return condition;
    }

    public void setCondition(ComponentCondition condition) {
        this.condition = condition;
    }

    public ComponentStatus getStatus() {
        return status;
    }

    public void setStatus(ComponentStatus status) {
        this.status = status;
    }

    public LocalDateTime getHarvestedAt() {
        return harvestedAt;
    }

    public void setHarvestedAt(LocalDateTime harvestedAt) {
        this.harvestedAt = harvestedAt;
    }
}
