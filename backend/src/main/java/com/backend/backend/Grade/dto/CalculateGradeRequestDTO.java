package com.backend.backend.Grade.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para solicitar el cálculo de nota final.
 * RF01-RF04: Contiene todas las variables necesarias para el cálculo.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculateGradeRequestDTO {
    
    @NotNull(message = "La lista de evaluaciones es obligatoria")
    @Size(min = 1, max = 10, message = "Debe haber entre 1 y 10 evaluaciones")
    @Valid
    private List<EvaluationDTO> examsStudents;
    
    @NotNull(message = "El indicador de asistencia mínima es obligatorio")
    private Boolean hasReachedMinimumClasses;
    
    @NotNull(message = "El acuerdo de docentes es obligatorio")
    private Boolean allYearsTeachers;
}

