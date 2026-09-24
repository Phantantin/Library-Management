package com.zou.controller;

import com.zou.exception.BookException;
import com.zou.payload.dto.BookDTO;
import com.zou.payload.request.BookSearchRequest;
import com.zou.payload.response.PageResponse;
import com.zou.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/books")
public class AdminBookController {

    private final BookService bookService;

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBook(@PathVariable Long id) throws BookException {
        return ResponseEntity.ok(bookService.getBookBy(id));
    }

    @PostMapping("/search")
    public ResponseEntity<PageResponse<BookDTO>> search(@RequestBody BookSearchRequest request) {
        request.setActiveOnly(false);
        return ResponseEntity.ok(bookService.searchBooksWithFilters(request));
    }

    @PostMapping()
    public ResponseEntity<BookDTO> createBook(
            @Valid @RequestBody BookDTO bookDTO) throws BookException {
        BookDTO createdBook = bookService.createBook(bookDTO);
        return ResponseEntity.ok(createdBook);
    }
}
