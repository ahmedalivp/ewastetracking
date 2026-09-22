package com.ewaste.tracking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * OOP PRINCIPLE: ENCAPSULATION
 * 
 * Records the redemption of recycling credits for vouchers, discounts, or eco-perks.
 */
@Entity
@Table(name = "credit_redemptions")
public class CreditRedemption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "consumer_id", nullable = false)
    private Consumer consumer;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private String redeemedFor;

    @Column(nullable = false)
    private LocalDateTime redeemedAt;

    public CreditRedemption() {
        this.redeemedAt = LocalDateTime.now();
    }

    public CreditRedemption(Consumer consumer, int amount, String redeemedFor) {
        this.consumer = consumer;
        this.amount = amount;
        this.redeemedFor = redeemedFor;
        this.redeemedAt = LocalDateTime.now();
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

    public String getRedeemedFor() {
        return redeemedFor;
    }

    public void setRedeemedFor(String redeemedFor) {
        this.redeemedFor = redeemedFor;
    }

    public LocalDateTime getRedeemedAt() {
        return redeemedAt;
    }

    public void setRedeemedAt(LocalDateTime redeemedAt) {
        this.redeemedAt = redeemedAt;
    }
}
