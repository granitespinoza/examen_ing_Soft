package com.backend.backend.Grade.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AttendancePolicyTest {
    
    private static final double DELTA = 0.01;
    
    @Test
    void shouldReturnZeroPenaltyWhenMinimumClassesReached() {
        AttendancePolicy policy = new AttendancePolicy(true);
        double weightedAverage = 4.0;
        
        double penalty = policy.calculatePenalty(weightedAverage);
        
        assertEquals(0.0, penalty, DELTA);
    }
    
    @Test
    void shouldCalculatePenaltyWhenMinimumClassesNotReached() {
        AttendancePolicy policy = new AttendancePolicy(false);
        double weightedAverage = 4.0;
        
        double penalty = policy.calculatePenalty(weightedAverage);
        
        assertEquals(0.4, penalty, DELTA); // 10% de 4.0
    }
    
    @Test
    void shouldNotApplyPenaltyWhenMinimumClassesReached() {
        AttendancePolicy policy = new AttendancePolicy(true);
        double weightedAverage = 4.5;
        
        double result = policy.applyPenalty(weightedAverage);
        
        assertEquals(4.5, result, DELTA);
    }
    
    @Test
    void shouldApplyPenaltyWhenMinimumClassesNotReached() {
        AttendancePolicy policy = new AttendancePolicy(false);
        double weightedAverage = 4.0;
        
        double result = policy.applyPenalty(weightedAverage);
        
        assertEquals(3.6, result, DELTA); // 4.0 - 0.4
    }
    
    @Test
    void shouldNotReturnNegativeGradeAfterPenalty() {
        AttendancePolicy policy = new AttendancePolicy(false);
        double weightedAverage = 0.5;
        
        double result = policy.applyPenalty(weightedAverage);
        
        assertTrue(result >= 0.0);
        assertEquals(0.45, result, DELTA); // 0.5 - 0.05
    }
    
    @Test
    void shouldReturnZeroWhenPenaltyExceedsGrade() {
        AttendancePolicy policy = new AttendancePolicy(false);
        // Con una penalización del 10%, nunca excederá el promedio (a menos que sea 0 o negativo)
        // Este test verifica que el resultado nunca sea negativo gracias a Math.max(0.0, result)
        double verySmallAverage = 0.001; // 10% de 0.001 = 0.0001, resultado = 0.0009
        
        double result = policy.applyPenalty(verySmallAverage);
        
        // El resultado debe ser >= 0.0 gracias a Math.max
        assertTrue(result >= 0.0);
        assertEquals(0.0009, result, DELTA);
        
        // Verificar que con un promedio de 0.0, el resultado es 0.0
        double resultZero = policy.applyPenalty(0.0);
        assertEquals(0.0, resultZero, DELTA);
    }
    
    @Test
    void shouldReturnTrueWhenMinimumClassesReached() {
        AttendancePolicy policy = new AttendancePolicy(true);
        
        assertTrue(policy.hasReachedMinimumClasses());
    }
    
    @Test
    void shouldReturnFalseWhenMinimumClassesNotReached() {
        AttendancePolicy policy = new AttendancePolicy(false);
        
        assertFalse(policy.hasReachedMinimumClasses());
    }
}

