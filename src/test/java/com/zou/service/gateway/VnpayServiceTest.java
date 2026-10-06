package com.zou.service.gateway;

import com.zou.domain.PaymentGateway;
import com.zou.domain.PaymentType;
import com.zou.modal.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class VnpayServiceTest {
    private VnpayService service;

    @BeforeEach
    void setUp() {
        service = new VnpayService();
        ReflectionTestUtils.setField(service, "tmnCode", "TESTCODE");
        ReflectionTestUtils.setField(service, "hashSecret", "unit-test-only-secret");
        ReflectionTestUtils.setField(service, "paymentUrl", "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html");
        ReflectionTestUtils.setField(service, "backendUrl", "https://library-api.example.test");
        ReflectionTestUtils.setField(service, "frontendUrl", "https://library.example.test");
    }

    @Test
    void createsSignedVnpayQrCheckoutAndRejectsTampering() {
        Payment payment = Payment.builder()
                .id(42L)
                .gateway(PaymentGateway.VNPAY)
                .paymentType(PaymentType.MEMBERSHIP)
                .gatewayOrderId("42")
                .amount(12_500L)
                .currency("VND")
                .description("Library membership")
                .build();

        String checkoutUrl = service.createPaymentUrl(payment, "203.0.113.8", "QR");
        assertTrue(checkoutUrl.startsWith("https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?"));
        Map<String, String> params = Arrays.stream(checkoutUrl.substring(checkoutUrl.indexOf('?') + 1).split("&"))
                .map(part -> part.split("=", 2))
                .collect(Collectors.toMap(
                        part -> URLDecoder.decode(part[0], StandardCharsets.UTF_8),
                        part -> URLDecoder.decode(part[1], StandardCharsets.UTF_8)
                ));

        assertEquals("VNPAYQR", params.get("vnp_BankCode"));
        assertEquals("1250000", params.get("vnp_Amount"));
        assertEquals("42", params.get("vnp_TxnRef"));
        assertTrue(service.hasValidSignature(params));
        assertTrue(service.hasExpectedMerchant(params));

        params.put("vnp_Amount", "100");
        assertFalse(service.hasValidSignature(params));
    }

    @Test
    void allMethodsDoesNotForceQrAndUnsupportedCurrencyIsRejected() {
        Payment payment = Payment.builder()
                .id(7L)
                .gatewayOrderId("7")
                .amount(1_000L)
                .currency("VND")
                .description("Fine")
                .build();
        String url = service.createPaymentUrl(payment, "203.0.113.8", "ALL");
        assertFalse(url.contains("vnp_BankCode"));

        payment.setCurrency("INR");
        assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> service.createPaymentUrl(payment, "203.0.113.8", "ALL"));
    }
}
