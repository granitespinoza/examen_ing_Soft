package com.backend.backend.OrdenItem.domain;

import com.backend.backend.Orden.domain.Orden;
import com.backend.backend.Producto.domain.Producto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "orden_id", nullable = false)
    private Orden orden;


    @ManyToOne
    @JoinColumn(name="producto_id", nullable = false)
    private Producto producto;

    private Integer cantidad;

    private BigDecimal precioUnitario;

    private BigDecimal subTotal;

    @PrePersist
    protected void onCreate(){
        if (subTotal == null) {
            subTotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));

        }
    }
}
