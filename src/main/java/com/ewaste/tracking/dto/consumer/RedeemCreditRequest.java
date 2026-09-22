package com.ewaste.tracking.dto.consumer;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RedeemCreditRequest {

    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be at least 1 credit point")
    private Integer amount;

    @NotBlank(message = "Redeemed for description is required")
    private String redeemedFor;

    public RedeemCreditRequest() {}

    public RedeemCreditRequest(Integer amount, String redeemedFor) {
        this.amount = amount;
        this.redeemedFor = redeemedFor;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getRedeemedFor() {
        return redeemedFor;
    }

    public void setRedeemedFor(String redeemedFor) {
        this.redeemedFor = redeemedFor;
    }
}
