package com.zou.service;

import com.zou.payload.dto.WishlistDTO;
import com.zou.payload.response.PageResponse;

public interface WishlistService {

    WishlistDTO addToWishList(Long bookId, String notes) throws Exception;
    void removeFromWishList(Long bookId) throws Exception;
    PageResponse<WishlistDTO> getMyWishlists(int page, int size) throws Exception;
}
