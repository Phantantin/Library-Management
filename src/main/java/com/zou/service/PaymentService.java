package com.zou.service;

import com.zou.payload.dto.PaymentDTO;
import com.zou.payload.request.PaymentInitiateRequest;
import com.zou.payload.response.PaymentInitiateResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface PaymentService {

    PaymentInitiateResponse initiatePayment(PaymentInitiateRequest req) throws Exception;

    PaymentDTO getPayment(Long paymentId) throws Exception;

    String processVnpayReturn(Map<String, String> params) throws Exception;

    Map<String, String> processVnpayIpn(Map<String, String> params);

    Page<PaymentDTO> getAllPayments(Pageable pageable);
}
