package com.zou.service.gateway;

import com.zou.modal.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.Normalizer;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class VnpayService {
    private static final ZoneId VIETNAM_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter VNPAY_DATE = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Value("${vnpay.tmn-code:}") private String tmnCode;
    @Value("${vnpay.hash-secret:}") private String hashSecret;
    @Value("${vnpay.payment-url:https://sandbox.vnpayment.vn/paymentv2/vpcpay.html}") private String paymentUrl;
    @Value("${app.backend-url:http://localhost:5000}") private String backendUrl;
    @Value("${app.frontend-url:http://localhost:3000}") private String frontendUrl;

    public String createPaymentUrl(Payment payment, String clientIp, String paymentMethod) {
        requireConfiguration();
        if (!"VND".equalsIgnoreCase(payment.getCurrency())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "VNPAY only supports VND payments.");
        }

        long amount;
        try {
            amount = Math.multiplyExact(payment.getAmount(), 100L);
        } catch (ArithmeticException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment amount is out of range.");
        }
        if (amount <= 0 || Long.toString(amount).length() > 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment amount is out of range.");
        }

        ZonedDateTime now = ZonedDateTime.now(VIETNAM_ZONE);
        TreeMap<String, String> params = new TreeMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", tmnCode.trim());
        params.put("vnp_Amount", Long.toString(amount));
        params.put("vnp_CreateDate", now.format(VNPAY_DATE));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_IpAddr", normalizeIp(clientIp));
        params.put("vnp_Locale", "vn");
        params.put("vnp_OrderInfo", orderInfo(payment.getDescription()));
        params.put("vnp_OrderType", "other");
        params.put("vnp_ReturnUrl", trimTrailingSlash(backendUrl) + "/api/payments/vnpay/return");
        params.put("vnp_ExpireDate", now.plusMinutes(15).format(VNPAY_DATE));
        params.put("vnp_TxnRef", payment.getGatewayOrderId());

        String bankCode = normalizePaymentMethod(paymentMethod);
        if (!bankCode.isBlank()) params.put("vnp_BankCode", bankCode);

        String secureHash = sign(params);
        params.put("vnp_SecureHash", secureHash);
        return paymentUrl + "?" + encodeParams(params);
    }

    public boolean hasValidSignature(Map<String, String> params) {
        if (hashSecret == null || hashSecret.isBlank()) return false;
        String providedHash = params.get("vnp_SecureHash");
        if (providedHash == null || !providedHash.matches("(?i)[0-9a-f]{128}")) return false;
        String expectedHash = sign(signedParams(params));
        return MessageDigest.isEqual(
                expectedHash.getBytes(StandardCharsets.US_ASCII),
                providedHash.toLowerCase(java.util.Locale.ROOT).getBytes(StandardCharsets.US_ASCII)
        );
    }

    public boolean hasExpectedMerchant(Map<String, String> params) {
        return tmnCode != null && !tmnCode.isBlank() && tmnCode.trim().equals(params.get("vnp_TmnCode"));
    }

    public String createFrontendReturnUrl(long paymentId, String result) {
        String base = trimTrailingSlash(frontendUrl) + "/payment-success/" + paymentId;
        return base + "?gateway=VNPAY&result=" + encode(result);
    }

    public static String normalizePaymentMethod(String paymentMethod) {
        if (paymentMethod == null || paymentMethod.isBlank() || "ALL".equalsIgnoreCase(paymentMethod)) return "";
        if ("QR".equalsIgnoreCase(paymentMethod) || "VNPAYQR".equalsIgnoreCase(paymentMethod)) return "VNPAYQR";
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported VNPAY payment method.");
    }

    private void requireConfiguration() {
        if (tmnCode == null || tmnCode.isBlank() || hashSecret == null || hashSecret.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "VNPAY payment is not configured. Contact the library administrator.");
        }
        if (paymentUrl == null || !paymentUrl.startsWith("https://")) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "VNPAY payment URL is not configured.");
        }
        if (backendUrl == null || !backendUrl.startsWith("https://")) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The public HTTPS backend URL is not configured for VNPAY.");
        }
    }

    private String sign(Map<String, String> params) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA512");
            hmac.init(new SecretKeySpec(hashSecret.trim().getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            return HexFormat.of().formatHex(hmac.doFinal(hashData(params).getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to sign VNPAY request", exception);
        }
    }

    private static TreeMap<String, String> signedParams(Map<String, String> params) {
        return params.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("vnp_"))
                .filter(entry -> !"vnp_SecureHash".equals(entry.getKey()) && !"vnp_SecureHashType".equals(entry.getKey()))
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (first, second) -> second, TreeMap::new));
    }

    private static String hashData(Map<String, String> params) {
        return params.entrySet().stream()
                .map(entry -> encode(entry.getKey()) + "=" + encode(entry.getValue()))
                .collect(Collectors.joining("&"));
    }

    private static String encodeParams(Map<String, String> params) {
        return params.entrySet().stream()
                .map(entry -> encode(entry.getKey()) + "=" + encode(entry.getValue()))
                .collect(Collectors.joining("&"));
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static String normalizeIp(String value) {
        if (value == null || value.isBlank()) return "127.0.0.1";
        String ip = value.trim();
        int comma = ip.indexOf(',');
        if (comma >= 0) ip = ip.substring(0, comma).trim();
        return ip.length() <= 45 ? ip : "127.0.0.1";
    }

    private static String orderInfo(String value) {
        String ascii = Normalizer.normalize(value == null ? "Library payment" : value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^A-Za-z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();
        if (ascii.isBlank()) ascii = "Library payment";
        return ascii.length() <= 255 ? ascii : ascii.substring(0, 255);
    }

    private static String trimTrailingSlash(String value) {
        return value.replaceAll("/+$", "");
    }
}
