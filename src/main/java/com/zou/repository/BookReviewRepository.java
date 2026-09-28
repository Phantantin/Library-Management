package com.zou.repository;

import com.zou.modal.Book;
import com.zou.modal.BookReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookReviewRepository extends JpaRepository<BookReview, Long> {

    Page<BookReview> findByBook(Book book, Pageable pageable);

    boolean existsByUserIdAndBookId(Long userId, Long bookId);
org.springframework.data.domain.Page<BookReview> findByUserId(Long id,org.springframework.data.domain.Pageable page);
}
