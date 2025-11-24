package com.backend.backend.common.mapper;

import com.backend.backend.Producto.domain.Producto;
import com.backend.backend.Producto.dto.ProductoDTO;

public class ProductoMapper {
    public static ProductoDTO toDTO(Producto producto) {
        if (producto == null) {
            return null;
        }
        return ProductoDTO.builder()
                .id(producto.getId())
                .comercianteId(producto.getComerciante().getId())
                .nombreComercio(producto.getComerciante().getNombreNegocio())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .categoria(producto.getCategoria())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .fechaCreacion(producto.getFechaCreacion())
                .build();
    }
}
