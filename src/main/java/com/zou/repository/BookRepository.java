package com.zou.repository;

import com.zou.modal.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Book b where b.id = :id")
    Optional<Book> findLockedById(@Param("id") Long id);
    long countByGenreIdAndActiveTrue(Long genreId);

    Page<Book> findByActiveTrueAndFeaturedTrue(Pageable pageable);

    Optional<Book> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    @Query("SELECT b FROM Book b WHERE " +
            "(:searchTerm IS NULL OR " +
            "LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(b.author) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(b.isbn) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "AND (:genreId IS NULL OR b.genre.id = :genreId) " +
            "AND (:availableOnly = false OR b.availableCopies > 0) " +
            "AND (:activeOnly = false OR b.active = true)"
    )
    Page<Book> seachBookWithFilters(
            @Param("searchTerm") String searchTerm,
            @Param("genreId") Long genreId,
            @Param("availableOnly") boolean availableOnly,
            @Param("activeOnly") boolean activeOnly,
            Pageable pageable
    );


    long countByActiveTrue();

    @Query("SELECT COUNT(b) FROM Book b WHERE b.availableCopies > 0 AND b.active = true")
    long countAvailableBooks();
}
