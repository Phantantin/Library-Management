
package com.zou.repository;

import com.zou.modal.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GenreRepository extends JpaRepository<Genre, Long> {

        List<Genre> findByActiveTrueOrderByDisplayOrderAsc();
        List<Genre> findByParentGenreIsNullAndActiveTrueOrderByDisplayOrderAsc();
        List<Genre> findByParentGenreIsNullAndActiveTrueOrderByDisplayOrderAsc(
                Long parentGenreId
        );

        long countByActiveTrue();

    //    @Query("select count(b) from book b where b.genre.id= :genreId")
    //    long countBooksByGenre(@Param("genreId") Long genreId);
    }

