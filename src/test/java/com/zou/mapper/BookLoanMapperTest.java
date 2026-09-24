package com.zou.mapper;

import com.zou.domain.BookLoanStatus;
import com.zou.modal.BookLoan;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BookLoanMapperTest {
    @Test
    void remainingDaysAreCalculatedFromDueDate() {
        BookLoan loan = BookLoan.builder().id(1L).status(BookLoanStatus.CHECKED_OUT)
                .checkoutDate(LocalDate.now().minusDays(3)).dueDate(LocalDate.now().plusDays(8))
                .isOverdue(false).overdueDays(0).returnCount(0).maxRenewals(2).build();
        assertEquals(8L, new BookLoanMapper().toDTO(loan).getRemainingDays());
    }
}
