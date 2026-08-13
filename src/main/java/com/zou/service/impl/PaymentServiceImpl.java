package com.zou.service.impl;

import com.zou.domain.PaymentGateway;
import com.zou.domain.PaymentStatus;
import com.zou.domain.PaymentType;
import com.zou.event.publisher.PaymentEventPublisher;
import com.zou.mapper.PaymentMapper;
import com.zou.modal.Payment;
import com.zou.modal.Subscription;
import com.zou.modal.User;
import com.zou.payload.dto.PaymentDTO;
import com.zou.payload.request.PaymentInitiateRequest;
import com.zou.payload.request.PaymentVerifyRequest;
import com.zou.payload.response.PaymentInitiateResponse;
import com.zou.payload.response.PaymentLinkResponse;
import com.zou.repository.PaymentRepository;
import com.zou.repository.SubscriptionRepository;
import com.zou.repository.UserRepository;
import com.zou.service.PaymentService;
import com.zou.service.gateway.RazorpayService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class PaymentServiceImpl implements PaymentService {
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentRepository paymentRepository;
    private final RazorpayService razorpayService;
    private final PaymentMapper paymentMapper;
    private final PaymentEventPublisher paymentEventPublisher;

    @Override
    public PaymentInitiateResponse initiatePayment(PaymentInitiateRequest req) throws Exception {
        User user = userRepository.findById(req.getUserId()).get();

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setPaymentType(req.getPaymentType());
        payment.setGateway(req.getGateway());
        payment.setAmount(req.getAmount());
        payment.setDescription(req.getDescription());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionId("TXN_" + UUID.randomUUID());
        payment.setInitiateAt(LocalDateTime.now());

        if(req.getSubscriptionId() != null) {
            Subscription sub = subscriptionRepository
                    .findById(req.getSubscriptionId())
                    .orElseThrow(()-> new Exception("Subscription not found"));
            payment.setSubscription(sub);
        }
        payment = paymentRepository.save(payment);

        PaymentInitiateResponse response = new PaymentInitiateResponse();

        if(req.getGateway()== PaymentGateway.RAZORPAY) {
            PaymentLinkResponse paymentLinkResponse = razorpayService.createPaymentLink(
                    user, payment
            );
            response = PaymentInitiateResponse.builder()
                    .paymentId(payment.getId())
                    .gateway(payment.getGateway())
                    .checkoutUrl(paymentLinkResponse.getPayment_link_url())
                    .transactionId(paymentLinkResponse.getPayment_link_id())
                    .amount(payment.getAmount())
                    .description(payment.getDescription())
                    .success(true)
                    .message("Payment initiated successfully")
                    .build();
            payment.setGatewayOrderId(paymentLinkResponse.getPayment_link_id());

        }


        payment.setStatus(PaymentStatus.PROCESSING);
        paymentRepository.save(payment);

        return response;
    }

    @Override
    public PaymentDTO verifyPayment(PaymentVerifyRequest req) throws Exception {

        JSONObject paymentDetails = razorpayService.fetchPaymentDetails(
                req.getRazorpayPaymentId()
        );
        JSONObject notes = paymentDetails.getJSONObject("notes");
        // Access specific fields inside notes
        Long paymentId = Long.parseLong(notes.optString("paymentId"));

        Payment payment = paymentRepository.findById(paymentId).get();

        boolean isValid = razorpayService.isValidPayment(req.getRazorpayPaymentId());

        if(PaymentGateway.RAZORPAY == payment.getGateway()) {
            if(isValid) {
                payment.setGatewayPaymentId(req.getRazorpayPaymentId());
            }
        }
        if(isValid){
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setCompletedAt(LocalDateTime.now());
            payment = paymentRepository.save(payment);

            // todo
            paymentEventPublisher.publishPaymentSuccessEvent(payment);
        }

        return paymentMapper.toDTO(payment);
    }

    @Override
    public Page<PaymentDTO> getAllPayments(Pageable pageable) {
        Page<Payment> payments = paymentRepository.findAll(pageable);

        return payments.map(paymentMapper::toDTO);
    }
}
