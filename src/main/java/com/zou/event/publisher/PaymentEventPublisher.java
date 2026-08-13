package com.zou.event.publisher;


import com.zou.modal.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishPaymentSuccessEvent(Payment payment) {
        applicationEventPublisher.publishEvent(payment);
    }
}
