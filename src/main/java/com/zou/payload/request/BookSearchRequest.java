package com.zou.payload.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookSearchRequest {

    private String searchTerm;

    private Long genreId;

    private Boolean availableOnly=false;

    private Boolean activeOnly=true;

    private Integer pageSize=20;

    private Integer page=0;

    private String sortBy="createdAt";

    private String sortDirection="DESC";
}
