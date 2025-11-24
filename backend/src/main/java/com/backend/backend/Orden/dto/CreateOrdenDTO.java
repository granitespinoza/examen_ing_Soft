package com.backend.backend.Orden.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrdenDTO {
    @NotBlank(message = "Shipping address is required")
    private String direccionEnvio;
}
