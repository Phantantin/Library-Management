package com.zou.mapper;

import com.zou.exception.SubscriptionException;
import com.zou.modal.Subscription;
import com.zou.modal.SubscriptionPlan;
import com.zou.modal.User;
import com.zou.payload.dto.SubscriptionDTO;
import com.zou.repository.SubscriptionPlanRepository;
import com.zou.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class SubscriptionMapper {

    private final UserRepository userRepository;
    private final SubscriptionPlanRepository planRepository;


    public SubscriptionDTO toDTO(Subscription subscription) {
        if(subscription == null) {
            return null;
        }
        SubscriptionDTO dto = new SubscriptionDTO();
        dto.setId(subscription.getId());

        // dto.setId(String.valueOf(subscription.getId()));

        // User information
        if(subscription.getUser() != null) {
            dto.setUserId(subscription.getUser().getId());
            dto.setUserName(subscription.getUser().getFullName());
            dto.setUserEmail(subscription.getUser().getEmail());
        }

        // Plan information
        if(subscription.getPlan() != null) {
            dto.setPlanId(subscription.getPlan().getId());
        }
        dto.setPlanName(subscription.getPlanName());
        dto.setPlanCode(subscription.getPlanCode());
        dto.setPrice(subscription.getPrice());
        dto.setCurrency(subscription.getPlan().getCurrency());
        dto.setStartDate(subscription.getStartDate());
        dto.setEndDate(subscription.getEndDate());
        dto.setIsActive(subscription.getIsActive());
        dto.setMaxBooksAllowed(subscription.getMaxBooksAllowed());
        dto.setMaxDaysPerBook(subscription.getMaxDaysPerBook());
        dto.setAutoRenew(subscription.getAutoRenew());
        dto.setCancelledAt(subscription.getCancelledAt());
        dto.setCancellationReason(subscription.getCancellationReason());
        dto.setNotes(subscription.getNotes());
        dto.setCreatedAt(subscription.getCreatedAt());
        dto.setUpdatedAt(subscription.getUpdatedAt());

        // calculated fields
        dto.setDaysRemaining(subscription.getDaysRemaining());
        dto.setIsValid(subscription.isValid());
        dto.setIsExpired(subscription.isExpired());
        return dto;
    }


    // convert DTO to Subscription entity

    public Subscription  toEntity(SubscriptionDTO dto,
                                  SubscriptionPlan plan ,
                                  User user) throws SubscriptionException {
        if(dto == null) {
            return null;
        }

        Subscription  subscription = new Subscription();

        subscription.setUser(user);
        subscription.setPlan(plan);

        // Map user
//        if(dto.getUserId() != null) {
//            User user = userRepository.findById(dto.getUserId())
//                    .orElseThrow(()-> new SubscriptionException("User not found with Id: " + dto));
//            subscription.setUser(user);
//
//        }

        // Map Plan
//        if(dto.getPlanId() != null) {
//            SubscriptionPlan plan = planRepository.findById(dto.getPlanId())
//                    .orElseThrow(()-> new SubscriptionException("Subscription not found with Id: " + dto));
//            subscription.setPlan(plan);
//        }

//        subscription.setPlanName(dto.getPlanName());
//        subscription.setPlanCode(dto.getPlanCode());
//        subscription.setPrice(dto.getPrice());
//        subscription.setStartDate(dto.getStartDate());
//        subscription.setEndDate(dto.getEndDate());
//        subscription.setIsActive(dto.getIsActive());
//        subscription.setMaxBooksAllowed(dto.getMaxBooksAllowed());
//        subscription.setMaxDaysPerBook(dto.getMaxDaysPerBook());
//        subscription.setAutoRenew(dto.getAutoRenew());
//        subscription.setCancelledAt(dto.getCancelledAt());
//        subscription.setCancellationReason(dto.getCancellationReason());
        subscription.setNotes(dto.getNotes());

        return  subscription;
    }

    // convert list of subscription

    public List<SubscriptionDTO> toDTOList(List<Subscription> subscriptions){
        if(subscriptions == null){
            return null;
        }

        return subscriptions.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
