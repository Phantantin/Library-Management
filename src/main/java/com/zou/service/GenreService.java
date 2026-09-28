package com.zou.service;

import com.zou.exception.GenreException;
import com.zou.payload.dto.GenreDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GenreService {

    GenreDTO createGenre(GenreDTO genreDTO);

    List<GenreDTO> getAllGenres();

    GenreDTO getGenreById(Long genreId) throws GenreException;

    GenreDTO updateGenre(Long genreId, GenreDTO genreDTO) throws GenreException;

    void deleteGenre(Long genreId) throws GenreException;

    void hardDeleteGenre(Long genreId) throws GenreException;

    List<GenreDTO> getAllActiveGenresWithSubGenre();

    List<GenreDTO> getTopLevelGenre();

  //  Page<GenreDTO> searchGenres(String searchTerm, Pageable pageable);

    long getTotalActiveGenres();

    long getBookCountByGenre(Long genreId);

    List<GenreDTO> getPopularGenres(int limit);

}
