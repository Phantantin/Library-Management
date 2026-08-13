package com.zou.payload.request;


import com.zou.domain.BookLoanStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckinRequest {

    @NotNull(message = "Book loan ID mandatory")
    private Long bookLoanId;

    private BookLoanStatus condition = BookLoanStatus.RETURNED;

    private String notes;

}
