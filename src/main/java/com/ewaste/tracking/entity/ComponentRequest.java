package com.ewaste.tracking.entity;

import com.ewaste.tracking.enums.RequestStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * OOP PRINCIPLE: ENCAPSULATION & COMPOSITION
 * 
 * Requisition entity representing secondary market entities purchasing
 * or requesting salvaged components for circular electronics manufacturing/repair.
 */
@Entity
@Table(name = "component_requests")
public class ComponentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false)
    private BusinessUser business;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "component_id", nullable = false)
    private Component component;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    public ComponentRequest() {
        this.status = RequestStatus.PENDING;
        this.requestedAt = LocalDateTime.now();
    }

    public ComponentRequest(BusinessUser business, Component component) {
        this.business = business;
        this.component = component;
        this.status = RequestStatus.PENDING;
        this.requestedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BusinessUser getBusiness() {
        return business;
    }

    public void setBusiness(BusinessUser business) {
        this.business = business;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }
}
