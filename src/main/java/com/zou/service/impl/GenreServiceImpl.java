package com.zou.service.impl;

import com.zou.exception.GenreException;
import com.zou.mapper.GenreMapper;
import com.zou.modal.Genre;
import com.zou.payload.dto.GenreDTO;
import com.zou.repository.GenreRepository;
import com.zou.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;
    private final com.zou.repository.BookRepository books;

    @Override
    public GenreDTO createGenre(GenreDTO genreDTO) {


        Genre genre = genreMapper.toEntity(genreDTO);
        Genre savedGenre = genreRepository.save(genre);


        return genreMapper.toGenreDTO(savedGenre);
    }

    @Override
    public List<GenreDTO> getAllGenres() {
        return genreRepository.findAll().stream()
                .map(genreMapper::toGenreDTO)
                .collect(Collectors.toList());
    }

    @Override
    public GenreDTO getGenreById(Long genreId) throws GenreException {
        Genre genre = genreRepository.findById(genreId).orElseThrow(
                ()-> new GenreException("Genre not found")
        );
        return genreMapper.toGenreDTO(genre);
    }

    @Override
    public GenreDTO updateGenre(Long genreId, GenreDTO genreDTO) throws GenreException {
        Genre exitstingGenre = genreRepository.findById(genreId).orElseThrow(
                ()-> new GenreException("Genre not found")
        );

        genreMapper.updateEntityFromDTO(genreDTO, exitstingGenre);

        Genre updatedGenre = genreRepository.save(exitstingGenre);
        return genreMapper.toGenreDTO(updatedGenre);
    }

    @Override
    public void deleteGenre(Long genreId) throws GenreException {
        Genre exitstingGenre = genreRepository.findById(genreId).orElseThrow(
                ()-> new GenreException("Genre not found")
        );

        exitstingGenre.setActive(false);
        genreRepository.save(exitstingGenre);

    }

    @Override
    public void hardDeleteGenre(Long genreId) throws GenreException {
        Genre exitstingGenre = genreRepository.findById(genreId).orElseThrow(
                ()-> new GenreException("Genre not found")
        );

        genreRepository.delete(exitstingGenre);
    }

    @Override
    public List<GenreDTO> getAllActiveGenresWithSubGenre() {
        List<Genre> topLevelGenres= genreRepository
                .findByParentGenreIsNullAndActiveTrueOrderByDisplayOrderAsc();
        return genreMapper.toDTOList(topLevelGenres);
    }

    @Override
    public List<GenreDTO> getTopLevelGenre() {
        List<Genre> topLevelGenres= genreRepository
                .findByParentGenreIsNullAndActiveTrueOrderByDisplayOrderAsc();
        return genreMapper.toDTOList(topLevelGenres);
    }

    @Override
    public long getTotalActiveGenres() {
        return genreRepository.countByActiveTrue();
    }

    @Override
    public long getBookCountByGenre(Long genreId) {
        return books.countByGenreIdAndActiveTrue(genreId);
    }

    @Override
    public List<GenreDTO> getPopularGenres(int limit) {
        return genreRepository.findPopular(org.springframework.data.domain.PageRequest.of(0, Math.max(1, Math.min(limit, 12))))
                .stream().map(genre -> {
                    GenreDTO dto = genreMapper.toGenreDTO(genre);
                    dto.setBookCount(books.countByGenreIdAndActiveTrue(genre.getId()));
                    return dto;
                }).toList();
    }
}
