package com.backend.backend.Grade.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GradeCalculatorTest {
    
    private GradeCalculator gradeCalculator;
    private static final double DELTA = 0.01;
    
    @BeforeEach
    void setUp() {
        gradeCalculator = new GradeCalculator();
    }
    
    @Test
    void shouldCalculateWeightedAverageCorrectly() {
        List<Evaluation> evaluations = Arrays.asList(
            Evaluation.builder().grade(4.0).weight(30.0).build(),
            Evaluation.builder().grade(3.5).weight(40.0).build(),
            Evaluation.builder().grade(4.5).weight(30.0).build()
        );
        
        double result = gradeCalculator.calculateWeightedAverage(evaluations);
        
        // (4.0*30 + 3.5*40 + 4.5*30) / 100 = (120 + 140 + 135) / 100 = 3.95
        assertEquals(3.95, result, DELTA);
    }
    
    @Test
    void shouldThrowExceptionWhenEvaluationsListIsEmpty() {
        List<Evaluation> evaluations = new ArrayList<>();
        
        assertThrows(IllegalArgumentException.class, 
            () -> gradeCalculator.calculateWeightedAverage(evaluations));
    }
    
    @Test
    void shouldThrowExceptionWhenEvaluationsListIsNull() {
        assertThrows(IllegalArgumentException.class, 
            () -> gradeCalculator.calculateWeightedAverage(null));
    }
    
    @Test
    void shouldThrowExceptionWhenWeightsDoNotSumTo100() {
        List<Evaluation> evaluations = Arrays.asList(
            Evaluation.builder().grade(4.0).weight(30.0).build(),
            Evaluation.builder().grade(3.5).weight(40.0).build()
        );
        
        assertThrows(IllegalArgumentException.class, 
            () -> gradeCalculator.calculateWeightedAverage(evaluations));
    }
    
    @Test
    void shouldCalculateFinalGradeWithAllComponents() {
        List<Evaluation> evaluations = Arrays.asList(
            Evaluation.builder().grade(4.0).weight(50.0).build(),
            Evaluation.builder().grade(3.5).weight(50.0).build()
        );
        AttendancePolicy attendancePolicy = new AttendancePolicy(true);
        ExtraPointsPolicy extraPointsPolicy = new ExtraPointsPolicy(true);
        
        GradeCalculationResult result = gradeCalculator.calculateFinalGrade(
            evaluations, attendancePolicy, extraPointsPolicy
        );
        
        assertEquals(3.75, result.getWeightedAverage(), DELTA);
        assertEquals(0.0, result.getAttendancePenalty(), DELTA);
        assertEquals(3.75, result.getGradeAfterAttendance(), DELTA);
        assertEquals(0.5, result.getExtraPoints(), DELTA);
        assertEquals(4.25, result.getFinalGrade(), DELTA);
        assertTrue(result.getCalculationTimeMs() < 300);
    }
    
    @Test
    void shouldCalculateFinalGradeWithAttendancePenalty() {
        List<Evaluation> evaluations = Arrays.asList(
            Evaluation.builder().grade(4.0).weight(100.0).build()
        );
        AttendancePolicy attendancePolicy = new AttendancePolicy(false);
        ExtraPointsPolicy extraPointsPolicy = new ExtraPointsPolicy(false);
        
        GradeCalculationResult result = gradeCalculator.calculateFinalGrade(
            evaluations, attendancePolicy, extraPointsPolicy
        );
        
        assertEquals(4.0, result.getWeightedAverage(), DELTA);
        assertEquals(0.4, result.getAttendancePenalty(), DELTA);
        assertEquals(3.6, result.getGradeAfterAttendance(), DELTA);
        assertEquals(0.0, result.getExtraPoints(), DELTA);
        assertEquals(3.6, result.getFinalGrade(), DELTA);
    }
    
    @Test
    void shouldCalculateFinalGradeWithExtraPoints() {
        List<Evaluation> evaluations = Arrays.asList(
            Evaluation.builder().grade(4.0).weight(100.0).build()
        );
        AttendancePolicy attendancePolicy = new AttendancePolicy(true);
        ExtraPointsPolicy extraPointsPolicy = new ExtraPointsPolicy(true);
        
        GradeCalculationResult result = gradeCalculator.calculateFinalGrade(
            evaluations, attendancePolicy, extraPointsPolicy
        );
        
        assertEquals(4.0, result.getWeightedAverage(), DELTA);
        assertEquals(0.0, result.getAttendancePenalty(), DELTA);
        assertEquals(4.0, result.getGradeAfterAttendance(), DELTA);
        assertEquals(0.5, result.getExtraPoints(), DELTA);
        assertEquals(4.5, result.getFinalGrade(), DELTA);
    }
    
    @Test
    void shouldCalculateFinalGradeWithBothPenaltyAndExtraPoints() {
        List<Evaluation> evaluations = Arrays.asList(
            Evaluation.builder().grade(4.0).weight(100.0).build()
        );
        AttendancePolicy attendancePolicy = new AttendancePolicy(false);
        ExtraPointsPolicy extraPointsPolicy = new ExtraPointsPolicy(true);
        
        GradeCalculationResult result = gradeCalculator.calculateFinalGrade(
            evaluations, attendancePolicy, extraPointsPolicy
        );
        
        assertEquals(4.0, result.getWeightedAverage(), DELTA);
        assertEquals(0.4, result.getAttendancePenalty(), DELTA);
        assertEquals(3.6, result.getGradeAfterAttendance(), DELTA);
        assertEquals(0.5, result.getExtraPoints(), DELTA);
        assertEquals(4.1, result.getFinalGrade(), DELTA);
    }
    
    @Test
    void shouldCalculateFinalGradeWithZeroEvaluations() {
        List<Evaluation> evaluations = new ArrayList<>();
        AttendancePolicy attendancePolicy = new AttendancePolicy(true);
        ExtraPointsPolicy extraPointsPolicy = new ExtraPointsPolicy(false);
        
        assertThrows(IllegalArgumentException.class, 
            () -> gradeCalculator.calculateFinalGrade(
                evaluations, attendancePolicy, extraPointsPolicy
            ));
    }
    
    @Test
    void shouldCalculateFinalGradeWithMaximumEvaluations() {
        List<Evaluation> evaluations = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            evaluations.add(Evaluation.builder().grade(4.0).weight(10.0).build());
        }
        AttendancePolicy attendancePolicy = new AttendancePolicy(true);
        ExtraPointsPolicy extraPointsPolicy = new ExtraPointsPolicy(false);
        
        GradeCalculationResult result = gradeCalculator.calculateFinalGrade(
            evaluations, attendancePolicy, extraPointsPolicy
        );
        
        assertEquals(4.0, result.getWeightedAverage(), DELTA);
        assertEquals(4.0, result.getFinalGrade(), DELTA);
    }
    
    @Test
    void shouldCalculateDeterministicResult() {
        List<Evaluation> evaluations = Arrays.asList(
            Evaluation.builder().grade(4.0).weight(50.0).build(),
            Evaluation.builder().grade(3.5).weight(50.0).build()
        );
        AttendancePolicy attendancePolicy = new AttendancePolicy(true);
        ExtraPointsPolicy extraPointsPolicy = new ExtraPointsPolicy(false);
        
        GradeCalculationResult result1 = gradeCalculator.calculateFinalGrade(
            evaluations, attendancePolicy, extraPointsPolicy
        );
        GradeCalculationResult result2 = gradeCalculator.calculateFinalGrade(
            evaluations, attendancePolicy, extraPointsPolicy
        );
        
        assertEquals(result1.getFinalGrade(), result2.getFinalGrade(), DELTA);
        assertEquals(result1.getWeightedAverage(), result2.getWeightedAverage(), DELTA);
    }
}

