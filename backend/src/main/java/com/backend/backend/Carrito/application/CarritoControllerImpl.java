package com.backend.backend.Carrito.application;

import com.backend.backend.Carrito.domain.CarritoServiceImpl;
import com.backend.backend.Carrito.dto.AddItemCarritoDTO;
import com.backend.backend.Carrito.dto.CarritoDTO;
import com.backend.backend.Carrito.dto.UpdateItemCarritoDTO;
import com.backend.backend.common.dto.ResponseDTO;
import com.backend.backend.users.domain.Usuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carrito")
@RequiredArgsConstructor
public class CarritoControllerImpl {

    private final CarritoServiceImpl carritoService;

    @GetMapping
    public ResponseEntity<ResponseDTO<CarritoDTO>> getCarrito(@AuthenticationPrincipal Usuario usuario) {
        try {
            CarritoDTO carrito = carritoService.getCarrito(usuario);
            return ResponseEntity.ok(ResponseDTO.success(carrito));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDTO.error(e.getMessage()));
        }
    }

    @PostMapping("/items")
    public ResponseEntity<ResponseDTO<CarritoDTO>> addItem(
            @Valid @RequestBody AddItemCarritoDTO request,
            @AuthenticationPrincipal Usuario usuario) {
        try {
            CarritoDTO carrito = carritoService.addItem(request, usuario);
            return ResponseEntity.ok(ResponseDTO.success("Item added to cart successfully", carrito));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDTO.error(e.getMessage()));
        }
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<ResponseDTO<CarritoDTO>> updateItem(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateItemCarritoDTO request,
            @AuthenticationPrincipal Usuario usuario) {
        try {
            CarritoDTO carrito = carritoService.updateItem(itemId, request, usuario);
            return ResponseEntity.ok(ResponseDTO.success("Cart item updated successfully", carrito));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDTO.error(e.getMessage()));
        }
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ResponseDTO<CarritoDTO>> removeItem(
            @PathVariable Long itemId,
            @AuthenticationPrincipal Usuario usuario) {
        try {
            CarritoDTO carrito = carritoService.removeItem(itemId, usuario);
            return ResponseEntity.ok(ResponseDTO.success("Item removed from cart successfully", carrito));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDTO.error(e.getMessage()));
        }
    }
}
