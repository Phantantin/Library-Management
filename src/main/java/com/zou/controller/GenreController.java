package com.zou.controller;

import com.zou.exception.GenreException;
import com.zou.modal.Genre;
import com.zou.payload.dto.GenreDTO;
import com.zou.payload.response.ApiResponse;
import com.zou.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/genres")
public class GenreController {

    private final GenreService genreService;

    @PostMapping("/create")
    public ResponseEntity<GenreDTO> addGenre(@RequestBody GenreDTO genreSTO) {
        GenreDTO createdGenreDTO = genreService.createGenre(genreSTO);
        return ResponseEntity.ok(createdGenreDTO);
    }

    @GetMapping
    public ResponseEntity<?> getAllGenre() {
        List<GenreDTO> genres = genreService.getAllActiveGenresWithSubGenre();
        return ResponseEntity.ok(genres);
    }

    @GetMapping("/{genreId}")
    public ResponseEntity<?> getGenreById(@PathVariable("genreId") Long genreId) throws GenreException {
        GenreDTO genres = genreService.getGenreById(genreId);
        return ResponseEntity.ok(genres);
    }

    @PutMapping("/{genreId}")
    public ResponseEntity<?> updateGenre(
            @PathVariable("genreId") Long genreId,
            @RequestBody GenreDTO genre
    ) throws GenreException {
        GenreDTO genres = genreService.updateGenre(genreId, genre);
        return ResponseEntity.ok(genres);
    }

    @DeleteMapping("/{genreId}")
    public ResponseEntity<?> deleteGenre(
            @PathVariable("genreId") Long genreId
    ) throws GenreException {
        genreService.deleteGenre(genreId);
        ApiResponse response = new ApiResponse("Genre Deleted - soft delete", true);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{genreId}/hard")
    public ResponseEntity<?> hardDeleteGenre(
            @PathVariable("genreId") Long genreId
    ) throws GenreException {
        genreService.hardDeleteGenre(genreId);
        ApiResponse response = new ApiResponse("Genre Deleted - hard delete", true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/top-level")
    public ResponseEntity<?> getTopLevelGenre() {
        List<GenreDTO> genres = genreService.getTopLevelGenre();
        return ResponseEntity.ok(genres);
    }

    @GetMapping("/count")
    public ResponseEntity<?> getTotalActiveGenres() {
        Long genres = genreService.getTotalActiveGenres();
        return ResponseEntity.ok(genres);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<GenreDTO>> popular(@RequestParam(defaultValue = "6") int limit) {
        return ResponseEntity.ok(genreService.getPopularGenres(limit));
    }

    @GetMapping("/{id}/book-count")
    public ResponseEntity<?> getBookCountByGenres(
            @PathVariable Long id
    ) {
        Long count = genreService.getBookCountByGenre(id);
        return ResponseEntity.ok(count);
    }


}
