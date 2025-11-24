package com.backend.backend.Producto.domain;

import com.backend.backend.Comerciante.domain.Comerciante;
import com.backend.backend.Comerciante.infrastructure.ComercianteRepository;
import com.backend.backend.Producto.dto.CreateProductoDTO;
import com.backend.backend.Producto.dto.ProductoDTO;
import com.backend.backend.Producto.dto.UpdateProductoDTO;
import com.backend.backend.Producto.infrastructure.ProductoRepository;
import com.backend.backend.common.exception.ResourceNotFoundException;
import com.backend.backend.common.exception.UnauthorizedException;
import com.backend.backend.common.mapper.ProductoMapper;
import com.backend.backend.users.domain.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final ComercianteRepository comercianteRepository;

    public List<ProductoDTO> getAllProductos() {
        try {
            return productoRepository.findAll().stream()
                    .map(ProductoMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error fetching products: " + e.getMessage());
        }
    }

    public List<ProductoDTO> getProductosActivos() {
        try {
            return productoRepository.findByStockGreaterThan(0).stream()
                    .map(ProductoMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error fetching active products: " + e.getMessage());
        }
    }

    public ProductoDTO getProductoById(Long id) {
        try {
            Producto producto = productoRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
            return ProductoMapper.toDTO(producto);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching product: " + e.getMessage());
        }
    }

    @Transactional
    public ProductoDTO createProducto(CreateProductoDTO request, Usuario usuario) {
        try {
            Comerciante comerciante = comercianteRepository.findByUsuarioId(usuario.getId())
                    .orElseThrow(() -> new UnauthorizedException("User is not a merchant"));

            Producto producto = Producto.builder()
                    .comerciante(comerciante)
                    .nombre(request.getNombre())
                    .descripcion(request.getDescripcion())
                    .categoria(request.getCategoria())
                    .precio(request.getPrecio())
                    .stock(request.getStock())
                    .build();

            producto = productoRepository.save(producto);
            return ProductoMapper.toDTO(producto);
        } catch (UnauthorizedException | ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error creating product: " + e.getMessage());
        }
    }

    @Transactional
    public ProductoDTO updateProducto(Long id, UpdateProductoDTO request, Usuario usuario) {
        try {
            Producto producto = productoRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

            Comerciante comerciante = comercianteRepository.findByUsuarioId(usuario.getId())
                    .orElseThrow(() -> new UnauthorizedException("User is not a merchant"));

            if (!producto.getComerciante().getId().equals(comerciante.getId())) {
                throw new UnauthorizedException("You are not authorized to update this product");
            }

            if (request.getNombre() != null) {
                producto.setNombre(request.getNombre());
            }
            if (request.getDescripcion() != null) {
                producto.setDescripcion(request.getDescripcion());
            }
            if (request.getCategoria() != null) {
                producto.setCategoria(request.getCategoria());
            }
            if (request.getPrecio() != null) {
                producto.setPrecio(request.getPrecio());
            }
            if (request.getStock() != null) {
                producto.setStock(request.getStock());
            }

            producto = productoRepository.save(producto);
            return ProductoMapper.toDTO(producto);
        } catch (ResourceNotFoundException | UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error updating product: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteProducto(Long id, Usuario usuario) {
        try {
            Producto producto = productoRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

            Comerciante comerciante = comercianteRepository.findByUsuarioId(usuario.getId())
                    .orElseThrow(() -> new UnauthorizedException("User is not a merchant"));

            if (!producto.getComerciante().getId().equals(comerciante.getId())) {
                throw new UnauthorizedException("You are not authorized to delete this product");
            }

            productoRepository.delete(producto);
        } catch (ResourceNotFoundException | UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error deleting product: " + e.getMessage());
        }
    }
}
