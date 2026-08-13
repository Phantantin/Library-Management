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

    @Override
    public PaymentInitiateResponse subscribe(SubscriptionDTO subscriptionDTO) throws Exception {
        User user = userService.getCurrentUser();

        SubscriptionPlan plan = subscriptionPlanRepository
                .findById(subscriptionDTO.getPlanId()).orElseThrow(
                        () -> new  Exception("Plan not found!")
                );

        // Optional<Sub>

        Subscription subscription = subscriptionMapper.toEntity(subscriptionDTO, plan, user);
        subscription.initializeFromPlan();
        subscription.setIsActive(false);
        Subscription savedSubscription = subscriptionRepository.save(subscription);
        // create payment todo

        PaymentInitiateRequest paymentInitiateRequest = PaymentInitiateRequest
                .builder()
                .userId(user.getId())
                .subscriptionId(savedSubscription.getId())
                .paymentType(PaymentType.MEMBERSHIP)
                .gateway(PaymentGateway.RAZORPAY)
                .amount(savedSubscription.getPrice())
                .description("library Subscription - " + plan.getName())
                .build();

        return paymentService.initiatePayment(paymentInitiateRequest);

    }

    @Override
    public SubscriptionDTO getUsersActiveSubscriptions(Long userId) throws Exception {
        User user = userService.getCurrentUser();

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
        // verify payment
        subscription.setIsActive(true);
        subscription = subscriptionRepository.save(subscription);
        return subscriptionMapper.toDTO(subscription);
    }

    @Override
    public List<SubscriptionDTO> getAllSubscriptions(Pageable pageable) {
        List<Subscription> subscriptions = subscriptionRepository.findAll();
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
