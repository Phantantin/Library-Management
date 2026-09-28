package com.zou.mapper;


import com.zou.exception.BookException;
import com.zou.modal.Book;
import com.zou.modal.Genre;
import com.zou.payload.dto.BookDTO;
import com.zou.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookMapper {

    private final GenreRepository genreRepository;

    public BookDTO toDTO(Book book) {
        if(book == null) {
            return null;
        }

        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .price(book.getPrice())
                .isbn(book.getIsbn())
                .genreId(book.getGenre().getId())
                .coverImageUrl(book.getCoverImageUrl())
                .genreName(book.getGenre().getName())
                .genreCode(book.getGenre().getCode())
                .publisher(book.getPublisher())
                .publicationDate(book.getPublicsheDate())
                .language(book.getLanguage())
                .pages(book.getPages())
                .description(book.getDescription())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .active(book.getActive())
                .featured(book.getFeatured())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }

    public Book toEntity(BookDTO dto) throws BookException {
        if(dto == null) {
            return null;
        }

        Book book = new Book();

        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());

        // Map genre
        if(dto.getGenreId() != null) {
            Genre genre = genreRepository.findById(dto.getGenreId())
                    .orElseThrow(() -> new BookException("Genre with id " + dto.getGenreId() + " not found"));
            book.setGenre(genre);
        }

        book.setPublisher(dto.getPublisher());
        book.setPublicsheDate(dto.getPublicationDate()); // Kiểm tra lại chính tả 'PublicsheDate' trong Entity nhé
        book.setLanguage(dto.getLanguage());
        book.setPages(dto.getPages());
        book.setDescription(dto.getDescription());
        book.setTotalCopies(dto.getTotalCopies());
        book.setAvailableCopies(dto.getAvailableCopies());

        // BỔ SUNG TRƯỜNG PRICE
        book.setPrice(dto.getPrice());

        book.setCoverImageUrl(dto.getCoverImageUrl());

        // Xử lý Active: Nếu dto có giá trị thì dùng, không thì mặc định true
        book.setActive(dto.getActive() != null ? dto.getActive() : true);
        book.setFeatured(dto.getFeatured() != null ? dto.getFeatured() : false);




        return book;
    }


    public void updateEntityFromDto(BookDTO dto, Book book) throws BookException {
        if(dto == null||book == null) {
            return;
        }
        book.setPrice(dto.getPrice());
        // ISBN should not be update
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());

        //UPDATE genre if provided

        if(dto.getGenreId() != null) {
            Genre genre = genreRepository.findById(dto.getGenreId())
                    .orElseThrow(() -> new BookException("Genre with id " + dto.getGenreId() + " not found"));
            book.setGenre(genre);
        }

        book.setPublisher(dto.getPublisher());
        book.setPublicsheDate(dto.getPublicationDate());
        book.setLanguage(dto.getLanguage());
        book.setPages(dto.getPages());
        book.setDescription(dto.getDescription());
        book.setTotalCopies(dto.getTotalCopies());
        book.setAvailableCopies(dto.getAvailableCopies());
        book.setActive(dto.getActive() == null ? book.getActive() : dto.getActive());


        book.setCoverImageUrl(dto.getCoverImageUrl());

        if(dto.getActive()!=null) {
            book.setActive(dto.getActive() == null ? book.getActive() : dto.getActive());
        }
        if(dto.getFeatured()!=null) book.setFeatured(dto.getFeatured());
    }



}
