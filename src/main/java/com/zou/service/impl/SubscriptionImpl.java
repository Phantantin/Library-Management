package com.zou.service.impl;

import com.zou.domain.PaymentGateway;
import com.zou.domain.PaymentType;
import com.zou.exception.SubscriptionException;
import com.zou.mapper.SubscriptionMapper;
import com.zou.modal.Subscription;
import com.zou.modal.SubscriptionPlan;
import com.zou.modal.User;
import com.zou.payload.dto.SubscriptionDTO;
import com.zou.payload.request.PaymentInitiateRequest;
import com.zou.payload.request.SubscriptionPurchaseRequest;
import com.zou.payload.response.PaymentInitiateResponse;
import com.zou.repository.SubscriptionPlanRepository;
import com.zou.repository.SubscriptionRepository;
import com.zou.service.PaymentService;
import com.zou.service.SubscriptionService;
import com.zou.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class SubscriptionImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final UserService userService;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final PaymentService paymentService;
    private final com.zou.repository.PaymentRepository payments;
    private final com.zou.service.AccessService access;

    @Override
    public PaymentInitiateResponse subscribe(SubscriptionPurchaseRequest purchase, String clientIp) throws Exception {
        User user = userService.getCurrentUser();

        SubscriptionPlan plan = subscriptionPlanRepository
                .findById(purchase.getPlanId()).orElseThrow(
                        () -> new  Exception("Plan not found!")
                );

        if(!Boolean.TRUE.equals(plan.getIsActive())) throw new Exception("Plan is inactive");
        if (!"VND".equalsIgnoreCase(plan.getCurrency())) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "VNPAY can only accept subscription plans priced in VND."
            );
        }
        // Optional<Sub>

        SubscriptionDTO draft = new SubscriptionDTO();
        draft.setPlanId(plan.getId());
        Subscription subscription = subscriptionMapper.toEntity(draft, plan, user);
        subscription.initializeFromPlan();
        subscription.setIsActive(false);
        Subscription savedSubscription = subscriptionRepository.save(subscription);
        // Create the provider payment only after the inactive subscription is persisted.

        PaymentInitiateRequest paymentInitiateRequest = PaymentInitiateRequest
                .builder()
                .userId(user.getId())
                .subscriptionId(savedSubscription.getId())
                .paymentType(PaymentType.MEMBERSHIP)
                .gateway(PaymentGateway.VNPAY)
                .amount(savedSubscription.getPrice())
                .description("library Subscription - " + plan.getName())
                .paymentMethod(purchase.getPaymentMethod())
                .ipAddress(clientIp)
                .build();

        return paymentService.initiatePayment(paymentInitiateRequest);

    }

    @Override
    public SubscriptionDTO getUsersActiveSubscriptions(Long userId) throws Exception {
        User user = userId == null ? userService.getCurrentUser() : userService.findById(userId);
        access.ownerOrAdmin(user.getId());
        Subscription subscription =  subscriptionRepository
                .findActiveSubscriptionByUserId(user.getId(), LocalDate.now())
                .orElseThrow(() -> new  Exception("No active subscription found!"));
        return subscriptionMapper.toDTO(subscription);
    }

    @Override
    public SubscriptionDTO cancelSubscription(Long subscriptionId, String reason) throws SubscriptionException {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(()-> new SubscriptionException(
                        "Subscription not found with Id: " + subscriptionId));
        access.ownerOrAdmin(subscription.getUser().getId());
        if(!subscription.getIsActive()){
            throw new SubscriptionException("Subscription is already inactive.");
        }

        // Mark as cancelled
        subscription.setIsActive(false);
        subscription.setCancelledAt(LocalDateTime.now());
        subscription.setCancellationReason(reason !=null ? reason : "Cancelled by user");
        subscription =  subscriptionRepository.save(subscription);

        return subscriptionMapper.toDTO(subscription);
    }

    @Override
    public SubscriptionDTO activeSubscription(Long subscriptionId, Long paymentId) throws SubscriptionException {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(
                        ()->new SubscriptionException("Subscription not found by id!")
                );
        // Activation is permitted only for a persisted verified payment for this subscription.
        var payment = payments.findById(paymentId).orElseThrow(() -> new SubscriptionException("Payment not found"));
        if(payment.getStatus()!=com.zou.domain.PaymentStatus.SUCCESS || payment.getSubscription()==null
            || !payment.getSubscription().getId().equals(subscriptionId)
            || !payment.getUser().getId().equals(subscription.getUser().getId()))
            throw new SubscriptionException("Verified subscription payment required");
        if(Boolean.TRUE.equals(subscription.getIsActive()) || subscription.getCancelledAt()!=null) return subscriptionMapper.toDTO(subscription);
        subscription.setStartDate(LocalDate.now()); subscription.calculateEndDate();
        subscription.setIsActive(true);
        subscription = subscriptionRepository.save(subscription);
        return subscriptionMapper.toDTO(subscription);
    }

    @Override
    public List<SubscriptionDTO> getAllSubscriptions(Pageable pageable) {
        List<Subscription> subscriptions = subscriptionRepository.findAll(pageable).getContent();
        return subscriptionMapper.toDTOList(subscriptions);
    }

    @Override
    public void deactivateExpiredSubscriptions() throws Exception {
        List<Subscription> expiredSubscriptions = subscriptionRepository
                .findExpireActiveSubscriptions(LocalDate.now());

        for (Subscription subscription : expiredSubscriptions) {
            subscription.setIsActive(false);
            subscriptionRepository.save(subscription);
        }
    }
}
