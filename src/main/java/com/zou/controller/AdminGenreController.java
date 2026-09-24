package com.zou.controller;

import com.zou.payload.dto.GenreDTO;
import com.zou.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/genres")
public class AdminGenreController {
    private final GenreService genres;
    @GetMapping
    public ResponseEntity<List<GenreDTO>> list() {
        return ResponseEntity.ok(genres.getAllGenres());
    }
}
