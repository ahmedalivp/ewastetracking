package com.ewaste.tracking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * OOP PRINCIPLE: ENCAPSULATION
 * 
 * Records awarded incentive credits for a consumer upon e-waste intake and component recovery.
 */
@Entity
@Table(name = "recycling_credits")
public class RecyclingCredit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "consumer_id", nullable = false)
    private Consumer consumer;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime awardedAt;

    public RecyclingCredit() {
        this.awardedAt = LocalDateTime.now();
    }

    public RecyclingCredit(Consumer consumer, int amount, String reason) {
        this.consumer = consumer;
        this.amount = amount;
        this.reason = reason;
        this.awardedAt = LocalDateTime.now();
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getAwardedAt() {
        return awardedAt;
    }

    public void setAwardedAt(LocalDateTime awardedAt) {
        this.awardedAt = awardedAt;
    }
}
