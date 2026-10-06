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

    @jakarta.validation.constraints.Email
    @jakarta.validation.constraints.NotBlank
    private String email;

    @jakarta.validation.constraints.NotBlank
    @JsonProperty("fullName")
    private String fullName;

    private String userName;

    private UserRole role;

    @jakarta.validation.constraints.NotBlank
    @jakarta.validation.constraints.Size(max=30)
    private String phone;

    @jakarta.validation.constraints.NotBlank
    @jakarta.validation.constraints.Size(min=8, max=72)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private LocalDateTime lastLogin;


}
