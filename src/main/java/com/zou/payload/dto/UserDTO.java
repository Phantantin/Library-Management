package com.zou.payload.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.zou.domain.AuthProvider;
import com.zou.domain.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Long id;

    @NotNull(message = "email is required")
    private String email;

    @NotNull(message = "fullName is required")
    @JsonProperty("fullName")
    private String fullName;

    private String userName;

    private UserRole role;

    private String phone;

    @NotNull(message = "password is required")
    private String password;

    private LocalDateTime lastLogin;


}
