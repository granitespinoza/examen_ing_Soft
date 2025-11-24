package com.backend.backend.Grade.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar una evaluación.
 * RF01: Registrar evaluaciones (nota + peso %).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationDTO {
    
    @NotNull(message = "La nota es obligatoria")
    @DecimalMin(value = "0.0", message = "La nota debe ser mayor o igual a 0.0")
    @DecimalMax(value = "5.0", message = "La nota debe ser menor o igual a 5.0")
    private Double grade;
    
    @NotNull(message = "El peso es obligatorio")
    @DecimalMin(value = "0.01", message = "El peso debe ser mayor a 0.0")
    @DecimalMax(value = "100.0", message = "El peso debe ser menor o igual a 100.0")
    private Double weight;
}

