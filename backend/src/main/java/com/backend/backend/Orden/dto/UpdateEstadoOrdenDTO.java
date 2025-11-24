package com.backend.backend.Orden.dto;

import com.backend.backend.Orden.domain.EstadoOrden;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEstadoOrdenDTO {
    @NotNull(message = "Estado is required")
    private EstadoOrden estado;
}
