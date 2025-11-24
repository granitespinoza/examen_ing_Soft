package com.backend.backend.Grade.domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test para simular usuarios concurrentes (RNF02: Soportar 50 usuarios concurrentes).
 */
class ConcurrentUsersTest {
    
    private static final int CONCURRENT_USERS = 50;
    private static final double DELTA = 0.01;
    
    @Test
    void shouldHandleConcurrentCalculations() throws InterruptedException {
        GradeCalculator calculator = new GradeCalculator();
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_USERS);
        CountDownLatch latch = new CountDownLatch(CONCURRENT_USERS);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        
        List<Evaluation> evaluations = Arrays.asList(
            Evaluation.builder().grade(4.0).weight(50.0).build(),
            Evaluation.builder().grade(3.5).weight(50.0).build()
        );
        AttendancePolicy attendancePolicy = new AttendancePolicy(true);
        ExtraPointsPolicy extraPointsPolicy = new ExtraPointsPolicy(false);
        
        for (int i = 0; i < CONCURRENT_USERS; i++) {
            executor.submit(() -> {
                try {
                    GradeCalculationResult result = calculator.calculateFinalGrade(
                        evaluations, attendancePolicy, extraPointsPolicy
                    );
                    
                    // Verificar que el resultado sea correcto
                    assertEquals(3.75, result.getWeightedAverage(), DELTA);
                    assertEquals(3.75, result.getFinalGrade(), DELTA);
                    assertTrue(result.getCalculationTimeMs() < 300);
                    
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executor.shutdown();
        
        assertEquals(CONCURRENT_USERS, successCount.get());
        assertEquals(0, errorCount.get());
    }
    
    @Test
    void shouldProduceDeterministicResultsUnderConcurrency() throws InterruptedException {
        GradeCalculator calculator = new GradeCalculator();
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_USERS);
        CountDownLatch latch = new CountDownLatch(CONCURRENT_USERS);
        List<Double> results = new ArrayList<>();
        
        List<Evaluation> evaluations = Arrays.asList(
            Evaluation.builder().grade(4.0).weight(100.0).build()
        );
        AttendancePolicy attendancePolicy = new AttendancePolicy(true);
        ExtraPointsPolicy extraPointsPolicy = new ExtraPointsPolicy(true);
        
        for (int i = 0; i < CONCURRENT_USERS; i++) {
            executor.submit(() -> {
                try {
                    GradeCalculationResult result = calculator.calculateFinalGrade(
                        evaluations, attendancePolicy, extraPointsPolicy
                    );
                    synchronized (results) {
                        results.add(result.getFinalGrade());
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executor.shutdown();
        
        // Verificar que todos los resultados sean iguales (determinismo)
        double firstResult = results.get(0);
        for (double result : results) {
            assertEquals(firstResult, result, DELTA);
        }
    }
}

