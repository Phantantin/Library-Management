//package com.zou.repository;
//
//import com.zou.domain.ReservationStatus;
//import com.zou.modal.Reservation;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//
//
//public interface ReservationRepository extends JpaRepository<Reservation, Long> {
//
//    /**
//     * Check if user already has an active reservation for a book
//     */
//    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
//            "FROM Reservation r " +
//            "WHERE r.user.id = :userId AND r.book.id = :bookId " +
//            "AND (r.status = 'PENDING' OR r.status = 'AVAILABLE')")
//    boolean hasActiveReservation(
//            @Param("userId") Long userId,
//            @Param("bookId") Long bookId
//    );
//
//
//    /**
//     * Count active reservations for a user
//     */
//    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.user.id = :userId " +
//            "AND (r.status = 'PENDING' OR r.status = 'AVAILABLE')")
//    Long countActiveReservationsByUser(
//            @Param("userId") Long userId
//    );
//
//
//    /**
//     * Count pending reservations for a book
//     */
//    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.book.id = :bookId " +
//            "AND r.status = 'PENDING'")
//    Long countPendingReservationsByBook(
//            @Param("bookId") Long bookId
//    );
//
//
//
//
//
//    /**
//     * Search reservations with dynamic filters
//     */
//    @Query("SELECT r FROM Reservation r WHERE " +
//            "(:userId IS NULL OR r.user.id = :userId) AND " +
//            "(:bookId IS NULL OR r.book.id = :bookId) AND " +
//            "(:status IS NULL OR r.status = :status) AND " +
//            "(:activeOnly = false OR " +
//            "(r.status = 'PENDING' OR r.status = 'AVAILABLE'))")
//    Page<Reservation> searchReservationsWithFilters(
//            @Param("userId") Long userId,
//            @Param("bookId") Long bookId,
//            @Param("status") ReservationStatus status,
//            @Param("activeOnly") boolean activeOnly,
//            Pageable pageable
//    );
//}






package com.zou.repository;

import com.zou.domain.ReservationStatus;
import com.zou.modal.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository
        extends JpaRepository<Reservation, Long> {

    List<Reservation> findByBookIdAndStatusOrderByReservedAtAsc(Long bookId, ReservationStatus status);

    /**
     * Check if user already has an active reservation for a book
     */
    @Query("""
            SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
            FROM Reservation r
            WHERE r.user.id = :userId
              AND r.book.id = :bookId
              AND r.status IN :statuses
            """)
    boolean hasActiveReservationInternal(
            @Param("userId") Long userId,
            @Param("bookId") Long bookId,
            @Param("statuses") List<ReservationStatus> statuses
    );

    default boolean hasActiveReservation(
            Long userId,
            Long bookId
    ) {
        return hasActiveReservationInternal(
                userId,
                bookId,
                List.of(
                        ReservationStatus.PENDING,
                        ReservationStatus.AVAILABLE
                )
        );
    }


    /**
     * Count active reservations for a user
     */
    @Query("""
            SELECT COUNT(r)
            FROM Reservation r
            WHERE r.user.id = :userId
              AND r.status IN :statuses
            """)
    Long countActiveReservationsByUserInternal(
            @Param("userId") Long userId,
            @Param("statuses") List<ReservationStatus> statuses
    );

    default Long countActiveReservationsByUser(
            Long userId
    ) {
        return countActiveReservationsByUserInternal(
                userId,
                List.of(
                        ReservationStatus.PENDING,
                        ReservationStatus.AVAILABLE
                )
        );
    }


    /**
     * Count pending reservations for a book
     */
    @Query("""
            SELECT COUNT(r)
            FROM Reservation r
            WHERE r.book.id = :bookId
              AND r.status = :status
            """)
    Long countReservationsByBookAndStatus(
            @Param("bookId") Long bookId,
            @Param("status") ReservationStatus status
    );

    default Long countPendingReservationsByBook(
            Long bookId
    ) {
        return countReservationsByBookAndStatus(
                bookId,
                ReservationStatus.PENDING
        );
    }


    /**
     * Search reservations with dynamic filters
     */
    @Query("""
            SELECT r
            FROM Reservation r
            WHERE (:userId IS NULL OR r.user.id = :userId)
              AND (:bookId IS NULL OR r.book.id = :bookId)
              AND (:status IS NULL OR r.status = :status)
              AND (
                    :activeOnly = false
                    OR r.status IN :activeStatuses
                  )
            """)
    Page<Reservation> searchReservationsWithFiltersInternal(
            @Param("userId") Long userId,
            @Param("bookId") Long bookId,
            @Param("status") ReservationStatus status,
            @Param("activeOnly") boolean activeOnly,
            @Param("activeStatuses") List<ReservationStatus> activeStatuses,
            Pageable pageable
    );

    default Page<Reservation> searchReservationsWithFilters(
            Long userId,
            Long bookId,
            ReservationStatus status,
            boolean activeOnly,
            Pageable pageable
    ) {
        return searchReservationsWithFiltersInternal(
                userId,
                bookId,
                status,
                activeOnly,
                List.of(
                        ReservationStatus.PENDING,
                        ReservationStatus.AVAILABLE
                ),
                pageable
        );
    }
}
