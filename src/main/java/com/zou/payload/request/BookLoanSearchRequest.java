package com.zou.payload.request;

import com.zou.domain.BookLoanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookLoanSearchRequest {

    private Long userId;
    private Long bookId;
    private BookLoanStatus status;
    private Boolean overdueOnly;
    private Boolean unpaidFinesOnly;
    private LocalDate startDate;
    private LocalDate endDate;
    @Builder.Default private Integer page=0;
    @Builder.Default private Integer size=20;
    @Builder.Default private String sortBy="createdAt";
    @Builder.Default private String sortDirection="DESC";
}
