package com.backend.backend.Producto.dto;

import com.backend.backend.Producto.domain.CategoriaProducto;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductoDTO {
    @NotBlank(message = "Product name is required")
    private String nombre;

    private String descripcion;

    @NotNull(message = "Category is required")
    private CategoriaProducto categoria;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal precio;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;
}
