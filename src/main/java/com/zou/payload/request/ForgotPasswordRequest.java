package com.zou.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordRequest {

    @jakarta.validation.constraints.NotBlank
    @jakarta.validation.constraints.Email
    private String email;
}
