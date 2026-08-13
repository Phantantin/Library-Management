package com.zou.service;

import com.zou.domain.BookLoanStatus;
import com.zou.payload.dto.BookLoanDTO;
import com.zou.payload.request.BookLoanSearchRequest;
import com.zou.payload.request.CheckinRequest;
import com.zou.payload.request.CheckoutRequest;
import com.zou.payload.request.RenewalRequest;
import com.zou.payload.response.PageResponse;
import org.springframework.data.domain.PageRequest;

public interface BookLoanService {

    BookLoanDTO checkoutBook(CheckoutRequest checkoutRequest) throws Exception;

    BookLoanDTO checkoutBookForUser(Long userId, CheckoutRequest checkoutRequest) throws Exception;

    BookLoanDTO checkinBook(CheckinRequest checkinRequest) throws Exception;

    BookLoanDTO renewCheckout(RenewalRequest renewalRequest) throws Exception;

    PageResponse<BookLoanDTO> getMyBookLoans(BookLoanStatus status,
                                             int page, int size) throws Exception;

    PageResponse<BookLoanDTO> getBookLoans(BookLoanSearchRequest request) throws Exception;

    int updateOverdueBookLoan() throws Exception;
}
