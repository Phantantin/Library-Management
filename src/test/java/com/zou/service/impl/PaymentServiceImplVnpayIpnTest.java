package com.zou.service.impl;

import com.zou.domain.PaymentGateway;
import com.zou.domain.PaymentStatus;
import com.zou.domain.PaymentType;
import com.zou.event.publisher.PaymentEventPublisher;
import com.zou.mapper.PaymentMapper;
import com.zou.modal.Payment;
import com.zou.repository.FineRepository;
import com.zou.repository.PaymentRepository;
import com.zou.repository.SubscriptionRepository;
import com.zou.repository.UserRepository;
import com.zou.service.AccessService;
import com.zou.service.gateway.VnpayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PaymentServiceImplVnpayIpnTest {
    private static final String HASH_SECRET = "unit-test-only-vnpay-secret";

    private PaymentRepository payments;
    private PaymentEventPublisher events;
    private PaymentServiceImpl service;
    private Payment payment;

    @BeforeEach
    void setUp() {
        var users = mock(UserRepository.class);
        var subscriptions = mock(SubscriptionRepository.class);
        payments = mock(PaymentRepository.class);
        var fines = mock(FineRepository.class);
        var vnpay = new VnpayService();
        ReflectionTestUtils.setField(vnpay, "tmnCode", "TESTCODE");
        ReflectionTestUtils.setField(vnpay, "hashSecret", HASH_SECRET);
        events = mock(PaymentEventPublisher.class);
        var mapper = mock(PaymentMapper.class);
        var access = mock(AccessService.class);
        service = new PaymentServiceImpl(users, subscriptions, payments, fines, vnpay, mapper, events, access);
        payment = Payment.builder()
                .id(42L)
                .gatewayOrderId("42")
                .gateway(PaymentGateway.VNPAY)
                .paymentType(PaymentType.MEMBERSHIP)
                .status(PaymentStatus.PROCESSING)
                .amount(12_500L)
                .currency("VND")
                .build();
    }

    @Test
    void signedSuccessfulIpnUpdatesPaymentAndPublishesOneSuccessEvent() throws Exception {
        when(payments.findLockedByGatewayOrderId("42")).thenReturn(Optional.of(payment));

        Map<String, String> response = service.processVnpayIpn(signedCallback("1250000"));

        assertEquals("00", response.get("RspCode"));
        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
        assertEquals("VNPAY-TXN-100", payment.getGatewayPaymentId());
        verify(payments).save(payment);
        verify(events).publishPaymentSuccessEvent(payment);
    }

    @Test
    void signedCallbackWithDifferentAmountIsRejectedWithoutChangingPayment() throws Exception {
        when(payments.findLockedByGatewayOrderId("42")).thenReturn(Optional.of(payment));

        Map<String, String> response = service.processVnpayIpn(signedCallback("1250001"));

        assertEquals("04", response.get("RspCode"));
        assertEquals(PaymentStatus.PROCESSING, payment.getStatus());
        verify(payments, never()).save(any());
        verifyNoInteractions(events);
    }

    @Test
    void repeatedIpnForAConfirmedPaymentIsAcknowledgedWithoutApplyingItAgain() throws Exception {
        payment.setStatus(PaymentStatus.SUCCESS);
        when(payments.findLockedByGatewayOrderId("42")).thenReturn(Optional.of(payment));

        Map<String, String> response = service.processVnpayIpn(signedCallback("1250000"));

        assertEquals("02", response.get("RspCode"));
        verify(payments, never()).save(any());
        verifyNoInteractions(events);
    }

    private Map<String, String> signedCallback(String amount) throws Exception {
        TreeMap<String, String> params = new TreeMap<>();
        params.put("vnp_TmnCode", "TESTCODE");
        params.put("vnp_TxnRef", "42");
        params.put("vnp_Amount", amount);
        params.put("vnp_ResponseCode", "00");
        params.put("vnp_TransactionStatus", "00");
        params.put("vnp_TransactionNo", "VNPAY-TXN-100");
        String hashData = params.entrySet().stream()
                .map(entry -> encode(entry.getKey()) + "=" + encode(entry.getValue()))
                .collect(java.util.stream.Collectors.joining("&"));
        Mac hmac = Mac.getInstance("HmacSHA512");
        hmac.init(new SecretKeySpec(HASH_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
        params.put("vnp_SecureHash", HexFormat.of().formatHex(hmac.doFinal(hashData.getBytes(StandardCharsets.UTF_8))));
        return params;
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
