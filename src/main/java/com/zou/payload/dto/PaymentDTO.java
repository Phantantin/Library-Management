package com.zou.payload.dto;

import com.zou.domain.PaymentGateway;
import com.zou.domain.PaymentStatus;
import com.zou.domain.PaymentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO {

    private Long id;

    @NotNull(message = "User ID is mandatory")
    private Long userId;

    private String userName;

    private String userEmail;

    private Long bookLoanId;

    private Long subscriptionId;

    @NotNull(message = "Payment type is mandatory")
    private PaymentType paymentType;

    private PaymentStatus status;

    @NotNull(message = "Payment gateway is mandatory")
    private PaymentGateway gateway;

    @NotNull(message = "Amount is mandatory")
    @Positive(message = "Amount must be positive")
    private Long amount;
    private String currency;
    private Long fineId;

    private String transactionId;

    private String gatewayPaymentId;

    private String gatewayOrderId;

    private String gatewaySignature;

    private String description;

    private String failureReason;

    private Integer retryCount;

    private LocalDateTime initiatedAt;

    private LocalDateTime completedAt;


    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
