package com.zou.service;

import com.zou.modal.SubscriptionPlan;
import com.zou.payload.dto.SubscriptionPlanDTO;

import java.util.List;

public interface SubscriptionPlanService {

    SubscriptionPlanDTO createSubscriptionPlan(SubscriptionPlanDTO planDTO) throws Exception;

    SubscriptionPlanDTO updateSubscriptionPlan(Long planId, SubscriptionPlanDTO planDTO) throws Exception;

    void deleteSubscriptionPlan(Long planId) throws Exception;

    List<SubscriptionPlanDTO> getAllSubscriptionPlan();

    SubscriptionPlan getBySubscriptionPlanCode(String subscriptionPlanCode) throws Exception;



}
