package com.zou.service;

import com.zou.domain.ReservationStatus;
import com.zou.modal.Book;
import com.zou.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationQueueService {
    private final ReservationRepository reservations;

    public void resequence(Long bookId) {
        var pending = reservations.findByBookIdAndStatusOrderByReservedAtAsc(bookId, ReservationStatus.PENDING);
        for (int i = 0; i < pending.size(); i++) pending.get(i).setQueuePosition(i + 1);
        reservations.saveAll(pending);
    }

    public void promoteNext(Book book) {
        if (book.getAvailableCopies() == null || book.getAvailableCopies() <= 0) return;
        var pending = reservations.findByBookIdAndStatusOrderByReservedAtAsc(book.getId(), ReservationStatus.PENDING);
        if (pending.isEmpty()) return;
        var next = pending.get(0);
        next.setStatus(ReservationStatus.AVAILABLE);
        next.setQueuePosition(0);
        next.setAvailableAt(LocalDateTime.now());
        next.setAvailableUntil(LocalDateTime.now().plusDays(2));
        next.setNotificationSent(false);
        reservations.save(next);
        resequence(book.getId());
    }
}
