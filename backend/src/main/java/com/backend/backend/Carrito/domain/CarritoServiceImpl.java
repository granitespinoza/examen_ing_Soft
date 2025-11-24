package com.backend.backend.Carrito.domain;

import com.backend.backend.Carrito.dto.AddItemCarritoDTO;
import com.backend.backend.Carrito.dto.CarritoDTO;
import com.backend.backend.Carrito.dto.UpdateItemCarritoDTO;
import com.backend.backend.Carrito.infrastructure.CarritoRepository;
import com.backend.backend.ItemCarrito.domain.ItemCarrito;
import com.backend.backend.ItemCarrito.infrastructure.ItemCarritoRepository;
import com.backend.backend.Producto.domain.Producto;
import com.backend.backend.Producto.infrastructure.ProductoRepository;
import com.backend.backend.common.exception.BadRequestException;
import com.backend.backend.common.exception.InsufficientStockException;
import com.backend.backend.common.exception.ResourceNotFoundException;
import com.backend.backend.common.mapper.CarritoMapper;
import com.backend.backend.users.domain.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CarritoServiceImpl {

    private final CarritoRepository carritoRepository;
    private final ItemCarritoRepository itemCarritoRepository;
    private final ProductoRepository productoRepository;

    public CarritoDTO getCarrito(Usuario usuario) {
        try {
            Carrito carrito = carritoRepository.findByClienteId(usuario.getId())
                    .orElseGet(() -> createCarritoForUser(usuario));
            return CarritoMapper.toDTO(carrito);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching cart: " + e.getMessage());
        }
    }

    @Transactional
    public CarritoDTO addItem(AddItemCarritoDTO request, Usuario usuario) {
        try {
            Carrito carrito = carritoRepository.findByClienteId(usuario.getId())
                    .orElseGet(() -> createCarritoForUser(usuario));

            Producto producto = productoRepository.findById(request.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductoId()));

            if (producto.getStock() < request.getCantidad()) {
                throw new InsufficientStockException("Insufficient stock for product: " + producto.getNombre());
            }

            ItemCarrito existingItem = itemCarritoRepository
                    .findByCarritoIdAndProductoId(carrito.getId(), request.getProductoId())
                    .orElse(null);

            if (existingItem != null) {
                int newCantidad = existingItem.getCantidad() + request.getCantidad();
                if (producto.getStock() < newCantidad) {
                    throw new InsufficientStockException("Insufficient stock for product: " + producto.getNombre());
                }
                existingItem.setCantidad(newCantidad);
                itemCarritoRepository.save(existingItem);
            } else {
                ItemCarrito newItem = ItemCarrito.builder()
                        .carrito(carrito)
                        .producto(producto)
                        .cantidad(request.getCantidad())
                        .precioUnitario(producto.getPrecio())
                        .build();
                itemCarritoRepository.save(newItem);
                carrito.getItems().add(newItem);
            }

            carrito = carritoRepository.save(carrito);
            return CarritoMapper.toDTO(carrito);
        } catch (ResourceNotFoundException | InsufficientStockException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error adding item to cart: " + e.getMessage());
        }
    }

    @Transactional
    public CarritoDTO updateItem(Long itemId, UpdateItemCarritoDTO request, Usuario usuario) {
        try {
            ItemCarrito item = itemCarritoRepository.findById(itemId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + itemId));

            Carrito carrito = item.getCarrito();
            if (!carrito.getCliente().getId().equals(usuario.getId())) {
                throw new BadRequestException("You are not authorized to update this cart item");
            }

            if (item.getProducto().getStock() < request.getCantidad()) {
                throw new InsufficientStockException("Insufficient stock for product: " + item.getProducto().getNombre());
            }

            item.setCantidad(request.getCantidad());
            itemCarritoRepository.save(item);

            carrito = carritoRepository.findById(carrito.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
            return CarritoMapper.toDTO(carrito);
        } catch (ResourceNotFoundException | BadRequestException | InsufficientStockException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error updating cart item: " + e.getMessage());
        }
    }

    @Transactional
    public CarritoDTO removeItem(Long itemId, Usuario usuario) {
        try {
            ItemCarrito item = itemCarritoRepository.findById(itemId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + itemId));

            Carrito carrito = item.getCarrito();
            if (!carrito.getCliente().getId().equals(usuario.getId())) {
                throw new BadRequestException("You are not authorized to remove this cart item");
            }

            carrito.getItems().remove(item);
            itemCarritoRepository.delete(item);

            carrito = carritoRepository.findById(carrito.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
            return CarritoMapper.toDTO(carrito);
        } catch (ResourceNotFoundException | BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error removing cart item: " + e.getMessage());
        }
    }

    private Carrito createCarritoForUser(Usuario usuario) {
        Carrito carrito = Carrito.builder()
                .cliente(usuario)
                .items(new ArrayList<>())
                .build();
        return carritoRepository.save(carrito);
    }
}
