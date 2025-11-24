package com.backend.backend.Producto.dto;

import com.backend.backend.Producto.domain.CategoriaProducto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    private Long id;
    private Long comercianteId;
    private String nombreComercio;
    private String nombre;
    private String descripcion;
    private CategoriaProducto categoria;
    private BigDecimal precio;
    private Integer stock;
    private LocalDateTime fechaCreacion;
}
