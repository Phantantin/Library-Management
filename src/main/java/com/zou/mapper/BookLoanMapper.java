package com.zou.mapper;

import com.zou.modal.BookLoan;
import com.zou.payload.dto.BookLoanDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class BookLoanMapper {

    public BookLoanDTO toDTO(BookLoan bookLoan){
        if(bookLoan==null){
            return null;
        }

        BookLoanDTO dto = new BookLoanDTO();
        dto.setId(bookLoan.getId());

        // User information
        if(bookLoan.getUser() !=null){
            dto.setUserId(bookLoan.getUser().getId());
            dto.setUserName(bookLoan.getUser().getFullName());
            dto.setUserEmail(bookLoan.getUser().getEmail());
        }

        // Bool information
        if(bookLoan.getBook() !=null){
            dto.setBookId(bookLoan.getBook().getId());
            dto.setBookTitle(bookLoan.getBook().getTitle());
            dto.setBookIsbn(bookLoan.getBook().getIsbn());
            dto.setBookAuthor(bookLoan.getBook().getAuthor());
            dto.setBookCoverImage(bookLoan.getBook().getCoverImageUrl());
        }

        // Book loan details
        dto.setBookLoanType(bookLoan.getType());
        dto.setBookLoanStatus(bookLoan.getStatus());
        dto.setCheckoutDate(bookLoan.getCheckoutDate());
        dto.setDueDate(bookLoan.getDueDate());
        dto.setRemainingDays(
                ChronoUnit.DAYS.between(
                        LocalDate.now(),
                        bookLoan.getCheckoutDate()
                )
        );

        dto.setReturnDate(bookLoan.getReturnDate());
        dto.setRenewalCount(bookLoan.getReturnCount());
        dto.setMaxRenewals(bookLoan.getMaxRenewals());

        dto.setNotes(bookLoan.getNotes());
        dto.setIsOverdue(bookLoan.getIsOverdue());
        dto.setOverdueDays(bookLoan.getOverdueDays());
        dto.setCreatedAt(bookLoan.getCreatedAt());
        dto.setUpdatedAt(bookLoan.getUpdatedAt());

        return dto;
    }
}
