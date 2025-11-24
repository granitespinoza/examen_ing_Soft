package com.backend.backend.ItemCarrito.domain;


import com.backend.backend.Carrito.domain.Carrito;
import com.backend.backend.Producto.domain.Producto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCarrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="carrito_id", nullable = false)
    private Carrito carrito;

    @ManyToOne
    @JoinColumn(name="producto_id", nullable = false)
    private Producto producto;

    private Integer cantidad;
    private BigDecimal precioUnitario;


    public BigDecimal getTotal(){
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
}
