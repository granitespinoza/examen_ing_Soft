package com.backend.backend.Grade.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa una evaluación con su nota y peso porcentual.
 * RF01: Registrar evaluaciones (nota + peso %).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Evaluation {
    
    private static final double MIN_GRADE = 0.0;
    private static final double MAX_GRADE = 5.0;
    private static final double MIN_WEIGHT = 0.0;
    private static final double MAX_WEIGHT = 100.0;
    
    private Double grade;
    private Double weight;
    
    /**
     * Valida que la evaluación tenga valores válidos.
     * @throws IllegalArgumentException si los valores no son válidos
     */
    public void validate() {
        if (grade == null || grade < MIN_GRADE || grade > MAX_GRADE) {
            throw new IllegalArgumentException(
                String.format("La nota debe estar entre %.1f y %.1f", MIN_GRADE, MAX_GRADE)
            );
        }
        if (weight == null || weight <= MIN_WEIGHT || weight > MAX_WEIGHT) {
            throw new IllegalArgumentException(
                String.format("El peso debe estar entre %.2f y %.1f", MIN_WEIGHT + 0.01, MAX_WEIGHT)
            );
        }
    }
}

