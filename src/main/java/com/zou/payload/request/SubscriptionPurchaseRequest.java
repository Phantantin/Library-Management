package com.zou.payload.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SubscriptionPurchaseRequest {
    @NotNull
    @Positive
    private Long planId;

    private String paymentMethod;
}
