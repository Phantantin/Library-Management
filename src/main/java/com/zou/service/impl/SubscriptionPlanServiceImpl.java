package com.zou.service.impl;

import com.zou.mapper.SubscriptionPlanMapper;
import com.zou.modal.SubscriptionPlan;
import com.zou.modal.User;
import com.zou.payload.dto.SubscriptionPlanDTO;
import com.zou.repository.SubscriptionPlanRepository;
import com.zou.repository.SubscriptionRepository;
import com.zou.service.SubscriptionPlanService;
import com.zou.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {
    private final SubscriptionPlanRepository planRepository;
    private final SubscriptionPlanMapper planMapper;
    private final UserService userService;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;

//    @Override
//    public SubscriptionPlanDTO createSubscriptionPlan(SubscriptionPlanDTO planDTO) throws Exception {
//
//        if(planRepository.existsByPlanCode(planDTO.getPlanCode())){
//            throw new Exception("Plan code is already exits");
//        }
//        SubscriptionPlan plan = planMapper.toEntity(planDTO);
//
//        User currentUser = userService.getCurrentUser();
//        plan.setCreatedBy(currentUser.getFullName());
//        plan.setUpdatedBy(currentUser.getFullName());
//
//        SubscriptionPlan savedPlan = planRepository.save(plan);
//        return planMapper.toDTO(savedPlan);
//    }


    @Override
    public SubscriptionPlanDTO createSubscriptionPlan(
            SubscriptionPlanDTO planDTO
    ) throws Exception {

        if (planRepository.existsByPlanCode(planDTO.getPlanCode())) {
            throw new Exception(
                    "Plan code already exists: " + planDTO.getPlanCode()
            );
        }

        User currentUser = userService.getCurrentUser();

        if (currentUser == null) {
            throw new Exception("Current user is null");
        }

        SubscriptionPlan plan = planMapper.toEntity(planDTO);

        String currentUserName = currentUser.getFullName();

        if (currentUserName == null || currentUserName.isBlank()) {
            currentUserName = currentUser.getEmail();
        }

        plan.setCreatedBy(currentUserName);
        plan.setUpdatedBy(currentUserName);

        SubscriptionPlan savedPlan = planRepository.save(plan);

        return planMapper.toDTO(savedPlan);
    }

    @Override
    public SubscriptionPlanDTO updateSubscriptionPlan(Long planId, SubscriptionPlanDTO planDTO) throws Exception {
       SubscriptionPlan existingPlan = planRepository.findById(planId).orElseThrow(
               ()-> new Exception("Plan not found!")
       );

       planMapper.updateEntity(existingPlan, planDTO);
       User currentUser = userService.getCurrentUser();
       existingPlan.setUpdatedBy(currentUser.getFullName());
       SubscriptionPlan updatedPlan = planRepository.save(existingPlan);
               
        return planMapper.toDTO(updatedPlan);
    }

    @Override
    public void deleteSubscriptionPlan(Long planId) throws Exception {
        SubscriptionPlan existingPlan = planRepository.findById(planId).orElseThrow(
                ()-> new Exception("Plan not found!")
        );
        planRepository.delete(existingPlan);
    }

    @Override
    public List<SubscriptionPlanDTO> getAllSubscriptionPlan() {
        List<SubscriptionPlan> planList = planRepository.findAll();
        return planList.stream().map(
                planMapper::toDTO
        ).collect(Collectors.toList());
    }

    @Override
    public SubscriptionPlan getBySubscriptionPlanCode(String subscriptionPlanCode) throws Exception {
        SubscriptionPlan plan= subscriptionPlanRepository.findByPlanCode(subscriptionPlanCode);

        if(plan==null){
            throw new Exception("Plan not found!");
        }
        return plan;
    }
}
