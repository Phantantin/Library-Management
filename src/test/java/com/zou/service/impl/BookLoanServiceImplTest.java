package com.zou.service.impl;

import com.zou.exception.BookException;
import com.zou.mapper.BookLoanMapper;
import com.zou.modal.Book;
import com.zou.modal.BookLoan;
import com.zou.modal.User;
import com.zou.payload.dto.BookLoanDTO;
import com.zou.payload.request.CheckoutRequest;
import com.zou.repository.BookLoanRepository;
import com.zou.repository.BookRepository;
import com.zou.service.AccessService;
import com.zou.service.ReservationQueueService;
import com.zou.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class BookLoanServiceImplTest {
    private static final long USER_ID = 7L;
    private static final long BOOK_ID = 12L;

    private BookLoanRepository loans;
    private UserService users;
    private BookRepository books;
    private BookLoanMapper mapper;
    private BookLoanServiceImpl service;
    private User user;
    private Book book;

    @BeforeEach
    void setUp() throws Exception {
        loans = mock(BookLoanRepository.class);
        users = mock(UserService.class);
        books = mock(BookRepository.class);
        mapper = mock(BookLoanMapper.class);
        service = new BookLoanServiceImpl(
                loans,
                users,
                books,
                mapper,
                mock(AccessService.class),
                mock(ReservationQueueService.class)
        );
        ReflectionTestUtils.setField(service, "maxActiveLoans", 5);
        ReflectionTestUtils.setField(service, "maxCheckoutDays", 30);
        ReflectionTestUtils.setField(service, "maxRenewalDays", 14);

        user = User.builder()
                .id(USER_ID)
                .email("reader@example.test")
                .fullName("Library Reader")
                .phone("0123456789")
                .build();
        book = Book.builder().id(BOOK_ID).active(true).availableCopies(2).build();
        when(users.findById(USER_ID)).thenReturn(user);
    }

    @Test
    void checkoutWorksWithoutAnActiveSubscriptionWhenProfileIsComplete() throws Exception {
        when(books.findLockedById(BOOK_ID)).thenReturn(Optional.of(book));
        when(loans.hasActiveCheckout(USER_ID, BOOK_ID)).thenReturn(false);
        when(loans.countActiveBookLoansByUser(USER_ID)).thenReturn(0L);
        when(loans.countOverdueBookLoansByUser(USER_ID)).thenReturn(0L);
        when(loans.save(any(BookLoan.class))).thenAnswer(invocation -> invocation.getArgument(0));
        var expected = new BookLoanDTO();
        when(mapper.toDTO(any(BookLoan.class))).thenReturn(expected);

        var result = service.checkoutBookForUser(USER_ID, new CheckoutRequest(BOOK_ID, 14, null));

        assertSame(expected, result);
        assertEquals(1, book.getAvailableCopies());
        var captor = org.mockito.ArgumentCaptor.forClass(BookLoan.class);
        verify(loans).save(captor.capture());
        assertEquals(LocalDate.now().plusDays(14), captor.getValue().getDueDate());
    }

    @Test
    void checkoutRequiresCompleteContactDetails() throws Exception {
        user.setPhone(" ");

        var exception = assertThrows(BookException.class,
                () -> service.checkoutBookForUser(USER_ID, new CheckoutRequest(BOOK_ID, 14, null)));

        assertEquals("Complete your profile before borrowing by adding your full name and phone number.", exception.getMessage());
        verifyNoInteractions(books, loans);
    }
}
