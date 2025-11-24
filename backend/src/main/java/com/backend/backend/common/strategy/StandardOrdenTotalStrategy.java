package com.backend.backend.common.strategy;

import com.backend.backend.Orden.domain.Orden;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class StandardOrdenTotalStrategy implements OrdenTotalCalculationStrategy {
    @Override
    public BigDecimal calculateTotal(Orden orden) {
        return orden.getItems().stream()
                .map(item -> item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
