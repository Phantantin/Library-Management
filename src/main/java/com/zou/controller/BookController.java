package com.zou.controller;


import com.zou.exception.BookException;
import com.zou.payload.dto.BookDTO;
import com.zou.payload.request.BookSearchRequest;
import com.zou.payload.response.ApiResponse;
import com.zou.payload.response.PageResponse;
import com.zou.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    @PostMapping()
    public ResponseEntity<BookDTO> createBook(
            @Valid @RequestBody BookDTO bookDTO) throws BookException {
        BookDTO createdBook = bookService.createBook(bookDTO);
        return ResponseEntity.ok(createdBook);
    }


    @PostMapping("/bulk")
    public ResponseEntity<?> createBooksBulk(
            @Valid @RequestBody List<BookDTO> bookDTOs) throws BookException {
        List<BookDTO> createdBooks = bookService.createBooksBulk(bookDTOs);
        return ResponseEntity.ok(createdBooks);
    }
    /*
    get a book by id
    Get /api/books/{id}
     */


    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id)
            throws BookException {
        BookDTO book =  bookService.getBookBy(id);
        return ResponseEntity.ok(book);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(
            @PathVariable Long id,
            @RequestBody BookDTO bookDTO) throws BookException {
            BookDTO updateBook = bookService.updateBook(id, bookDTO);
            return ResponseEntity.ok(updateBook);
    }



    /*
    Soft delete a book (mark as inactive)
    Delete /api/books/{id}
     */

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteBook(
            @PathVariable Long id) throws BookException {
        bookService.deleteBook(id);
        return ResponseEntity.ok(new ApiResponse("Book deleted successfully", true));
    }

    /*
    Permanently delete a book
    Delete /api/books/{id}/permanent
     */

    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<ApiResponse> deletePermanentBook(
            @PathVariable Long id) throws BookException {
        bookService.hardDeleteBook(id);
        return ResponseEntity.ok(new ApiResponse("Book permanent successfully", true));
    }

    @GetMapping
    public ResponseEntity<PageResponse<BookDTO>> getBooks(
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false, defaultValue = "false") Boolean availableOnly,
            @RequestParam(defaultValue = "true") boolean activeOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection){

        // Build search request from query parameters
        BookSearchRequest bookSearchRequest = new BookSearchRequest();
        bookSearchRequest.setGenreId(genreId);
        bookSearchRequest.setAvailableOnly(availableOnly);
        bookSearchRequest.setPage(page);
        bookSearchRequest.setPageSize(size);
        bookSearchRequest.setSortBy(sortBy);
        bookSearchRequest.setSortDirection(sortDirection);

        PageResponse<BookDTO> books = bookService.searchBooksWithFilters(bookSearchRequest);
        return ResponseEntity.ok(books);
    }

    @PostMapping("/search")
    public ResponseEntity<PageResponse<BookDTO>> advancedSearch(
            @RequestBody BookSearchRequest bookSearchRequest){
        PageResponse<BookDTO> books = bookService.searchBooksWithFilters(bookSearchRequest);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/stats")
    public ResponseEntity<BookStatsResponse> getBookStats() {
        long totalActive = bookService.getTotalActiveBooks();
        long totalAvailable = bookService.getTotalAvailableBooks();

        BookStatsResponse stats = new BookStatsResponse(totalActive, totalAvailable);
        return ResponseEntity.ok(stats);
    }

    /*
    Statistics responseDTO
     */

    public static class BookStatsResponse {
        public long totalActiveBooks;
        public long totalAvailableBooks;

        public BookStatsResponse(long totalActiveBooks, long totalAvailableBooks) {
            this.totalActiveBooks = totalActiveBooks;
            this.totalAvailableBooks = totalAvailableBooks;
        }
    }

}
