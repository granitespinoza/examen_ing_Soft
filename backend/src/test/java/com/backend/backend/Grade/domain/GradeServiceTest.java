package com.backend.backend.Grade.domain;

import com.backend.backend.Grade.dto.CalculateGradeRequestDTO;
import com.backend.backend.Grade.dto.EvaluationDTO;
import com.backend.backend.Grade.dto.GradeResultDTO;
import com.backend.backend.common.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GradeServiceTest {
    
    @Mock
    private GradeCalculator gradeCalculator;
    
    @InjectMocks
    private GradeService gradeService;
    
    private CalculateGradeRequestDTO request;
    private GradeCalculationResult calculationResult;
    
    @BeforeEach
    void setUp() {
        request = CalculateGradeRequestDTO.builder()
                .examsStudents(Arrays.asList(
                    EvaluationDTO.builder().grade(4.0).weight(50.0).build(),
                    EvaluationDTO.builder().grade(3.5).weight(50.0).build()
                ))
                .hasReachedMinimumClasses(true)
                .allYearsTeachers(false)
                .build();
        
        calculationResult = GradeCalculationResult.builder()
                .weightedAverage(3.75)
                .attendancePenalty(0.0)
                .gradeAfterAttendance(3.75)
                .extraPoints(0.0)
                .finalGrade(3.75)
                .calculationTimeMs(10)
                .build();
    }
    
    @Test
    void shouldCalculateFinalGradeSuccessfully() {
        when(gradeCalculator.calculateFinalGrade(anyList(), any(AttendancePolicy.class), any(ExtraPointsPolicy.class)))
                .thenReturn(calculationResult);
        
        GradeResultDTO result = gradeService.calculateFinalGrade(request);
        
        assertNotNull(result);
        assertEquals(3.75, result.getWeightedAverage(), 0.01);
        assertEquals(3.75, result.getFinalGrade(), 0.01);
        assertNotNull(result.getMessage());
        verify(gradeCalculator).calculateFinalGrade(anyList(), any(), any());
    }
    
    @Test
    void shouldThrowExceptionWhenEvaluationsListIsEmpty() {
        request.setExamsStudents(new ArrayList<>());
        
        assertThrows(BadRequestException.class, 
            () -> gradeService.calculateFinalGrade(request));
    }
    
    @Test
    void shouldThrowExceptionWhenEvaluationsListIsNull() {
        request.setExamsStudents(null);
        
        assertThrows(BadRequestException.class, 
            () -> gradeService.calculateFinalGrade(request));
    }
    
    @Test
    void shouldThrowExceptionWhenExceedingMaxEvaluations() {
        List<EvaluationDTO> evaluations = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            evaluations.add(EvaluationDTO.builder().grade(4.0).weight(10.0).build());
        }
        request.setExamsStudents(evaluations);
        
        assertThrows(BadRequestException.class, 
            () -> gradeService.calculateFinalGrade(request));
    }
    
    @Test
    void shouldAcceptMaximumEvaluations() {
        List<EvaluationDTO> evaluations = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            evaluations.add(EvaluationDTO.builder().grade(4.0).weight(10.0).build());
        }
        request.setExamsStudents(evaluations);
        
        when(gradeCalculator.calculateFinalGrade(anyList(), any(), any()))
                .thenReturn(calculationResult);
        
        assertDoesNotThrow(() -> gradeService.calculateFinalGrade(request));
    }
    
    @Test
    void shouldCalculateWithAttendancePenalty() {
        request.setHasReachedMinimumClasses(false);
        calculationResult.setAttendancePenalty(0.375);
        calculationResult.setGradeAfterAttendance(3.375);
        calculationResult.setFinalGrade(3.375);
        
        when(gradeCalculator.calculateFinalGrade(anyList(), any(), any()))
                .thenReturn(calculationResult);
        
        GradeResultDTO result = gradeService.calculateFinalGrade(request);
        
        assertEquals(0.375, result.getAttendancePenalty(), 0.01);
        assertEquals(3.375, result.getGradeAfterAttendance(), 0.01);
        assertTrue(result.getMessage().contains("Penalización por inasistencias"));
    }
    
    @Test
    void shouldCalculateWithExtraPoints() {
        request.setAllYearsTeachers(true);
        calculationResult.setExtraPoints(0.5);
        calculationResult.setFinalGrade(4.25);
        
        when(gradeCalculator.calculateFinalGrade(anyList(), any(), any()))
                .thenReturn(calculationResult);
        
        GradeResultDTO result = gradeService.calculateFinalGrade(request);
        
        assertEquals(0.5, result.getExtraPoints(), 0.01);
        assertEquals(4.25, result.getFinalGrade(), 0.01);
        assertTrue(result.getMessage().contains("Puntos extra aplicados"));
    }
    
    @Test
    void shouldCalculateWithBothPenaltyAndExtraPoints() {
        request.setHasReachedMinimumClasses(false);
        request.setAllYearsTeachers(true);
        calculationResult.setAttendancePenalty(0.375);
        calculationResult.setGradeAfterAttendance(3.375);
        calculationResult.setExtraPoints(0.5);
        calculationResult.setFinalGrade(3.875);
        
        when(gradeCalculator.calculateFinalGrade(anyList(), any(), any()))
                .thenReturn(calculationResult);
        
        GradeResultDTO result = gradeService.calculateFinalGrade(request);
        
        assertEquals(0.375, result.getAttendancePenalty(), 0.01);
        assertEquals(0.5, result.getExtraPoints(), 0.01);
        assertEquals(3.875, result.getFinalGrade(), 0.01);
    }
    
    @Test
    void shouldRoundResultsToTwoDecimals() {
        calculationResult.setWeightedAverage(3.756789);
        calculationResult.setFinalGrade(3.756789);
        
        when(gradeCalculator.calculateFinalGrade(anyList(), any(), any()))
                .thenReturn(calculationResult);
        
        GradeResultDTO result = gradeService.calculateFinalGrade(request);
        
        assertEquals(3.76, result.getWeightedAverage(), 0.01);
        assertEquals(3.76, result.getFinalGrade(), 0.01);
    }
}

