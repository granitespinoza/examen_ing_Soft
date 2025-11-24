package com.backend.backend.Orden.dto;

import com.backend.backend.Orden.domain.EstadoOrden;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenDTO {
    private Long id;
    private Long clienteId;
    private List<OrdenItemDTO> items;
    private LocalDateTime fechaOrden;
    private EstadoOrden estado;
    private BigDecimal total;
    private String direccionEnvio;
}
