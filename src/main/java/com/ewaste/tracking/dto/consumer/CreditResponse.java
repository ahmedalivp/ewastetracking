package com.ewaste.tracking.dto.consumer;

import java.time.LocalDateTime;

public class CreditResponse {

    private Long id;
    private int amount;
    private String reason;
    private LocalDateTime awardedAt;

    public CreditResponse() {}

    public CreditResponse(Long id, int amount, String reason, LocalDateTime awardedAt) {
        this.id = id;
        this.amount = amount;
        this.reason = reason;
        this.awardedAt = awardedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
