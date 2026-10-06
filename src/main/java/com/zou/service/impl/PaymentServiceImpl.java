package com.zou.service.impl;

import com.zou.domain.PaymentGateway;
import com.zou.domain.PaymentStatus;
import com.zou.domain.PaymentType;
import com.zou.event.publisher.PaymentEventPublisher;
import com.zou.mapper.PaymentMapper;
import com.zou.modal.Payment;
import com.zou.payload.dto.PaymentDTO;
import com.zou.payload.request.PaymentInitiateRequest;
import com.zou.payload.response.PaymentInitiateResponse;
import com.zou.repository.FineRepository;
import com.zou.repository.PaymentRepository;
import com.zou.repository.SubscriptionRepository;
import com.zou.repository.UserRepository;
import com.zou.service.AccessService;
import com.zou.service.PaymentService;
import com.zou.service.gateway.VnpayService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class PaymentServiceImpl implements PaymentService {
    private final UserRepository users;
    private final SubscriptionRepository subscriptions;
    private final PaymentRepository payments;
    private final FineRepository fines;
    private final VnpayService vnpay;
    private final PaymentMapper mapper;
    private final PaymentEventPublisher events;
    private final AccessService access;

    @Override
    public PaymentInitiateResponse initiatePayment(PaymentInitiateRequest request) throws Exception {
        if (request.getGateway() != PaymentGateway.VNPAY) {
            throw new IllegalArgumentException("Unsupported payment gateway");
        }
        access.ownerOrAdmin(request.getUserId());
        var user = users.findById(request.getUserId()).orElseThrow();

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setPaymentType(request.getPaymentType());
        payment.setGateway(PaymentGateway.VNPAY);
        payment.setAmount(request.getAmount());
        payment.setCurrency("VND");
        payment.setDescription(request.getDescription());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionId("TXN_" + UUID.randomUUID());

        if (request.getSubscriptionId() != null) {
            var subscription = subscriptions.findById(request.getSubscriptionId()).orElseThrow();
            if (!subscription.getUser().getId().equals(user.getId())) {
                throw new org.springframework.security.access.AccessDeniedException("Invalid owner");
            }
            if (!"VND".equalsIgnoreCase(subscription.getPlan().getCurrency())) {
                throw new IllegalArgumentException("VNPAY subscriptions must be priced in VND");
            }
            payment.setSubscription(subscription);
            payment.setAmount(subscription.getPrice());
        }

        if (request.getFineId() != null) {
            var fine = fines.findById(request.getFineId()).orElseThrow();
            if (!fine.getUser().getId().equals(user.getId())) {
                throw new org.springframework.security.access.AccessDeniedException("Invalid owner");
            }
            payment.setFine(fine);
            payment.setAmount(fine.getAmount());
        }

        if (payment.getAmount() == null || payment.getAmount() <= 0) {
            throw new IllegalArgumentException("Invalid payment amount");
        }
        payment = payments.save(payment);
        payment.setGatewayOrderId(payment.getId().toString());
        payment.setStatus(PaymentStatus.PROCESSING);
        payment = payments.save(payment);

        String checkoutUrl = vnpay.createPaymentUrl(payment, request.getIpAddress(), request.getPaymentMethod());
        return PaymentInitiateResponse.builder()
                .paymentId(payment.getId())
                .gateway(PaymentGateway.VNPAY)
                .gatewayOrderId(payment.getGatewayOrderId())
                .transactionId(payment.getTransactionId())
                .amount(payment.getAmount())
                .description(payment.getDescription())
                .checkoutUrl(checkoutUrl)
                .message("Continue to secure VNPAY checkout")
                .success(true)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDTO getPayment(Long paymentId) throws Exception {
        Payment payment = payments.findById(paymentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
        access.ownerOrAdmin(payment.getUser().getId());
        return mapper.toDTO(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public String processVnpayReturn(Map<String, String> params) {
        if (!vnpay.hasValidSignature(params) || !vnpay.hasExpectedMerchant(params)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid VNPAY return signature.");
        }
        Payment payment = findVnpayPayment(params.get("vnp_TxnRef"))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
        verifyAmount(payment, params.get("vnp_Amount"));
        String result = isSuccessfulResult(params) ? "success" : "failed";
        return vnpay.createFrontendReturnUrl(payment.getId(), result);
    }

    @Override
    public Map<String, String> processVnpayIpn(Map<String, String> params) {
        if (!vnpay.hasValidSignature(params) || !vnpay.hasExpectedMerchant(params)) {
            return ipnResponse("97", "Invalid signature");
        }

        Optional<Payment> match = findLockedVnpayPayment(params.get("vnp_TxnRef"));
        if (match.isEmpty()) return ipnResponse("01", "Order not found");
        Payment payment = match.get();

        if (payment.getStatus() == PaymentStatus.SUCCESS || payment.getStatus() == PaymentStatus.FAILED
                || payment.getStatus() == PaymentStatus.CANCELLED) {
            return ipnResponse("02", "Order already confirmed");
        }
        try {
            verifyAmount(payment, params.get("vnp_Amount"));
        } catch (ResponseStatusException exception) {
            return ipnResponse("04", "Invalid amount");
        }

        if (isSuccessfulResult(params)) {
            String transactionNumber = params.get("vnp_TransactionNo");
            if (transactionNumber == null || transactionNumber.isBlank()) {
                return ipnResponse("99", "Transaction number missing");
            }
            payment.setGatewayPaymentId(transactionNumber);
            payment.setGatewaySignature(params.get("vnp_SecureHash"));
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setCompletedAt(LocalDateTime.now());
            payments.save(payment);
            if (payment.getFine() != null) {
                var fine = fines.findById(payment.getFine().getId())
                        .orElseThrow(() -> new IllegalStateException("Paid fine was not found"));
                fine.applyPayment(payment.getAmount());
                fine.setTransactionId(transactionNumber);
                fines.save(fine);
            }
            events.publishPaymentSuccessEvent(payment);
        } else {
            String responseCode = params.getOrDefault("vnp_ResponseCode", "99");
            payment.setStatus("24".equals(responseCode) ? PaymentStatus.CANCELLED : PaymentStatus.FAILED);
            payment.setFailureReason("VNPAY response code: " + responseCode);
            payment.setGatewaySignature(params.get("vnp_SecureHash"));
            payment.setCompletedAt(LocalDateTime.now());
            payments.save(payment);
        }
        return ipnResponse("00", "Confirm Success");
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentDTO> getAllPayments(Pageable pageable) {
        return payments.findAll(pageable).map(mapper::toDTO);
    }

    private Optional<Payment> findVnpayPayment(String transactionReference) {
        if (transactionReference == null || !transactionReference.matches("[0-9]{1,19}")) return Optional.empty();
        return payments.findByGatewayOrderId(transactionReference)
                .filter(payment -> payment.getGateway() == PaymentGateway.VNPAY);
    }

    private Optional<Payment> findLockedVnpayPayment(String transactionReference) {
        if (transactionReference == null || !transactionReference.matches("[0-9]{1,19}")) return Optional.empty();
        return payments.findLockedByGatewayOrderId(transactionReference)
                .filter(payment -> payment.getGateway() == PaymentGateway.VNPAY);
    }

    private static void verifyAmount(Payment payment, String amountValue) {
        if (!"VND".equalsIgnoreCase(payment.getCurrency()) || amountValue == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid payment amount");
        }
        try {
            long expected = Math.multiplyExact(payment.getAmount(), 100L);
            if (expected != Long.parseLong(amountValue)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid payment amount");
            }
        } catch (NumberFormatException | ArithmeticException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid payment amount");
        }
    }

    private static boolean isSuccessfulResult(Map<String, String> params) {
        return "00".equals(params.get("vnp_ResponseCode"))
                && "00".equals(params.get("vnp_TransactionStatus"));
    }

    private static Map<String, String> ipnResponse(String code, String message) {
        return Map.of("RspCode", code, "Message", message);
    }
}
