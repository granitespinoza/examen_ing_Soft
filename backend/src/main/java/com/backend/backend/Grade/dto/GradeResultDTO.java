package com.backend.backend.Grade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO con el resultado del cálculo de nota final.
 * RF05: Visualizar detalle: promedio ponderado, penalización por inasistencias, puntos extra.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeResultDTO {
    
    private double weightedAverage;
    private double attendancePenalty;
    private double gradeAfterAttendance;
    private double extraPoints;
    private double finalGrade;
    private long calculationTimeMs;
    
    private String message;
}

