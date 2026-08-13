package com.zou.service;

import com.zou.payload.dto.BookReviewDTO;
import com.zou.payload.request.CreateReviewRequest;
import com.zou.payload.request.UpdateReviewRequest;
import com.zou.payload.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface BookReviewService {

    BookReviewDTO createReview(CreateReviewRequest request) throws Exception;

    BookReviewDTO updateReview(Long reviewId, UpdateReviewRequest request) throws Exception;

    void deleteReview(Long reviewId) throws Exception;

    PageResponse<BookReviewDTO> getReviewsByBookId(Long bookId, int page, int size) throws Exception;
}
