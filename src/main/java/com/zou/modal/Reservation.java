package com.zou.modal;


import com.zou.domain.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Book book;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ReservationStatus status= ReservationStatus.PENDING;

    private LocalDateTime reservedAt;

    private LocalDateTime availableAt;

    private LocalDateTime availableUntil;

    @Column(name = "fulfilled_at")
    private LocalDateTime fulfilledAt;

    private LocalDateTime cancelledAt;

    @Column(name = "queue_position")
    private Integer queuePosition;

    @Column(name = "notification_sent", nullable = false)
    private Boolean notificationSent;

    @Column(columnDefinition = "TEXT")
    private String notes;


    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


    public boolean canBeCancelled() {
        return status == ReservationStatus.PENDING
                || status == ReservationStatus.AVAILABLE;
    }

    /**
     * Check if reservation has expired
     */
    public boolean hasExpired() {
        return status == ReservationStatus.AVAILABLE
                && availableUntil != null
                && LocalDateTime.now().isAfter(availableUntil);
    }
}
