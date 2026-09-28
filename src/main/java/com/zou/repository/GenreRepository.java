
package com.zou.repository;

import com.zou.modal.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GenreRepository extends JpaRepository<Genre, Long> {

        List<Genre> findByActiveTrueOrderByDisplayOrderAsc();
        List<Genre> findByParentGenreIsNullAndActiveTrueOrderByDisplayOrderAsc();


        long countByActiveTrue();

        @org.springframework.data.jpa.repository.Query("select b.genre from Book b where b.active=true and b.genre.active=true group by b.genre order by count(b) desc")
        org.springframework.data.domain.Page<Genre> findPopular(org.springframework.data.domain.Pageable pageable);

    //    @Query("select count(b) from book b where b.genre.id= :genreId")
    //    long countBooksByGenre(@Param("genreId") Long genreId);
    }

