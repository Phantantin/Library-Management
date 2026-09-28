package com.zou.modal;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Genre code is Mandatory")
    @Column(unique = true)
    private String code;

    @NotBlank(message = "Genre name is mandatory")
    private String name;

    @Size(max= 500, message = "Description must not exceed 500 character ")
    private String description;

    @Min(value = 0, message = "display order cannot be negative")
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @ManyToOne
    private Genre parentGenre;

    @OneToMany
    @Builder.Default
    private List<Genre> subGenres = new ArrayList<Genre>();

//    @OneToMany(mappedBy = "genre", cascade = CascadeType.PERSIST)
//    private List<Book> books = new ArrayList<Book>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
