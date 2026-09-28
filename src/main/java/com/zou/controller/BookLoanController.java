package com.zou.controller;

import com.zou.domain.BookLoanStatus;
import com.zou.payload.dto.BookDTO;
import com.zou.payload.dto.BookLoanDTO;
import com.zou.payload.request.BookLoanSearchRequest;
import com.zou.payload.request.CheckinRequest;
import com.zou.payload.request.CheckoutRequest;
import com.zou.payload.request.RenewalRequest;
import com.zou.payload.response.ApiResponse;
import com.zou.payload.response.PageResponse;
import com.zou.service.BookLoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book-loans")
public class BookLoanController {

    private final BookLoanService bookLoanService;

    @PostMapping("/checkout")
    public ResponseEntity<?> checkoutBook(
            @Valid @RequestBody CheckoutRequest checkoutRequest) throws Exception {
        BookLoanDTO bookLoan = bookLoanService.checkoutBook(checkoutRequest);
        return new ResponseEntity<>(bookLoan, HttpStatus.CREATED);
    }

    @PostMapping("/checkout/user/{userId}")
    public ResponseEntity<?> checkoutBookForUser(
            @PathVariable Long userId,
            @Valid @RequestBody CheckoutRequest checkoutRequest) throws Exception {
        BookLoanDTO bookLoan = bookLoanService
                .checkoutBookForUser(userId, checkoutRequest);
        return new ResponseEntity<>(bookLoan, HttpStatus.CREATED);
    }

    @PostMapping("/checkin")
    public ResponseEntity<?> checkin(
            @Valid @RequestBody CheckinRequest checkinRequest
    ) throws Exception {
        BookLoanDTO bookLoan = bookLoanService
                .checkinBook(checkinRequest);
        return new ResponseEntity<>(bookLoan, HttpStatus.CREATED);
    }

    @PostMapping("/renew")
    public ResponseEntity<?> renew(
            @Valid @RequestBody RenewalRequest  renewalRequest
            ) throws Exception {
        BookLoanDTO bookLoan = bookLoanService
                .renewCheckout(renewalRequest);
        return new ResponseEntity<>(bookLoan, HttpStatus.OK);
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyBookLoans(
            @RequestParam(required = false)BookLoanStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
            ) throws Exception {
        PageResponse<BookLoanDTO> bookLoans = bookLoanService
                .getMyBookLoans(status, page, size);
        return ResponseEntity.ok(bookLoans);
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchAllBookLoans(
            @RequestBody BookLoanSearchRequest bookLoanSearchRequest
    ) throws Exception {
        PageResponse<BookLoanDTO> bookLoans = bookLoanService
                .getBookLoans(bookLoanSearchRequest);
        return ResponseEntity.ok(bookLoans);
    }

    @PostMapping({"/admin/update-overdue", "/admin/udpate-overdue"})
    public ResponseEntity<?> updateOverdueBookLoans() throws Exception {
        int updateCount = bookLoanService.updateOverdueBookLoan();
        return ResponseEntity.ok(
                new ApiResponse(
                        "over due book loans are updated", true
                )
        );
    }
}

