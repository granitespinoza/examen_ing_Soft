package com.backend.backend.Grade.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Resultado del cálculo de nota final con todos los detalles.
 * RF05: Visualizar detalle: promedio ponderado, penalización por inasistencias, puntos extra.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeCalculationResult {
    
    private double weightedAverage;
    private double attendancePenalty;
    private double gradeAfterAttendance;
    private double extraPoints;
    private double finalGrade;
    private long calculationTimeMs;
}

