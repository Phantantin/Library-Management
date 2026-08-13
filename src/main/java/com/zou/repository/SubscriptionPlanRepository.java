package com.zou.repository;

import com.zou.modal.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    Boolean existsByPlanCode(String planCode);

    SubscriptionPlan findByPlanCode(String planCode);
}
