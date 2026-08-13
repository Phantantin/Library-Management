package com.zou.payload.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WaiveFineRequest {

    @NotNull(message = "Fine Id is mandatory")
    private Long fineId;

    @NotBlank(message = "Waiver reason is mandatory")
    private String reason;
}
