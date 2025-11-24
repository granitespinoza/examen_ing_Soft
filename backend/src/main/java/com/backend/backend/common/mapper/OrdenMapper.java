package com.backend.backend.common.mapper;

import com.backend.backend.Orden.domain.Orden;
import com.backend.backend.Orden.dto.OrdenDTO;
import com.backend.backend.Orden.dto.OrdenItemDTO;
import com.backend.backend.OrdenItem.domain.OrdenItem;

import java.util.stream.Collectors;

public class OrdenMapper {
    public static OrdenDTO toDTO(Orden orden) {
        if (orden == null) {
            return null;
        }
        return OrdenDTO.builder()
                .id(orden.getId())
                .clienteId(orden.getCliente().getId())
                .items(orden.getItems().stream()
                        .map(OrdenMapper::itemToDTO)
                        .collect(Collectors.toList()))
                .fechaOrden(orden.getFechaOrden())
                .estado(orden.getEstado())
                .total(orden.getTotal())
                .direccionEnvio(orden.getDireccionEnvio())
                .build();
    }

    public static OrdenItemDTO itemToDTO(OrdenItem item) {
        if (item == null) {
            return null;
        }
        return OrdenItemDTO.builder()
                .id(item.getId())
                .productoId(item.getProducto().getId())
                .productoNombre(item.getProducto().getNombre())
                .cantidad(item.getCantidad())
                .precioUnitario(item.getPrecioUnitario())
                .subTotal(item.getSubTotal())
                .build();
    }
}
