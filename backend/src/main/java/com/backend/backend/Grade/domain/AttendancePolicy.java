package com.backend.backend.Grade.domain;

/**
 * Política de asistencia que determina la penalización por inasistencias.
 * RF02: Registrar si cumplió asistencia mínima.
 */
public class AttendancePolicy {
    
    private static final double PENALTY_PERCENTAGE = 0.1; // 10% de penalización
    
    private final boolean hasReachedMinimumClasses;
    
    public AttendancePolicy(boolean hasReachedMinimumClasses) {
        this.hasReachedMinimumClasses = hasReachedMinimumClasses;
    }
    
    /**
     * Calcula la penalización por inasistencias.
     * @param weightedAverage El promedio ponderado antes de aplicar penalización
     * @return La penalización aplicada (0.0 si cumplió asistencia, 10% del promedio si no)
     */
    public double calculatePenalty(double weightedAverage) {
        if (hasReachedMinimumClasses) {
            return 0.0;
        }
        return weightedAverage * PENALTY_PERCENTAGE;
    }
    
    /**
     * Aplica la penalización al promedio ponderado.
     * @param weightedAverage El promedio ponderado
     * @return El promedio después de aplicar la penalización
     */
    public double applyPenalty(double weightedAverage) {
        double penalty = calculatePenalty(weightedAverage);
        double result = weightedAverage - penalty;
        return Math.max(0.0, result); // No puede ser negativo
    }
    
    public boolean hasReachedMinimumClasses() {
        return hasReachedMinimumClasses;
    }
    
    public double getPenaltyPercentage() {
        return PENALTY_PERCENTAGE;
    }
}

