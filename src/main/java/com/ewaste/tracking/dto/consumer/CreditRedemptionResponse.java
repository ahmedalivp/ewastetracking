package com.ewaste.tracking.dto.consumer;

import java.time.LocalDateTime;

public class CreditRedemptionResponse {

    private Long id;
    private int amount;
    private String redeemedFor;
    private LocalDateTime redeemedAt;
    private int remainingBalance;

    public CreditRedemptionResponse() {}

    public CreditRedemptionResponse(Long id, int amount, String redeemedFor, LocalDateTime redeemedAt, int remainingBalance) {
        this.id = id;
        this.amount = amount;
        this.redeemedFor = redeemedFor;
        this.redeemedAt = redeemedAt;
        this.remainingBalance = remainingBalance;
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

    public int getRemainingBalance() {
        return remainingBalance;
    }

    public void setRemainingBalance(int remainingBalance) {
        this.remainingBalance = remainingBalance;
    }
}
