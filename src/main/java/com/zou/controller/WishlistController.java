package com.zou.controller;

import com.zou.payload.dto.WishlistDTO;
import com.zou.payload.response.ApiResponse;
import com.zou.payload.response.PageResponse;
import com.zou.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping("/add/{bookId}")
    public ResponseEntity<?> addToWishlist(
            @PathVariable Long bookId,
            @RequestParam(required = false) String notes
    ) throws Exception {

        WishlistDTO wishlistDTO =
                wishlistService.addToWishList(bookId, notes);

        return ResponseEntity.ok(wishlistDTO);
    }

    @DeleteMapping("/remove/{bookId}")
    public ResponseEntity<ApiResponse> removeFromWishlist(
            @PathVariable Long bookId
    ) throws Exception {

        wishlistService.removeFromWishList(bookId);

        return ResponseEntity.ok(
                new ApiResponse(
                        "Book removed from wishlist successfully",
                        true
                )
        );
    }

    @GetMapping("/my-wishlist")
    public ResponseEntity<?> getMyWishlist(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws Exception {

        PageResponse<WishlistDTO> wishlist =
                wishlistService.getMyWishlists(page, size);

        return ResponseEntity.ok(wishlist);
    }
}
