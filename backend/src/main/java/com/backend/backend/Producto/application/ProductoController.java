package com.backend.backend.Producto.application;

import com.backend.backend.Producto.domain.ProductoService;
import com.backend.backend.Producto.dto.CreateProductoDTO;
import com.backend.backend.Producto.dto.ProductoDTO;
import com.backend.backend.Producto.dto.UpdateProductoDTO;
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
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<ResponseDTO<List<ProductoDTO>>> getAllProductos() {
        try {
            List<ProductoDTO> productos = productoService.getProductosActivos();
            return ResponseEntity.ok(ResponseDTO.success(productos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDTO.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<ProductoDTO>> getProductoById(@PathVariable Long id) {
        try {
            ProductoDTO producto = productoService.getProductoById(id);
            return ResponseEntity.ok(ResponseDTO.success(producto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDTO.error(e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<ProductoDTO>> createProducto(
            @Valid @RequestBody CreateProductoDTO request,
            @AuthenticationPrincipal Usuario usuario) {
        try {
            ProductoDTO producto = productoService.createProducto(request, usuario);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseDTO.success("Product created successfully", producto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDTO.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<ProductoDTO>> updateProducto(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductoDTO request,
            @AuthenticationPrincipal Usuario usuario) {
        try {
            ProductoDTO producto = productoService.updateProducto(id, request, usuario);
            return ResponseEntity.ok(ResponseDTO.success("Product updated successfully", producto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDTO.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<Void>> deleteProducto(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario) {
        try {
            productoService.deleteProducto(id, usuario);
            return ResponseEntity.ok(ResponseDTO.success("Product deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDTO.error(e.getMessage()));
        }
    }
}
