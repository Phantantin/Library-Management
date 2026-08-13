package com.zou.service.impl;

import com.zou.exception.BookException;
import com.zou.mapper.BookMapper;
import com.zou.modal.Book;
import com.zou.payload.dto.BookDTO;
import com.zou.payload.request.BookSearchRequest;
import com.zou.payload.response.PageResponse;
import com.zou.repository.BookRepository;
import com.zou.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public BookDTO createBook(BookDTO bookDTO) throws BookException {
        if(bookRepository.existsByIsbn(bookDTO.getIsbn())) {
            throw new BookException("Book with isbn "+ bookDTO.getIsbn()+ "already exits");

        }
        Book book = bookMapper.toEntity(bookDTO);
        // total - 10
        // available -11
        book.isVailableCopesValid();
        Book savedBook = bookRepository.save(book);

        return bookMapper.toDTO(savedBook);
    }

    @Override
    public List<BookDTO> createBooksBulk(List<BookDTO> bookDTOs) throws BookException {

        List<BookDTO> createdBooks = new ArrayList<>();
        for(BookDTO bookDTO: bookDTOs) {
            BookDTO book =  createBook(bookDTO);
            createdBooks.add(book);
        }
        return createdBooks;
    }

    @Override
    public BookDTO getBookBy(Long bookId) throws BookException {
        Book book =  bookRepository.findById(bookId).orElseThrow(
                ()-> new BookException("Book not found!")
        );
        return bookMapper.toDTO(book);
    }

    @Override
    public BookDTO getBookByISBN(String isbn) throws BookException {
        Book book =  bookRepository.findByIsbn(isbn).orElseThrow(
                ()-> new BookException("Book not found!")
        );
        return bookMapper.toDTO(book);
    }

    @Override
    public BookDTO updateBook(Long bookId, BookDTO bookDTO) throws BookException {
        Book existingBook = bookRepository.findById(bookId).orElseThrow(
                ()-> new BookException("Book not found!")
        );
        bookMapper.updateEntityFromDto(bookDTO, existingBook);
        existingBook.isVailableCopesValid();
        Book savedBook = bookRepository.save(existingBook);
        return bookMapper.toDTO(savedBook);
    }

    @Override
    public void deleteBook(Long bookId) throws BookException {
        Book existingBook = bookRepository.findById(bookId).orElseThrow(
                ()-> new BookException("Book not found!")
        );
        existingBook.setActive(false);
        bookRepository.save(existingBook);

    }

    @Override
    public void hardDeleteBook(Long bookId) throws BookException {
        Book existingBook = bookRepository.findById(bookId).orElseThrow(
                ()-> new BookException("Book not found!")
        );
        bookRepository.delete(existingBook);

    }

    @Override
    public PageResponse<BookDTO> searchBooksWithFilters(BookSearchRequest searchRequest) {

        Pageable pageable = createPageable(searchRequest.getPage(),
                searchRequest.getPageSize(),
                searchRequest.getSortBy(),
                searchRequest.getSortDirection());
        Page<Book> bookPage = bookRepository.seachBookWithFilters(
                searchRequest.getSearchTerm(),
                searchRequest.getGenreId(),
                searchRequest.getAvailableOnly(),
                pageable
        );

        return converToPageResponse(bookPage);
    }

    @Override
    public long getTotalActiveBooks() {
        return bookRepository.countByActiveTrue();
    }

    @Override
    public long getTotalAvailableBooks() {

        return bookRepository.countAvailableBooks();
    }

    private Pageable createPageable(int page, int size, String sortBy, String sortDirection) {
        size = Math.min(size, 10);
        size = Math.max(size, 1);

        Sort sort = sortDirection.equalsIgnoreCase("ASC")
                ?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        return PageRequest.of(page, size, sort);
    }

    private PageResponse<BookDTO> converToPageResponse(Page<Book> books) {
        List<BookDTO> bookDTOs = books.getContent()
                .stream()
                .map(bookMapper::toDTO)
                .collect(Collectors.toList());
        return new PageResponse<>(bookDTOs,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isLast(),
                books.isFirst(),
                books.isEmpty()
        );
    }
}
