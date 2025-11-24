package com.backend.backend.Grade.domain;

import com.backend.backend.Grade.dto.CalculateGradeRequestDTO;
import com.backend.backend.Grade.dto.EvaluationDTO;
import com.backend.backend.Grade.dto.GradeResultDTO;
import com.backend.backend.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar el cálculo de notas finales.
 * Implementa todos los RF y RNF.
 */
@Service
@RequiredArgsConstructor
public class GradeService {
    
    private static final int MAX_EVALUATIONS = 10; // RNF01: Máximo 10 evaluaciones
    
    private final GradeCalculator gradeCalculator;
    
    /**
     * Calcula la nota final de un estudiante.
     * RF04: Solicitar cálculo de nota final.
     * 
     * @param request DTO con los datos del estudiante
     * @return Resultado del cálculo con todos los detalles
     */
    public GradeResultDTO calculateFinalGrade(CalculateGradeRequestDTO request) {
        // Validar número de evaluaciones (RNF01)
        if (request.getExamsStudents() == null || request.getExamsStudents().isEmpty()) {
            throw new BadRequestException("Debe haber al menos una evaluación");
        }
        
        if (request.getExamsStudents().size() > MAX_EVALUATIONS) {
            throw new BadRequestException(
                String.format("No se pueden registrar más de %d evaluaciones", MAX_EVALUATIONS)
            );
        }
        
        // Convertir DTOs a entidades de dominio
        List<Evaluation> evaluations = request.getExamsStudents().stream()
                .map(this::toEvaluation)
                .collect(Collectors.toList());
        
        // Crear políticas
        AttendancePolicy attendancePolicy = new AttendancePolicy(
            request.getHasReachedMinimumClasses()
        );
        
        ExtraPointsPolicy extraPointsPolicy = new ExtraPointsPolicy(
            request.getAllYearsTeachers()
        );
        
        // Calcular nota final
        GradeCalculationResult result = gradeCalculator.calculateFinalGrade(
            evaluations,
            attendancePolicy,
            extraPointsPolicy
        );
        
        // Convertir a DTO de respuesta
        return GradeResultDTO.builder()
                .weightedAverage(roundToTwoDecimals(result.getWeightedAverage()))
                .attendancePenalty(roundToTwoDecimals(result.getAttendancePenalty()))
                .gradeAfterAttendance(roundToTwoDecimals(result.getGradeAfterAttendance()))
                .extraPoints(roundToTwoDecimals(result.getExtraPoints()))
                .finalGrade(roundToTwoDecimals(result.getFinalGrade()))
                .calculationTimeMs(result.getCalculationTimeMs())
                .message(buildMessage(result, attendancePolicy, extraPointsPolicy))
                .build();
    }
    
    /**
     * Convierte un EvaluationDTO a Evaluation.
     */
    private Evaluation toEvaluation(EvaluationDTO dto) {
        return Evaluation.builder()
                .grade(dto.getGrade())
                .weight(dto.getWeight())
                .build();
    }
    
    /**
     * Redondea un valor a 2 decimales.
     */
    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
    
    /**
     * Construye un mensaje descriptivo del resultado.
     */
    private String buildMessage(
            GradeCalculationResult result,
            AttendancePolicy attendancePolicy,
            ExtraPointsPolicy extraPointsPolicy) {
        
        StringBuilder message = new StringBuilder();
        message.append(String.format("Promedio ponderado: %.2f", result.getWeightedAverage()));
        
        if (result.getAttendancePenalty() > 0) {
            message.append(String.format(
                ". Penalización por inasistencias: -%.2f (%.0f%%)",
                result.getAttendancePenalty(),
                attendancePolicy.getPenaltyPercentage() * 100
            ));
        } else {
            message.append(". Asistencia mínima cumplida: sin penalización");
        }
        
        if (result.getExtraPoints() > 0) {
            message.append(String.format(
                ". Puntos extra aplicados: +%.2f",
                result.getExtraPoints()
            ));
        }
        
        message.append(String.format(". Nota final: %.2f", result.getFinalGrade()));
        
        return message.toString();
    }
}

