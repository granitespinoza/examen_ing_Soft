package com.backend.backend.Orden.domain;

import com.backend.backend.Carrito.domain.Carrito;
import com.backend.backend.Carrito.infrastructure.CarritoRepository;
import com.backend.backend.Comerciante.domain.Comerciante;
import com.backend.backend.ItemCarrito.domain.ItemCarrito;
import com.backend.backend.ItemCarrito.infrastructure.ItemCarritoRepository;
import com.backend.backend.Orden.dto.CreateOrdenDTO;
import com.backend.backend.Orden.dto.OrdenDTO;
import com.backend.backend.Orden.dto.UpdateEstadoOrdenDTO;
import com.backend.backend.Orden.infrastructure.OrdenRepository;
import com.backend.backend.OrdenItem.infrastructure.OrdenItemRepository;
import com.backend.backend.Producto.domain.CategoriaProducto;
import com.backend.backend.Producto.domain.Producto;
import com.backend.backend.Producto.infrastructure.ProductoRepository;
import com.backend.backend.common.exception.BadRequestException;
import com.backend.backend.common.exception.InsufficientStockException;
import com.backend.backend.common.exception.ResourceNotFoundException;
import com.backend.backend.users.domain.TipoUsuario;
import com.backend.backend.users.domain.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdenServiceTest {

    @Mock
    private OrdenRepository ordenRepository;

    @Mock
    private OrdenItemRepository ordenItemRepository;

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private ItemCarritoRepository itemCarritoRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private OrdenService ordenService;

    private Usuario usuario;
    private Carrito carrito;
    private Producto producto;
    private ItemCarrito itemCarrito;
    private Orden orden;
    private CreateOrdenDTO createOrdenDTO;
    private UpdateEstadoOrdenDTO updateEstadoDTO;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .nombre("Test User")
                .email("test@example.com")
                .tipoUsuario(TipoUsuario.CLIENTE)
                .build();

        Comerciante comerciante = Comerciante.builder()
                .id(1L)
                .nombreNegocio("Test Business")
                .build();

        producto = Producto.builder()
                .id(1L)
                .comerciante(comerciante)
                .nombre("Test Product")
                .precio(BigDecimal.valueOf(100))
                .stock(50)
                .categoria(CategoriaProducto.ELECTRONICA)
                .build();

        carrito = Carrito.builder()
                .id(1L)
                .cliente(usuario)
                .items(new ArrayList<>())
                .build();

        itemCarrito = ItemCarrito.builder()
                .id(1L)
                .carrito(carrito)
                .producto(producto)
                .cantidad(5)
                .precioUnitario(BigDecimal.valueOf(100))
                .build();

        carrito.getItems().add(itemCarrito);

        orden = Orden.builder()
                .id(1L)
                .cliente(usuario)
                .items(new ArrayList<>())
                .estado(EstadoOrden.PENDIENTE)
                .total(BigDecimal.valueOf(500))
                .direccionEnvio("Test Address")
                .build();

        createOrdenDTO = CreateOrdenDTO.builder()
                .direccionEnvio("Test Address")
                .build();

        updateEstadoDTO = UpdateEstadoOrdenDTO.builder()
                .estado(EstadoOrden.ENVIADO)
                .build();
    }

    @Test
    void createOrden_WithValidCart_ShouldCreateOrden() {
        when(carritoRepository.findByClienteId(usuario.getId())).thenReturn(Optional.of(carrito));
        when(ordenRepository.save(any(Orden.class))).thenReturn(orden);
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        when(itemCarritoRepository.findAll()).thenReturn(new ArrayList<>());
        when(carritoRepository.save(any(Carrito.class))).thenReturn(carrito);

        OrdenDTO result = ordenService.createOrden(createOrdenDTO, usuario);

        assertNotNull(result);
        verify(ordenRepository).save(any(Orden.class));
        verify(productoRepository).save(producto);
    }

    @Test
    void createOrden_WithEmptyCart_ShouldThrowBadRequestException() {
        carrito.getItems().clear();
        when(carritoRepository.findByClienteId(usuario.getId())).thenReturn(Optional.of(carrito));

        assertThrows(BadRequestException.class,
            () -> ordenService.createOrden(createOrdenDTO, usuario));
    }

    @Test
    void createOrden_WithNoCart_ShouldThrowBadRequestException() {
        when(carritoRepository.findByClienteId(usuario.getId())).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class,
            () -> ordenService.createOrden(createOrdenDTO, usuario));
    }

    @Test
    void createOrden_WithInsufficientStock_ShouldThrowInsufficientStockException() {
        producto.setStock(2);
        when(carritoRepository.findByClienteId(usuario.getId())).thenReturn(Optional.of(carrito));

        assertThrows(InsufficientStockException.class,
            () -> ordenService.createOrden(createOrdenDTO, usuario));
    }

    @Test
    void getOrdenesByUsuario_ShouldReturnUserOrders() {
        when(ordenRepository.findByClienteIdOrderByFechaOrdenDesc(usuario.getId()))
                .thenReturn(Arrays.asList(orden));

        List<OrdenDTO> result = ordenService.getOrdenesByUsuario(usuario);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(ordenRepository).findByClienteIdOrderByFechaOrdenDesc(usuario.getId());
    }

    @Test
    void getOrdenById_WithValidId_ShouldReturnOrden() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));

        OrdenDTO result = ordenService.getOrdenById(1L, usuario);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(ordenRepository).findById(1L);
    }

    @Test
    void getOrdenById_WithInvalidId_ShouldThrowResourceNotFoundException() {
        when(ordenRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> ordenService.getOrdenById(999L, usuario));
    }

    @Test
    void getOrdenById_WithUnauthorizedUser_ShouldThrowBadRequestException() {
        Usuario otherUser = Usuario.builder()
                .id(2L)
                .build();
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));

        assertThrows(BadRequestException.class,
            () -> ordenService.getOrdenById(1L, otherUser));
    }

    @Test
    void updateEstado_WithValidData_ShouldUpdateOrdenEstado() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
        when(ordenRepository.save(any(Orden.class))).thenReturn(orden);

        OrdenDTO result = ordenService.updateEstado(1L, updateEstadoDTO, usuario);

        assertNotNull(result);
        verify(ordenRepository).save(orden);
    }

    @Test
    void updateEstado_WithInvalidId_ShouldThrowResourceNotFoundException() {
        when(ordenRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> ordenService.updateEstado(999L, updateEstadoDTO, usuario));
    }

    @Test
    void updateEstado_WithUnauthorizedUser_ShouldThrowBadRequestException() {
        Usuario otherUser = Usuario.builder()
                .id(2L)
                .build();
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));

        assertThrows(BadRequestException.class,
            () -> ordenService.updateEstado(1L, updateEstadoDTO, otherUser));
    }
}
