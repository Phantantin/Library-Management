package com.zou.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {

    @jakarta.validation.constraints.NotBlank
    private String token;
    @jakarta.validation.constraints.NotBlank
    @jakarta.validation.constraints.Size(min=8,max=72)
    private String password;
}
