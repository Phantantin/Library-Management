package com.zou.mapper;

import com.zou.modal.Book;
import com.zou.modal.Genre;
import com.zou.repository.GenreRepository;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookMapperTest {
    private final GenreRepository genres = mock(GenreRepository.class);
    private final BookMapper mapper = new BookMapper(genres);

    @Test
    void dtoUsesGenreIdAndIncludesCover() {
        Genre genre = Genre.builder().id(7L).name("History").code("HIS").build();
        Book book = Book.builder().id(42L).isbn("isbn").title("Title").author("Author").genre(genre)
                .totalCopies(3).availableCopies(2).active(true).coverImageUrl("https://example.test/cover.jpg").build();
        var dto = mapper.toDTO(book);
        assertEquals(7L, dto.getGenreId());
        assertEquals("https://example.test/cover.jpg", dto.getCoverImageUrl());
    }

    @Test
    void entityCannotImportClientControlledId() throws Exception {
        var dto = com.zou.payload.dto.BookDTO.builder().id(99L).isbn("isbn").title("Title").author("Author")
                .genreId(7L).totalCopies(1).availableCopies(1).build();
        when(genres.findById(7L)).thenReturn(Optional.of(Genre.builder().id(7L).build()));
        assertNull(mapper.toEntity(dto).getId());
    }
}
