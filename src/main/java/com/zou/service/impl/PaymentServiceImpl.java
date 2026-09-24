package com.zou.service.impl;
import com.zou.domain.*;
import com.zou.event.publisher.PaymentEventPublisher;
import com.zou.mapper.PaymentMapper;
import com.zou.modal.*;
import com.zou.payload.dto.PaymentDTO;
import com.zou.payload.request.*;
import com.zou.payload.response.PaymentInitiateResponse;
import com.zou.repository.*;
import com.zou.service.*;
import com.zou.service.gateway.RazorpayService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;
@Service @RequiredArgsConstructor
@Transactional(rollbackFor=Exception.class)
public class PaymentServiceImpl implements PaymentService {
    private final UserRepository users;
    private final SubscriptionRepository subscriptions;
    private final PaymentRepository payments;
    private final FineRepository fines;
    private final RazorpayService gateway;
    private final PaymentMapper mapper;
    private final PaymentEventPublisher events;
    private final AccessService access;
    @Value("${app.payment.currency:VND}") private String fineCurrency;
    @Override public PaymentInitiateResponse initiatePayment(PaymentInitiateRequest req) throws Exception {
        if(req.getGateway()!=PaymentGateway.RAZORPAY) throw new IllegalArgumentException("Unsupported payment gateway");
        access.ownerOrAdmin(req.getUserId());
        var user=users.findById(req.getUserId()).orElseThrow();
        Payment payment=new Payment();
        payment.setUser(user); payment.setPaymentType(req.getPaymentType()); payment.setGateway(req.getGateway());
        payment.setAmount(req.getAmount()); payment.setCurrency(fineCurrency); payment.setDescription(req.getDescription());
        payment.setStatus(PaymentStatus.PENDING); payment.setTransactionId("TXN_"+UUID.randomUUID());
        if(req.getSubscriptionId()!=null) {
            var sub=subscriptions.findById(req.getSubscriptionId()).orElseThrow();
            if(!sub.getUser().getId().equals(user.getId())) throw new org.springframework.security.access.AccessDeniedException("Invalid owner");
            payment.setSubscription(sub); payment.setAmount(sub.getPrice()); payment.setCurrency(sub.getPlan().getCurrency());
        }
        if(req.getFineId()!=null) {
            var fine=fines.findById(req.getFineId()).orElseThrow();
            if(!fine.getUser().getId().equals(user.getId())) throw new org.springframework.security.access.AccessDeniedException("Invalid owner");
            payment.setFine(fine); payment.setAmount(fine.getAmount());
        }
        if(payment.getAmount()==null || payment.getAmount()<=0) throw new IllegalArgumentException("Invalid payment amount");
        payment=payments.save(payment);
        var link=gateway.createPaymentLink(user,payment);
        payment.setGatewayOrderId(link.getPayment_link_id()); payment.setStatus(PaymentStatus.PROCESSING);
        payments.save(payment);
        return PaymentInitiateResponse.builder().paymentId(payment.getId()).gateway(payment.getGateway())
            .transactionId(payment.getTransactionId()).checkoutUrl(link.getPayment_link_url()).amount(payment.getAmount())
            .description(payment.getDescription()).success(true).message("Continue to secure checkout").build();
    }
    @Override public PaymentDTO verifyPayment(PaymentVerifyRequest req) throws Exception {
        JSONObject details=gateway.fetchPaymentDetails(req.getRazorpayPaymentId());
        JSONObject notes=details.optJSONObject("notes");
        if(notes==null) throw new IllegalArgumentException("Payment metadata missing");
        Long id=Long.valueOf(notes.getString("paymentId"));
        Payment payment=payments.findLockedById(id).orElseThrow();
        access.ownerOrAdmin(payment.getUser().getId());
        if(payment.getStatus()==PaymentStatus.SUCCESS) {
            if(!req.getRazorpayPaymentId().equals(payment.getGatewayPaymentId())) throw new IllegalArgumentException("Payment reference mismatch");
            return mapper.toDTO(payment);
        }
        if(payment.getGateway()!=PaymentGateway.RAZORPAY || !"captured".equals(details.optString("status"))
            || details.optLong("amount")!=payment.getAmount() || !payment.getCurrency().equals(details.optString("currency")))
            throw new IllegalArgumentException("Payment has not been captured for the expected amount and currency");
        JSONObject link=gateway.fetchLink(payment.getGatewayOrderId());
        boolean linked=false;
        var entries=link.optJSONArray("payments");
        if(entries!=null) for(int i=0;i<entries.length();i++) {
            if(req.getRazorpayPaymentId().equals(entries.getJSONObject(i).optString("payment_id"))) linked=true;
        }
        if(!linked || !"paid".equals(link.optString("status"))) throw new IllegalArgumentException("Payment does not belong to the expected checkout link");
        if(payment.getFine()!=null) {
            Fine fine=payment.getFine();
            if(fine.getStatus()==FineStatus.PAID || fine.getStatus()==FineStatus.WAIVED) throw new IllegalArgumentException("Fine is already settled; contact the library about this payment");
            fine.applyPayment(payment.getAmount()); fine.setTransactionId(payment.getTransactionId()); fines.save(fine);
        }
        payment.setGatewayPaymentId(req.getRazorpayPaymentId()); payment.setStatus(PaymentStatus.SUCCESS); payment.setCompletedAt(LocalDateTime.now());
        payments.save(payment); events.publishPaymentSuccessEvent(payment);
        return mapper.toDTO(payment);
    }
    @Override @Transactional(readOnly=true)
    public Page<PaymentDTO> getAllPayments(Pageable pageable) {return payments.findAll(pageable).map(mapper::toDTO);}
}
