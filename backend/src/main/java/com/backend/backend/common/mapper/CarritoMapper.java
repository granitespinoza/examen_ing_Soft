package com.backend.backend.common.mapper;

import com.backend.backend.Carrito.domain.Carrito;
import com.backend.backend.Carrito.dto.CarritoDTO;
import com.backend.backend.Carrito.dto.ItemCarritoDTO;
import com.backend.backend.ItemCarrito.domain.ItemCarrito;

import java.math.BigDecimal;
import java.util.stream.Collectors;

public class CarritoMapper {
    public static CarritoDTO toDTO(Carrito carrito) {
        if (carrito == null) {
            return null;
        }

        BigDecimal total = carrito.getItems().stream()
                .map(ItemCarrito::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CarritoDTO.builder()
                .id(carrito.getId())
                .clienteId(carrito.getCliente().getId())
                .items(carrito.getItems().stream()
                        .map(CarritoMapper::itemToDTO)
                        .collect(Collectors.toList()))
                .total(total)
                .fechaCreacion(carrito.getFechaCreacion())
                .fechaActualizacion(carrito.getFechaActualizacion())
                .build();
    }

    public static ItemCarritoDTO itemToDTO(ItemCarrito item) {
        if (item == null) {
            return null;
        }
        return ItemCarritoDTO.builder()
                .id(item.getId())
                .productoId(item.getProducto().getId())
                .productoNombre(item.getProducto().getNombre())
                .cantidad(item.getCantidad())
                .precioUnitario(item.getPrecioUnitario())
                .total(item.getTotal())
                .build();
    }
}
