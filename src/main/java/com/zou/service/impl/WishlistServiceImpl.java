package com.zou.service.impl;

import com.zou.mapper.WishlistMapper;
import com.zou.modal.Book;
import com.zou.modal.User;
import com.zou.modal.Wishlist;
import com.zou.payload.dto.WishlistDTO;
import com.zou.payload.response.PageResponse;
import com.zou.repository.BookRepository;
import com.zou.repository.WishlistRepository;
import com.zou.service.UserService;
import com.zou.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserService userService;
    private final BookRepository bookRepository;
    private final WishlistMapper wishlistMapper;

    @Override
    public WishlistDTO addToWishList(Long bookId, String notes) throws Exception {
        User user = userService.getCurrentUser();
        // 1 validate book exits
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new Exception("Book not found"));

        // 2 check if book is already in wishlist
        if(wishlistRepository.existsByUserIdAndBookId(user.getId(), book.getId())){
            throw new Exception("Book is already your wishlist");
        }
        // create wishlist
        Wishlist  wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setBook(book);
        wishlist.setNotes(notes);
        Wishlist saved = wishlistRepository.save(wishlist);

        return wishlistMapper.toDTO(saved);
    }

    @Override
    public void removeFromWishList(Long bookId) throws Exception {
        User user = userService.getCurrentUser();

        Wishlist wishlist = wishlistRepository.findByUserIdAndBookId(
                user.getId(), bookId
        );
        if(wishlist == null){
            throw new Exception("Book is not in your wishlist");
        }
        wishlistRepository.delete(wishlist);
    }

    @Override
    public PageResponse<WishlistDTO> getMyWishlists(int page, int size) throws Exception {
        Long userId = userService.getCurrentUser().getId();
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("addedAt").descending());
        Page<Wishlist> wishlistPage = wishlistRepository.findByUserId(userId,  pageable);
        return convertToPageResponse(wishlistPage);
    }

    private PageResponse<WishlistDTO> convertToPageResponse(
            Page<Wishlist> wishlistPage
    ) {

        List<WishlistDTO> wishlistDTOs = wishlistPage
                .getContent()
                .stream()
                .map(wishlistMapper::toDTO)
                .collect(Collectors.toList());

        return new PageResponse<>(
                wishlistDTOs,
                wishlistPage.getNumber(),
                wishlistPage.getSize(),
                wishlistPage.getTotalElements(),
                wishlistPage.getTotalPages(),
                wishlistPage.isLast(),
                wishlistPage.isFirst(),
                wishlistPage.isEmpty()
        );
    }
}
