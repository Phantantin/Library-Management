package com.zou.payload.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.aspectj.bridge.IMessage;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class SubscriptionDTO {

    private Long id;

    @NotNull(message = "User ID is mandatory")
    private Long userId;

    private String userName;
    private String userEmail;

    @NotNull(message = "Subscription plan ID is mandatory")
    private Long planId;

    private String planName;
    private String planCode;
    private Long price;
    private String currency;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer maxBooksAllowed;
    private Integer maxDaysPerBook;
    private Boolean autoRenew;
    private LocalDateTime cancelledAt;
    private String cancellationReason;
    private String notes;
    private Long daysRemaining;
    private Boolean isActive;
    private Boolean isValid;
    private Boolean isExpired;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
