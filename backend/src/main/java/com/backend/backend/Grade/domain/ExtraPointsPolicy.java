package com.backend.backend.Grade.domain;

/**
 * Política de puntos extra basada en el acuerdo de docentes.
 * RF03: Registrar acuerdo de docentes para puntos extra.
 */
public class ExtraPointsPolicy {
    
    private static final double EXTRA_POINTS = 0.5; // 0.5 puntos extra
    private static final double MAXIMUM_GRADE = 5.0; // Nota máxima permitida
    
    private final boolean allYearsTeachers;
    
    public ExtraPointsPolicy(boolean allYearsTeachers) {
        this.allYearsTeachers = allYearsTeachers;
    }
    
    /**
     * Calcula los puntos extra a aplicar.
     * @return Los puntos extra (0.5 si hay acuerdo, 0.0 si no)
     */
    public double calculateExtraPoints() {
        if (allYearsTeachers) {
            return EXTRA_POINTS;
        }
        return 0.0;
    }
    
    /**
     * Aplica los puntos extra a la nota, respetando el máximo.
     * @param currentGrade La nota actual
     * @return La nota con puntos extra aplicados (máximo 5.0)
     */
    public double applyExtraPoints(double currentGrade) {
        double extraPoints = calculateExtraPoints();
        double result = currentGrade + extraPoints;
        return Math.min(result, MAXIMUM_GRADE);
    }
    
    public boolean hasExtraPoints() {
        return allYearsTeachers;
    }
    
    public double getExtraPointsValue() {
        return EXTRA_POINTS;
    }
}

