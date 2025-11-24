package com.backend.backend.Grade.domain;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Calculadora de notas finales que implementa la lógica de cálculo determinista.
 * RF04: Solicitar cálculo de nota final.
 * RNF03: Cálculo determinista (mismos datos = misma nota).
 * RNF04: Tiempo de cálculo < 300 ms.
 */
@Component
public class GradeCalculator {
    
    private static final double TOTAL_WEIGHT_PERCENTAGE = 100.0;
    private static final double TOLERANCE = 0.01; // Tolerancia para comparación de pesos
    private static final long MAX_CALCULATION_TIME_MS = 300; // RNF04: Tiempo máximo de cálculo en milisegundos
    
    /**
     * Calcula el promedio ponderado de las evaluaciones.
     * @param evaluations Lista de evaluaciones
     * @return El promedio ponderado
     */
    public double calculateWeightedAverage(List<Evaluation> evaluations) {
        if (evaluations == null || evaluations.isEmpty()) {
            throw new IllegalArgumentException("La lista de evaluaciones no puede estar vacía");
        }
        
        double totalWeightedSum = 0.0;
        double totalWeight = 0.0;
        
        for (Evaluation evaluation : evaluations) {
            evaluation.validate();
            totalWeightedSum += evaluation.getGrade() * evaluation.getWeight();
            totalWeight += evaluation.getWeight();
        }
        
        // Validar que los pesos sumen aproximadamente 100%
        if (Math.abs(totalWeight - TOTAL_WEIGHT_PERCENTAGE) > TOLERANCE) {
            throw new IllegalArgumentException(
                String.format("La suma de pesos debe ser 100%%. Actual: %.2f%%", totalWeight)
            );
        }
        
        return totalWeightedSum / totalWeight;
    }
    
    /**
     * Calcula la nota final considerando evaluaciones, asistencia y puntos extra.
     * @param evaluations Lista de evaluaciones
     * @param attendancePolicy Política de asistencia
     * @param extraPointsPolicy Política de puntos extra
     * @return El resultado del cálculo con todos los detalles
     */
    public GradeCalculationResult calculateFinalGrade(
            List<Evaluation> evaluations,
            AttendancePolicy attendancePolicy,
            ExtraPointsPolicy extraPointsPolicy) {
        
        long startTime = System.currentTimeMillis();
        
        // 1. Calcular promedio ponderado
        double weightedAverage = calculateWeightedAverage(evaluations);
        
        // 2. Aplicar penalización por asistencia
        double penalty = attendancePolicy.calculatePenalty(weightedAverage);
        double gradeAfterAttendance = attendancePolicy.applyPenalty(weightedAverage);
        
        // 3. Aplicar puntos extra
        double extraPoints = extraPointsPolicy.calculateExtraPoints();
        double finalGrade = extraPointsPolicy.applyExtraPoints(gradeAfterAttendance);
        
        long calculationTime = System.currentTimeMillis() - startTime;
        
        // RNF04: Validar tiempo de cálculo
        if (calculationTime >= MAX_CALCULATION_TIME_MS) {
            throw new RuntimeException(
                String.format("El cálculo excedió el tiempo máximo permitido: %d ms", calculationTime)
            );
        }
        
        return GradeCalculationResult.builder()
                .weightedAverage(weightedAverage)
                .attendancePenalty(penalty)
                .gradeAfterAttendance(gradeAfterAttendance)
                .extraPoints(extraPoints)
                .finalGrade(finalGrade)
                .calculationTimeMs(calculationTime)
                .build();
    }
}

