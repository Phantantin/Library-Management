package com.zou.payload.request;
import jakarta.validation.constraints.*;
public record ProfileRequest(@NotBlank @Size(max=100) String fullName, @NotBlank @Size(max=30) String phone) {}
