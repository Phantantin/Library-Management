package com.zou.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutRequest {

    @NotNull(message = "Book ID is mandatory")
    private Long bookId;

    @NotNull(message = "Checkout days are mandatory")
    @Min(value = 1, message = "Checkout days must be at least 1")
    @Max(value = 30, message = "Checkout days cannot exceed 30")
    private Integer checkoutDays=14;

    private String notes;
}
