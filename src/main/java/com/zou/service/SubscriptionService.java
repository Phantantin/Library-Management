package com.zou.service;

import com.zou.exception.SubscriptionException;
import com.zou.payload.dto.SubscriptionDTO;
import com.zou.payload.response.PaymentInitiateResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubscriptionService {

    PaymentInitiateResponse subscribe(SubscriptionDTO subscriptionDTO) throws Exception;

    SubscriptionDTO getUsersActiveSubscriptions(Long userId) throws Exception;

    SubscriptionDTO cancelSubscription(Long subscriptionId, String reason) throws SubscriptionException;

    SubscriptionDTO activeSubscription(Long subscriptionId, Long paymentId) throws SubscriptionException;

    List<SubscriptionDTO> getAllSubscriptions(Pageable pageable);

     void deactivateExpiredSubscriptions() throws Exception;
}
