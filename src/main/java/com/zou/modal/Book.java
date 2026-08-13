package com.zou.modal;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String isbn;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @JoinColumn(nullable = false)
    @ManyToOne
    private Genre genre;

    private String publisher;

    private LocalDate publicsheDate;

    private String language;

    private Integer pages;

    private String description;

    @Column(nullable = false)
    private Integer totalCopies;

    @Column(nullable = false)
    private Integer availableCopies;

    private BigDecimal price;

    private String coverImageUrl;

    @Column(nullable = false)
    private Boolean active=true;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @AssertTrue(message = "Available copies cannot exceed total copies")
    public boolean isVailableCopesValid() {
        if(totalCopies == null || availableCopies == null) {
            return true;
        }
        return totalCopies >= availableCopies;
    }
}
