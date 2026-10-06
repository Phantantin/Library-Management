package com.zou.service.impl;

import com.zou.domain.BookLoanStatus;
import com.zou.domain.BookLoanType;
import com.zou.exception.BookException;
import com.zou.mapper.BookLoanMapper;
import com.zou.modal.Book;
import com.zou.modal.BookLoan;
import com.zou.modal.User;
import com.zou.payload.dto.BookLoanDTO;
import com.zou.payload.request.BookLoanSearchRequest;
import com.zou.payload.request.CheckinRequest;
import com.zou.payload.request.CheckoutRequest;
import com.zou.payload.request.RenewalRequest;
import com.zou.payload.response.PageResponse;
import com.zou.repository.BookLoanRepository;
import com.zou.repository.BookRepository;
import com.zou.service.BookLoanService;
import com.zou.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.transaction.annotation.Transactional(rollbackFor=Exception.class)
@Service
@RequiredArgsConstructor
public class BookLoanServiceImpl implements BookLoanService {
    private final BookLoanRepository bookLoanRepository;
    private final UserService userService;
    private final BookRepository bookRepository;
    private final BookLoanMapper bookLoanMapper;
    private final com.zou.service.AccessService access;
    private final com.zou.service.ReservationQueueService reservationQueue;

    @Value("${app.loan.max-active-loans:5}")
    private int maxActiveLoans;

    @Value("${app.loan.max-checkout-days:30}")
    private int maxCheckoutDays;

    @Value("${app.loan.max-renewal-days:14}")
    private int maxRenewalDays;

    @Override
    public BookLoanDTO checkoutBook(CheckoutRequest checkoutRequest) throws Exception {

        User user = userService.getCurrentUser();
        return checkoutBookForUser(user.getId(), checkoutRequest);
    }

    @Override
    public BookLoanDTO checkoutBookForUser(Long userId, CheckoutRequest checkoutRequest) throws Exception {
        access.ownerOrAdmin(userId);
        // 1. validate user exit
        User user = userService.findById(userId);
        if (!org.springframework.util.StringUtils.hasText(user.getFullName())
                || !org.springframework.util.StringUtils.hasText(user.getEmail())
                || !org.springframework.util.StringUtils.hasText(user.getPhone())) {
            throw new BookException("Complete your profile before borrowing by adding your full name and phone number.");
        }
        // 2. validate book exists and is available
        Book book = bookRepository.findLockedById(checkoutRequest.getBookId())
                .orElseThrow(()-> new BookException("Book not found"));
        if (checkoutRequest.getCheckoutDays() == null
                || checkoutRequest.getCheckoutDays() < 1
                || checkoutRequest.getCheckoutDays() > maxCheckoutDays) {
            throw new BookException("Checkout days must be between 1 and " + maxCheckoutDays + " days.");
        }


        if(!book.getActive()){
            throw new BookException("Book is not active.");
        }
        if(book.getAvailableCopies()<=0){
            throw new BookException("Book is not available copies.");
        }

        // 4. check if user already has this book checkout
        if(bookLoanRepository.hasActiveCheckout(userId, book.getId())){
            throw new  BookException("Book has already been checked out.");
        }
        // 5. check user's active checkout limit
        long activeCheckouts=bookLoanRepository.countActiveBookLoansByUser(userId);

        if(activeCheckouts>=maxActiveLoans){
            throw new  BookException("You have reached the maximum number of books allowed.");
        }
        // 6. Check for overdue books
        long overdueCount = bookLoanRepository.countOverdueBookLoansByUser(userId);
        if(overdueCount>0){
            throw new Exception("First return old overdue book.");
        }
        // 7. create book loan
        BookLoan bookLoan = BookLoan
                .builder()
                .user(user)
                .book(book)
                .type(BookLoanType.CHECKOUT)
                .status(BookLoanStatus.CHECKED_OUT)
                .checkoutDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(checkoutRequest.getCheckoutDays()))
                .returnCount(0)
                .maxRenewals(2)
                .notes(checkoutRequest.getNotes())
                .isOverdue(false)
                .overdueDays(0)
                .build();

        // 9. Update book available copies
        book.setAvailableCopies(book.getAvailableCopies()-1);
        bookRepository.save(book);

        //10. save book plan
        BookLoan savedBookLoan = bookLoanRepository.save(bookLoan);
        return bookLoanMapper.toDTO(savedBookLoan);
    }

    @Override
    public BookLoanDTO checkinBook(CheckinRequest checkinRequest) throws Exception {
        // 1. validate book loan exit
        BookLoan bookLoan = bookLoanRepository.findLockedById(checkinRequest.getBookLoanId())
                .orElseThrow(()-> new Exception("Book Loan not found"));

        access.ownerOrAdmin(bookLoan.getUser().getId());
        // 2.check if already returned
        if(!bookLoan.isActive()){
            throw new BookException("Book loan is not active.");
        }

        // 3.set return date
        bookLoan.setReturnDate(LocalDate.now());

        // 4.
        BookLoanStatus condition= checkinRequest.getCondition();
        if(condition == null){
            condition = BookLoanStatus.RETURNED;
        }
        if (!java.util.List.of(BookLoanStatus.RETURNED, BookLoanStatus.LOST, BookLoanStatus.DAMAGED).contains(condition)) throw new BookException("Invalid return condition");
        bookLoan.setStatus(condition);

        // Fine assessment remains a staff-controlled workflow.
        bookLoan.setOverdueDays(0);
        bookLoan.setIsOverdue(false);
        // 6.
        bookLoan.setNotes("Book returned by user");
        // 7 update book avai
        if(condition != BookLoanStatus.LOST){
            Book book = bookLoan.getBook();
            book.setAvailableCopies(book.getAvailableCopies()+1);
            bookRepository.save(book);
            reservationQueue.promoteNext(book);
        }

        // 8
        BookLoan savedBookLoan = bookLoanRepository.save(bookLoan);
        return bookLoanMapper.toDTO(savedBookLoan);
    }

    @Override
    public BookLoanDTO renewCheckout(RenewalRequest renewalRequest) throws Exception {

        // 1. validate book loan exit
        BookLoan bookLoan = bookLoanRepository.findLockedById(renewalRequest.getBookLoanId())
                .orElseThrow(()-> new Exception("Book Loan not found"));


        access.ownerOrAdmin(bookLoan.getUser().getId());
        if (renewalRequest.getExtensionDays() == null
                || renewalRequest.getExtensionDays() < 1
                || renewalRequest.getExtensionDays() > maxRenewalDays) {
            throw new BookException("Renewal extension must be between 1 and " + maxRenewalDays + " days.");
        }
        if(bookLoan.getDueDate().isBefore(LocalDate.now())) throw new BookException("Overdue loans cannot be renewed");
        // 2 check if can be renewed
        if(!bookLoan.canRenew()){
            throw new BookException("Book cannot be renewed.");
        }

        // update due date
        bookLoan.setDueDate(bookLoan.getDueDate()
                .plusDays(renewalRequest.getExtensionDays()));
        bookLoan.setReturnCount(bookLoan.getReturnCount()+1);

        bookLoan.setNotes("Book renewed by user");
        BookLoan savedBookLoan = bookLoanRepository.save(bookLoan);

        return bookLoanMapper.toDTO(savedBookLoan);
    }

    @Override
    public PageResponse<BookLoanDTO> getMyBookLoans(BookLoanStatus status, int page, int size) throws Exception {
        User currentUser = userService.getCurrentUser();
        Page<BookLoan> bookLoanPage;
        if(status !=null){
            //
            Pageable pageable = PageRequest.of(page, size, Sort.by("dueDate").ascending());
            bookLoanPage = bookLoanRepository.findByStatusAndUser(
                    status, currentUser, pageable);
        }else{
            //
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            bookLoanPage = bookLoanRepository.findByUserId(currentUser.getId(), pageable);
        }
        return convertToPageResponse(bookLoanPage);
    }



    @Override
    public PageResponse<BookLoanDTO> getBookLoans(BookLoanSearchRequest searchRequest) throws Exception {
        //
        Pageable pageable = createPageable(
                searchRequest.getPage(),
                searchRequest.getSize(),
                searchRequest.getSortBy(),
                searchRequest.getSortDirection()
        );
        Page<BookLoan> bookLoanPage;
        //
        if(Boolean.TRUE.equals(searchRequest.getOverdueOnly())){
            //
            bookLoanPage = bookLoanRepository.findOverdueBookLoans(LocalDate.now(), pageable);
        }
        else if(searchRequest.getUserId() != null){
            bookLoanPage = bookLoanRepository.findByUserId(searchRequest.getUserId(), pageable);
        }
        else if(searchRequest.getBookId() != null){
            bookLoanPage = bookLoanRepository.findByBookId(searchRequest.getBookId(), pageable);
        }
        else if(searchRequest.getStatus() !=null){
            //
            bookLoanPage = bookLoanRepository.findByStatus(searchRequest.getStatus(), pageable);
        }
        else if(searchRequest.getStartDate() != null && searchRequest.getEndDate() != null){
            //
            bookLoanPage = bookLoanRepository.findBooksLoansByDateRange(
                    searchRequest.getStartDate(),
                    searchRequest.getEndDate(),
                    pageable
                    );
        }else{
            // default: return all loans
            bookLoanPage = bookLoanRepository.findAll(pageable);
        }

        return convertToPageResponse(bookLoanPage);
    }

    @Override
    public int updateOverdueBookLoan() throws Exception {
        Pageable pageable = PageRequest.of(0, 1000);
        Page<BookLoan> overduePage = bookLoanRepository
                .findOverdueBookLoans(LocalDate.now(), pageable);

        int updateCount = 0;
        for(BookLoan bookLoan : overduePage.getContent()){
            if(bookLoan.getStatus() == BookLoanStatus.CHECKED_OUT){
                bookLoan.setStatus(BookLoanStatus.OVERDUE);
                bookLoan.setIsOverdue(true);

                // calculate overdue days
                int overdueDays =calculateOverdueDays(
                        bookLoan.getDueDate(),
                        LocalDate.now()
                );

                bookLoan.setOverdueDays(overdueDays);
                // calculate fine
               // BigDecimal fine = fineCalculationService.calculateOverdueFine(bookLoan);
                bookLoanRepository.save(bookLoan);
                updateCount++;

            }
        }
        return updateCount;
    }


    private Pageable createPageable(int page,
                                    int size,
                                    String sortBy,
                                    String sortDirection) {
        size = Math.min(size, 100);
        size = Math.max(size, 1);

        Sort sort = sortDirection.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        return PageRequest.of(page, size, sort);
    }


    private PageResponse<BookLoanDTO> convertToPageResponse(Page<BookLoan> bookLoanPage) {
        List<BookLoanDTO> bookLoanDTOS = bookLoanPage.getContent()
                .stream()
                .map(bookLoanMapper::toDTO)
                .collect(Collectors.toList());

        return new PageResponse<>(
                bookLoanDTOS,
                bookLoanPage.getNumber(),
                bookLoanPage.getSize(),
                bookLoanPage.getTotalElements(),
                bookLoanPage.getTotalPages(),
                bookLoanPage.isLast(),
                bookLoanPage.isFirst(),
                bookLoanPage.isEmpty()
        );
    }

    public int calculateOverdueDays(LocalDate dueDate, LocalDate today){
        if(today.isBefore(dueDate) || today.isEqual(dueDate)){
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(dueDate, today);
    }
}
