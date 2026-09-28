package com.zou.modal;


import com.zou.domain.BookLoanStatus;
import com.zou.domain.BookLoanType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookLoan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne
    private User user;

    @JoinColumn(nullable = false)
    @ManyToOne
    private Book book;

    @Enumerated(EnumType.STRING)
    private BookLoanType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookLoanStatus status;

    @Column(nullable = false)
    private LocalDate checkoutDate;

    private LocalDate dueDate;

    private LocalDate returnDate;

    @Column(nullable = false)
    @Builder.Default
    private Integer returnCount=0;

    @Column(nullable = false)
    @Builder.Default
    private Integer maxRenewals=2;

    // Fine records reference this loan through the separate Fine entity.

    @Column(length = 500)
    private String notes;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isOverdue=false;

    @Column(nullable = false)
    @Builder.Default
    private Integer overdueDays =0;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public boolean isActive(){
        return status==BookLoanStatus.CHECKED_OUT
                || status==BookLoanStatus.OVERDUE;
    }

    public boolean canRenew(){
        return status == BookLoanStatus.CHECKED_OUT
                && !isOverdue
                && returnCount<maxRenewals;
    }
}
