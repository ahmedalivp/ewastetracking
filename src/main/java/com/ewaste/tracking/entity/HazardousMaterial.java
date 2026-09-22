package com.ewaste.tracking.entity;

import com.ewaste.tracking.enums.DiversionStatus;
import com.ewaste.tracking.enums.HazardousType;
import jakarta.persistence.*;

/**
 * OOP PRINCIPLE: ENCAPSULATION & COMPOSITION
 * 
 * Represents toxic or hazardous substances present in discarded electronic gear.
 * Tracks environmental safe diversion out of municipal solid waste streams.
 */
@Entity
@Table(name = "hazardous_materials")
public class HazardousMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ewaste_item_id", nullable = false)
    private EWasteItem ewasteItem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HazardousType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiversionStatus diversionStatus;

    public HazardousMaterial() {
        this.diversionStatus = DiversionStatus.FLAGGED;
    }

    public HazardousMaterial(EWasteItem ewasteItem, HazardousType type, DiversionStatus diversionStatus) {
        this.ewasteItem = ewasteItem;
        this.type = type;
        this.diversionStatus = diversionStatus != null ? diversionStatus : DiversionStatus.FLAGGED;
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

    public HazardousType getType() {
        return type;
    }

    public void setType(HazardousType type) {
        this.type = type;
    }

    public DiversionStatus getDiversionStatus() {
        return diversionStatus;
    }

    public void setDiversionStatus(DiversionStatus diversionStatus) {
        this.diversionStatus = diversionStatus;
    }
}
