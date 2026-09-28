package com.zou.modal;

import com.zou.domain.AuthProvider;
import com.zou.domain.UserRole;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @jakarta.persistence.Column(nullable = false, unique = true)
    private String email;

    private String fullName;

    @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    private UserRole role;

    private String phone;

    private String userName;

    @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    @Builder.Default
    private AuthProvider authProvider=AuthProvider.LOCAL;

    private String googleId;

    private String profileImage;

    @com.fasterxml.jackson.annotation.JsonProperty(access = com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY)
    @jakarta.persistence.Column(nullable = false)
    private String password;

    private LocalDateTime lastLogin;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
