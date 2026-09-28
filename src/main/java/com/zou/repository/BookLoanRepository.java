package com.zou.repository;

import com.zou.domain.BookLoanStatus;
import com.zou.modal.Book;
import com.zou.modal.BookLoan;
import com.zou.modal.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookLoanRepository extends JpaRepository<BookLoan,Long> {

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from BookLoan b where b.id = :id")
    java.util.Optional<BookLoan> findLockedById(@Param("id") Long id);

    Page<BookLoan> findByUserId(Long userId, Pageable pageable);
    Page<BookLoan> findByStatusAndUser(BookLoanStatus status, User user, Pageable pageable);
    Page<BookLoan> findByStatus(BookLoanStatus status, Pageable pageable);
    Page<BookLoan> findByBookId(Long bookId, Pageable pageable);

    List<BookLoan> findByBookId(Long bookId);

    @Query("select case when count(bl)>0 then true else false end from BookLoan bl"+
    " where bl.user.id =:userId and bl.book.id =:bookId "+
    " and (bl.status = 'CHECKED_OUT' OR bl.status='OVERDUE')")
    boolean hasActiveCheckout(
            @Param("userId") Long userId,
            @Param("bookId") Long bookId
    );

    @Query("SELECT COUNT(bl) FROM BookLoan bl WHERE bl.user.id = :userId "+
    "AND (bl.status = 'CHECKED_OUT' OR bl.status='OVERDUE')")
    long countActiveBookLoansByUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(bl) FROM BookLoan bl WHERE bl.user.id = :userId "+
            "AND bl.status='OVERDUE' ")
    long countOverdueBookLoansByUser(@Param("userId") Long userId);


    @Query("SELECT bl FROM BookLoan bl WHERE bl.dueDate < :currentDate "+
            "AND (bl.status= 'CHECKED_OUT' OR bl.status = 'OVERDUE') ")
    Page<BookLoan> findOverdueBookLoans(@Param("currentDate")LocalDate currentDate,
                                        Pageable pageable);

    @Query("SELECT bl FROM BookLoan bl WHERE bl.checkoutDate BETWEEN :startDate AND :endDate")
    Page<BookLoan> findBooksLoansByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    //boolean exitsByUserIdAndBookIdAndStatus(Long userId, Long bookId, BookLoanStatus status);

    boolean existsByUserIdAndBookIdAndStatus(
            Long userId,
            Long bookId,
            BookLoanStatus status
    );
    long countByStatus(BookLoanStatus status);

    @Query("select bl.book from BookLoan bl where bl.book.active=true group by bl.book order by count(bl) desc")
    Page<Book> findPopularBooks(Pageable pageable);

    @Query("select bl.checkoutDate, count(bl) from BookLoan bl where bl.checkoutDate >= :start group by bl.checkoutDate order by bl.checkoutDate")
    List<Object[]> countCheckoutsByDaySince(@Param("start") LocalDate start);

    @Query("select bl.returnDate, count(bl) from BookLoan bl where bl.returnDate is not null and bl.returnDate >= :start group by bl.returnDate order by bl.returnDate")
    List<Object[]> countReturnsByDaySince(@Param("start") LocalDate start);

    @Query("select bl.book.title, count(bl) from BookLoan bl group by bl.book.id, bl.book.title order by count(bl) desc")
    List<Object[]> popularBookStatistics(Pageable pageable);

    @Query("select bl.book.genre.name, count(bl) from BookLoan bl group by bl.book.genre.id, bl.book.genre.name order by count(bl) desc")
    List<Object[]> popularGenreStatistics(Pageable pageable);
}
