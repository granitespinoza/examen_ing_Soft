package com.backend.backend.common.strategy;

import com.backend.backend.Orden.domain.Orden;

import java.math.BigDecimal;

public interface OrdenTotalCalculationStrategy {
    BigDecimal calculateTotal(Orden orden);
}
