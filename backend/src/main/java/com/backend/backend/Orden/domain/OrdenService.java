package com.backend.backend.Orden.domain;

import com.backend.backend.Carrito.domain.Carrito;
import com.backend.backend.Carrito.infrastructure.CarritoRepository;
import com.backend.backend.ItemCarrito.domain.ItemCarrito;
import com.backend.backend.ItemCarrito.infrastructure.ItemCarritoRepository;
import com.backend.backend.Orden.dto.CreateOrdenDTO;
import com.backend.backend.Orden.dto.OrdenDTO;
import com.backend.backend.Orden.dto.UpdateEstadoOrdenDTO;
import com.backend.backend.Orden.infrastructure.OrdenRepository;
import com.backend.backend.OrdenItem.domain.OrdenItem;
import com.backend.backend.OrdenItem.infrastructure.OrdenItemRepository;
import com.backend.backend.Producto.domain.Producto;
import com.backend.backend.Producto.infrastructure.ProductoRepository;
import com.backend.backend.common.exception.BadRequestException;
import com.backend.backend.common.exception.InsufficientStockException;
import com.backend.backend.common.exception.ResourceNotFoundException;
import com.backend.backend.common.mapper.OrdenMapper;
import com.backend.backend.users.domain.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrdenService {

    private final OrdenRepository ordenRepository;
    private final OrdenItemRepository ordenItemRepository;
    private final CarritoRepository carritoRepository;
    private final ItemCarritoRepository itemCarritoRepository;
    private final ProductoRepository productoRepository;

    @Transactional
    public OrdenDTO createOrden(CreateOrdenDTO request, Usuario usuario) {
        try {
            Carrito carrito = carritoRepository.findByClienteId(usuario.getId())
                    .orElseThrow(() -> new BadRequestException("Cart not found"));

            if (carrito.getItems().isEmpty()) {
                throw new BadRequestException("Cart is empty");
            }

            for (ItemCarrito item : carrito.getItems()) {
                if (item.getProducto().getStock() < item.getCantidad()) {
                    throw new InsufficientStockException("Insufficient stock for product: " + item.getProducto().getNombre());
                }
            }

            Orden orden = Orden.builder()
                    .cliente(usuario)
                    .items(new ArrayList<>())
                    .estado(EstadoOrden.PENDIENTE)
                    .direccionEnvio(request.getDireccionEnvio())
                    .build();

            BigDecimal total = BigDecimal.ZERO;
            for (ItemCarrito item : carrito.getItems()) {
                OrdenItem ordenItem = OrdenItem.builder()
                        .orden(orden)
                        .producto(item.getProducto())
                        .cantidad(item.getCantidad())
                        .precioUnitario(item.getPrecioUnitario())
                        .build();
                orden.getItems().add(ordenItem);
                total = total.add(ordenItem.getPrecioUnitario().multiply(BigDecimal.valueOf(ordenItem.getCantidad())));

                Producto producto = item.getProducto();
                producto.setStock(producto.getStock() - item.getCantidad());
                productoRepository.save(producto);
            }

            orden.setTotal(total);
            orden = ordenRepository.save(orden);

            carrito.getItems().clear();
            itemCarritoRepository.deleteAll(itemCarritoRepository.findAll().stream()
                    .filter(item -> item.getCarrito().getId().equals(carrito.getId()))
                    .collect(Collectors.toList()));
            carritoRepository.save(carrito);

            return OrdenMapper.toDTO(orden);
        } catch (BadRequestException | InsufficientStockException | ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error creating order: " + e.getMessage());
        }
    }

    public List<OrdenDTO> getOrdenesByUsuario(Usuario usuario) {
        try {
            return ordenRepository.findByClienteIdOrderByFechaOrdenDesc(usuario.getId()).stream()
                    .map(OrdenMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error fetching orders: " + e.getMessage());
        }
    }

    public OrdenDTO getOrdenById(Long id, Usuario usuario) {
        try {
            Orden orden = ordenRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

            if (!orden.getCliente().getId().equals(usuario.getId())) {
                throw new BadRequestException("You are not authorized to view this order");
            }

            return OrdenMapper.toDTO(orden);
        } catch (ResourceNotFoundException | BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching order: " + e.getMessage());
        }
    }

    @Transactional
    public OrdenDTO updateEstado(Long id, UpdateEstadoOrdenDTO request, Usuario usuario) {
        try {
            Orden orden = ordenRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

            if (!orden.getCliente().getId().equals(usuario.getId())) {
                throw new BadRequestException("You are not authorized to update this order");
            }

            orden.setEstado(request.getEstado());
            orden = ordenRepository.save(orden);
            return OrdenMapper.toDTO(orden);
        } catch (ResourceNotFoundException | BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error updating order status: " + e.getMessage());
        }
    }
}
