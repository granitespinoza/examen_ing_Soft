package com.backend.backend.Grade.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExtraPointsPolicyTest {
    
    private static final double DELTA = 0.01;
    
    @Test
    void shouldReturnZeroExtraPointsWhenNoAgreement() {
        ExtraPointsPolicy policy = new ExtraPointsPolicy(false);
        
        double extraPoints = policy.calculateExtraPoints();
        
        assertEquals(0.0, extraPoints, DELTA);
    }
    
    @Test
    void shouldReturnExtraPointsWhenAgreementExists() {
        ExtraPointsPolicy policy = new ExtraPointsPolicy(true);
        
        double extraPoints = policy.calculateExtraPoints();
        
        assertEquals(0.5, extraPoints, DELTA);
    }
    
    @Test
    void shouldNotApplyExtraPointsWhenNoAgreement() {
        ExtraPointsPolicy policy = new ExtraPointsPolicy(false);
        double currentGrade = 4.0;
        
        double result = policy.applyExtraPoints(currentGrade);
        
        assertEquals(4.0, result, DELTA);
    }
    
    @Test
    void shouldApplyExtraPointsWhenAgreementExists() {
        ExtraPointsPolicy policy = new ExtraPointsPolicy(true);
        double currentGrade = 4.0;
        
        double result = policy.applyExtraPoints(currentGrade);
        
        assertEquals(4.5, result, DELTA);
    }
    
    @Test
    void shouldNotExceedMaximumGradeWhenApplyingExtraPoints() {
        ExtraPointsPolicy policy = new ExtraPointsPolicy(true);
        double currentGrade = 4.8;
        
        double result = policy.applyExtraPoints(currentGrade);
        
        assertEquals(5.0, result, DELTA);
    }
    
    @Test
    void shouldCapAtMaximumWhenGradePlusExtraExceedsMaximum() {
        ExtraPointsPolicy policy = new ExtraPointsPolicy(true);
        double currentGrade = 5.0;
        
        double result = policy.applyExtraPoints(currentGrade);
        
        assertEquals(5.0, result, DELTA);
    }
    
    @Test
    void shouldReturnTrueWhenHasExtraPoints() {
        ExtraPointsPolicy policy = new ExtraPointsPolicy(true);
        
        assertTrue(policy.hasExtraPoints());
    }
    
    @Test
    void shouldReturnFalseWhenNoExtraPoints() {
        ExtraPointsPolicy policy = new ExtraPointsPolicy(false);
        
        assertFalse(policy.hasExtraPoints());
    }
}

