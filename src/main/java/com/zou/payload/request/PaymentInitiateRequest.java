package com.zou.payload.request;

import com.zou.domain.PaymentGateway;
import com.zou.domain.PaymentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentInitiateRequest {

    @NotNull(message = "User ID is mandatory")
    private Long userId;

    // Chỉ bắt buộc khi thanh toán tiền phạt FINE
    private Long bookLoanId;

    @NotNull(message = "Payment type is mandatory")
    private PaymentType paymentType;

    @NotNull(message = "Payment gateway is mandatory")
    private PaymentGateway gateway;

    @NotNull(message = "Amount is mandatory")
    @Positive(message = "Amount must be positive")
    private Long amount;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private Long fineId;

    private Long subscriptionId;

    // URL chuyển hướng sau khi thanh toán thành công
    @Size(max = 500, message = "Success URL must not exceed 500 characters")
    private String successUrl;

    // URL chuyển hướng khi hủy thanh toán
    @Size(max = 500, message = "Cancel URL must not exceed 500 characters")
    private String cancelUrl;
}
