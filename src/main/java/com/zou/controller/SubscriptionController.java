package com.zou.controller;

import com.zou.exception.SubscriptionException;
import com.zou.payload.dto.SubscriptionDTO;
import com.zou.payload.request.SubscriptionPurchaseRequest;
import com.zou.payload.response.ApiResponse;
import com.zou.payload.response.PaymentInitiateResponse;
import com.zou.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribeToSubscription(
            @Valid @RequestBody SubscriptionPurchaseRequest purchase,
            HttpServletRequest request
    ) throws Exception{
      PaymentInitiateResponse dto = subscriptionService.subscribe(purchase, request.getRemoteAddr());
      return ResponseEntity.ok(dto);
    }

    @GetMapping("/user/active")
    public ResponseEntity<?> getUsersActiveSubscriptions(
            @RequestParam(required = false) Long userId
    ) throws Exception {
        SubscriptionDTO dto = subscriptionService.getUsersActiveSubscriptions(userId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/admin")
    public ResponseEntity<?> getAllSubscriptions(@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="20") int size){
        Pageable pageable = PageRequest.of(page, size);
        List<SubscriptionDTO> dtoList = subscriptionService.getAllSubscriptions(pageable);
        return ResponseEntity.ok(dtoList);
    }

    @PostMapping("/admin/deactivate-expired")
    public ResponseEntity<?> deactivateExpiredSubscriptions() throws Exception {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        subscriptionService.deactivateExpiredSubscriptions();
        ApiResponse res = new ApiResponse("Task done!", true);
        return ResponseEntity.ok(res);
    }

    @Deprecated
    @GetMapping("/admin/deactivate-expired")
    public ResponseEntity<?> deactivateExpiredSubscriptionsLegacy() throws Exception {
        return deactivateExpiredSubscriptions();
    }

    @PostMapping("/cancel/{subscriptionId}")
    public ResponseEntity<?> cancelSubscription(
            @PathVariable Long subscriptionId,
            @RequestParam(required = false) String reason) throws SubscriptionException {
        SubscriptionDTO subscription = subscriptionService.cancelSubscription(subscriptionId, reason);
        return ResponseEntity.ok(subscription);
    }

    @PostMapping("/activate")
    public ResponseEntity<?> activateSubscription(
            @RequestParam Long subscriptionId,
            @RequestParam Long paymentId) throws SubscriptionException {
        SubscriptionDTO subscription = subscriptionService.activeSubscription(subscriptionId, paymentId);
        return ResponseEntity.ok(subscription);
    }
}
