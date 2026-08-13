package com.zou.mapper;

import com.zou.modal.Genre;
import com.zou.payload.dto.GenreDTO;
import com.zou.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GenreMapper {

    private final GenreRepository genreRepository;

    public GenreDTO toGenreDTO(Genre savedGenre) {
        if (savedGenre == null) {
            return null;
        }

        GenreDTO dto = GenreDTO.builder()
                .id(savedGenre.getId())
                .code(savedGenre.getCode())
                .name(savedGenre.getName())
                .description(savedGenre.getDescription())
                .displayOrder(savedGenre.getDisplayOrder())
                .active(savedGenre.getActive())
                .createdAt(savedGenre.getCreatedAt())
                .updatedAt(savedGenre.getUpdatedAt())
                .build();

        if (savedGenre.getParentGenre() != null) {
            dto.setParentGenreId(savedGenre.getParentGenre().getId());
            dto.setParentGenreName(savedGenre.getParentGenre().getName());
        }

        // --- CÁCH SỬA AN TOÀN ---
        if (savedGenre.getSubGenres() != null) {
            dto.setSubGenre(savedGenre.getSubGenres().stream()
                    .filter(subGenre -> subGenre.getActive() != null && subGenre.getActive())
                    .map(this::toGenreDTO) // Sử dụng method reference cho sạch code
                    .collect(Collectors.toList()));
        } else {
            dto.setSubGenre(new ArrayList<>()); // Trả về list rỗng thay vì null để tránh lỗi ở Frontend
        }

        return dto;
    }
    
//    public GenreDTO toGenreDTO(Genre savedGenre) {
//        if (savedGenre == null) {
//            return null;
//        }
//
//        GenreDTO dto = GenreDTO.builder()
//                .id(savedGenre.getId())
//                .code(savedGenre.getCode())
//                .name(savedGenre.getName())
//                .description(savedGenre.getDescription())
//                .displayOrder(savedGenre.getDisplayOrder())
//                .active(savedGenre.getActive())
//                .createdAt(savedGenre.getCreatedAt())
//                .updatedAt(savedGenre.getUpdatedAt())
//                .build();
//
//        if(savedGenre.getParentGenre()!=null){
//            dto.setParentGenreId(savedGenre.getParentGenre().getId());
//            dto.setParentGenreName(savedGenre.getParentGenre().getName());
//        }
//
//        if(savedGenre.getSubGenres() != null && !savedGenre.getSubGenres().isEmpty()){
//
//        }
//        dto.setSubGenre(savedGenre.getSubGenres().stream()
//                .filter(subGenre-> subGenre.getActive())
//                .map(subGenre-> toGenreDTO(subGenre)).collect(Collectors.toList()));
//
//        return dto;
//    }

     public Genre toEntity(GenreDTO genreDTO) {
         if (genreDTO == null) {
             return null;
         }

         Genre genre = Genre.builder()
                 .code(genreDTO.getCode())
                 .name(genreDTO.getName())
                 .description(genreDTO.getDescription())
                 .displayOrder(genreDTO.getDisplayOrder())
                 .active(genreDTO.getActive())
                 .build();

         if (genreDTO.getParentGenreId() != null) {
             genreRepository.findById(genreDTO.getParentGenreId())
                     .ifPresent(genre::setParentGenre);
         }

        return genre;
     }

     public void updateEntityFromDTO(GenreDTO dto, Genre existingGenre) {
        if (dto == null || existingGenre == null) {
            return;
        }
         existingGenre.setCode(dto.getCode());
         existingGenre.setName(dto.getName());
         existingGenre.setDescription(dto.getDescription());
         existingGenre.setDisplayOrder(dto.getDisplayOrder());
        if(dto.getActive()!=null){
            existingGenre.setActive(dto.getActive());
        }
        if(dto.getParentGenreId()!=null){
            genreRepository.findById(dto.getParentGenreId())
                    .ifPresent(existingGenre::setParentGenre);
        }
     }

     public List<GenreDTO> toDTOList(List<Genre> genreList) {
        return genreList.stream().map(genre -> toGenreDTO(genre)).collect(Collectors.toList());
     }


}
