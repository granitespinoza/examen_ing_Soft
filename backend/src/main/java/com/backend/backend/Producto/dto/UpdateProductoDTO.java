package com.backend.backend.Producto.dto;

import com.backend.backend.Producto.domain.CategoriaProducto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductoDTO {
    private String nombre;
    private String descripcion;
    private CategoriaProducto categoria;

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal precio;

    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;
}
