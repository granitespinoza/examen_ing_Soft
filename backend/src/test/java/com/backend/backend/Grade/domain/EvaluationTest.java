package com.backend.backend.Grade.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EvaluationTest {
    
    @Test
    void shouldValidateSuccessfullyWhenGradeAndWeightAreValid() {
        Evaluation evaluation = Evaluation.builder()
                .grade(4.5)
                .weight(30.0)
                .build();
        
        assertDoesNotThrow(() -> evaluation.validate());
    }
    
    @Test
    void shouldThrowExceptionWhenGradeIsNull() {
        Evaluation evaluation = Evaluation.builder()
                .grade(null)
                .weight(30.0)
                .build();
        
        assertThrows(IllegalArgumentException.class, () -> evaluation.validate());
    }
    
    @Test
    void shouldThrowExceptionWhenGradeIsNegative() {
        Evaluation evaluation = Evaluation.builder()
                .grade(-1.0)
                .weight(30.0)
                .build();
        
        assertThrows(IllegalArgumentException.class, () -> evaluation.validate());
    }
    
    @Test
    void shouldThrowExceptionWhenGradeExceedsMaximum() {
        Evaluation evaluation = Evaluation.builder()
                .grade(6.0)
                .weight(30.0)
                .build();
        
        assertThrows(IllegalArgumentException.class, () -> evaluation.validate());
    }
    
    @Test
    void shouldThrowExceptionWhenWeightIsNull() {
        Evaluation evaluation = Evaluation.builder()
                .grade(4.5)
                .weight(null)
                .build();
        
        assertThrows(IllegalArgumentException.class, () -> evaluation.validate());
    }
    
    @Test
    void shouldThrowExceptionWhenWeightIsZero() {
        Evaluation evaluation = Evaluation.builder()
                .grade(4.5)
                .weight(0.0)
                .build();
        
        assertThrows(IllegalArgumentException.class, () -> evaluation.validate());
    }
    
    @Test
    void shouldThrowExceptionWhenWeightExceedsMaximum() {
        Evaluation evaluation = Evaluation.builder()
                .grade(4.5)
                .weight(101.0)
                .build();
        
        assertThrows(IllegalArgumentException.class, () -> evaluation.validate());
    }
    
    @Test
    void shouldAcceptGradeAtMinimumBoundary() {
        Evaluation evaluation = Evaluation.builder()
                .grade(0.0)
                .weight(30.0)
                .build();
        
        assertDoesNotThrow(() -> evaluation.validate());
    }
    
    @Test
    void shouldAcceptGradeAtMaximumBoundary() {
        Evaluation evaluation = Evaluation.builder()
                .grade(5.0)
                .weight(30.0)
                .build();
        
        assertDoesNotThrow(() -> evaluation.validate());
    }
}

