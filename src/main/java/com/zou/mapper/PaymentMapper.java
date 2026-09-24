package com.zou.mapper;

import com.zou.modal.Payment;
import com.zou.payload.dto.PaymentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentMapper {
    public PaymentDTO toDTO(Payment payment) {

        if (payment == null) {
            return null;
        }

        PaymentDTO dto = new PaymentDTO();

        dto.setId(payment.getId());

        // User information
        if (payment.getUser() != null) {
            dto.setUserId(payment.getUser().getId());
            dto.setUserName(payment.getUser().getFullName());
            dto.setUserEmail(payment.getUser().getEmail());
        }

        // Book loan information
//        if (payment.getBookLoan() != null) {
//            dto.setBookLoanId(payment.getBookLoan().getId());
//        }

        // Subscription information
        if (payment.getSubscription() != null) {
            dto.setSubscriptionId(payment.getSubscription().getId());
        }

        // Payment information
        dto.setPaymentType(payment.getPaymentType());
        dto.setStatus(payment.getStatus());
        dto.setGateway(payment.getGateway());
        dto.setAmount(payment.getAmount());
        dto.setCurrency(payment.getCurrency());
        dto.setFineId(payment.getFine()==null ? null : payment.getFine().getId());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setUpdatedAt(payment.getUpdatedAt());

        // Gateway information
        dto.setTransactionId(payment.getTransactionId());
        dto.setGatewayPaymentId(payment.getGatewayPaymentId());
        dto.setGatewayOrderId(payment.getGatewayOrderId());
        dto.setGatewaySignature(payment.getGatewaySignature());

        // Additional information
        dto.setDescription(payment.getDescription());
        dto.setFailureReason(payment.getFailureReason());

        // Time information
        dto.setInitiatedAt(payment.getInitiateAt());
        dto.setCompletedAt(payment.getCompletedAt());

        return dto;
    }
}
