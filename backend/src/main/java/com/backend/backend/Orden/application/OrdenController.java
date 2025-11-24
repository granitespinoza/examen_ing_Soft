package com.backend.backend.Orden.application;

import com.backend.backend.Orden.domain.OrdenService;
import com.backend.backend.Orden.dto.CreateOrdenDTO;
import com.backend.backend.Orden.dto.OrdenDTO;
import com.backend.backend.Orden.dto.UpdateEstadoOrdenDTO;
import com.backend.backend.common.dto.ResponseDTO;
import com.backend.backend.users.domain.Usuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ordenes")
@RequiredArgsConstructor
public class OrdenController {

    private final OrdenService ordenService;

    @PostMapping
    public ResponseEntity<ResponseDTO<OrdenDTO>> createOrden(
            @Valid @RequestBody CreateOrdenDTO request,
            @AuthenticationPrincipal Usuario usuario) {
        try {
            OrdenDTO orden = ordenService.createOrden(request, usuario);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseDTO.success("Order created successfully", orden));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDTO.error(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<OrdenDTO>>> getOrdenes(@AuthenticationPrincipal Usuario usuario) {
        try {
            List<OrdenDTO> ordenes = ordenService.getOrdenesByUsuario(usuario);
            return ResponseEntity.ok(ResponseDTO.success(ordenes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDTO.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<OrdenDTO>> getOrdenById(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario) {
        try {
            OrdenDTO orden = ordenService.getOrdenById(id, usuario);
            return ResponseEntity.ok(ResponseDTO.success(orden));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDTO.error(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ResponseDTO<OrdenDTO>> updateEstado(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEstadoOrdenDTO request,
            @AuthenticationPrincipal Usuario usuario) {
        try {
            OrdenDTO orden = ordenService.updateEstado(id, request, usuario);
            return ResponseEntity.ok(ResponseDTO.success("Order status updated successfully", orden));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDTO.error(e.getMessage()));
        }
    }
}
