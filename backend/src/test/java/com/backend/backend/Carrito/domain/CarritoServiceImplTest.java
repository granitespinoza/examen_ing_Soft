package com.backend.backend.Carrito.domain;

import com.backend.backend.Carrito.dto.AddItemCarritoDTO;
import com.backend.backend.Carrito.dto.CarritoDTO;
import com.backend.backend.Carrito.dto.UpdateItemCarritoDTO;
import com.backend.backend.Carrito.infrastructure.CarritoRepository;
import com.backend.backend.Comerciante.domain.Comerciante;
import com.backend.backend.ItemCarrito.domain.ItemCarrito;
import com.backend.backend.ItemCarrito.infrastructure.ItemCarritoRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarritoServiceImplTest {

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private ItemCarritoRepository itemCarritoRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private CarritoServiceImpl carritoService;

    private Usuario usuario;
    private Carrito carrito;
    private Producto producto;
    private ItemCarrito itemCarrito;
    private AddItemCarritoDTO addItemDTO;
    private UpdateItemCarritoDTO updateItemDTO;

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

        addItemDTO = AddItemCarritoDTO.builder()
                .productoId(1L)
                .cantidad(5)
                .build();

        updateItemDTO = UpdateItemCarritoDTO.builder()
                .cantidad(10)
                .build();
    }

    @Test
    void getCarrito_WithExistingCart_ShouldReturnCarrito() {
        when(carritoRepository.findByClienteId(usuario.getId())).thenReturn(Optional.of(carrito));

        CarritoDTO result = carritoService.getCarrito(usuario);

        assertNotNull(result);
        verify(carritoRepository).findByClienteId(usuario.getId());
    }

    @Test
    void getCarrito_WithNoExistingCart_ShouldCreateAndReturnNewCarrito() {
        when(carritoRepository.findByClienteId(usuario.getId())).thenReturn(Optional.empty());
        when(carritoRepository.save(any(Carrito.class))).thenReturn(carrito);

        CarritoDTO result = carritoService.getCarrito(usuario);

        assertNotNull(result);
        verify(carritoRepository).save(any(Carrito.class));
    }

    @Test
    void addItem_WithValidData_ShouldAddItemToCart() {
        when(carritoRepository.findByClienteId(usuario.getId())).thenReturn(Optional.of(carrito));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(itemCarritoRepository.findByCarritoIdAndProductoId(carrito.getId(), 1L))
                .thenReturn(Optional.empty());
        when(itemCarritoRepository.save(any(ItemCarrito.class))).thenReturn(itemCarrito);
        when(carritoRepository.save(any(Carrito.class))).thenReturn(carrito);

        CarritoDTO result = carritoService.addItem(addItemDTO, usuario);

        assertNotNull(result);
        verify(itemCarritoRepository).save(any(ItemCarrito.class));
    }

    @Test
    void addItem_WithInsufficientStock_ShouldThrowInsufficientStockException() {
        producto.setStock(2);
        when(carritoRepository.findByClienteId(usuario.getId())).thenReturn(Optional.of(carrito));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        assertThrows(InsufficientStockException.class,
            () -> carritoService.addItem(addItemDTO, usuario));
    }

    @Test
    void addItem_WithNonExistentProduct_ShouldThrowResourceNotFoundException() {
        when(carritoRepository.findByClienteId(usuario.getId())).thenReturn(Optional.of(carrito));
        when(productoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> carritoService.addItem(addItemDTO, usuario));
    }

    @Test
    void addItem_WithExistingItem_ShouldUpdateQuantity() {
        when(carritoRepository.findByClienteId(usuario.getId())).thenReturn(Optional.of(carrito));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(itemCarritoRepository.findByCarritoIdAndProductoId(carrito.getId(), 1L))
                .thenReturn(Optional.of(itemCarrito));
        when(itemCarritoRepository.save(any(ItemCarrito.class))).thenReturn(itemCarrito);
        when(carritoRepository.save(any(Carrito.class))).thenReturn(carrito);

        CarritoDTO result = carritoService.addItem(addItemDTO, usuario);

        assertNotNull(result);
        verify(itemCarritoRepository).save(itemCarrito);
        assertEquals(10, itemCarrito.getCantidad());
    }

    @Test
    void updateItem_WithValidData_ShouldUpdateItemQuantity() {
        carrito.getItems().add(itemCarrito);
        when(itemCarritoRepository.findById(1L)).thenReturn(Optional.of(itemCarrito));
        when(itemCarritoRepository.save(any(ItemCarrito.class))).thenReturn(itemCarrito);
        when(carritoRepository.findById(carrito.getId())).thenReturn(Optional.of(carrito));

        CarritoDTO result = carritoService.updateItem(1L, updateItemDTO, usuario);

        assertNotNull(result);
        verify(itemCarritoRepository).save(itemCarrito);
    }

    @Test
    void updateItem_WithInsufficientStock_ShouldThrowInsufficientStockException() {
        producto.setStock(5);
        when(itemCarritoRepository.findById(1L)).thenReturn(Optional.of(itemCarrito));

        UpdateItemCarritoDTO largeUpdateDTO = UpdateItemCarritoDTO.builder()
                .cantidad(100)
                .build();

        assertThrows(InsufficientStockException.class,
            () -> carritoService.updateItem(1L, largeUpdateDTO, usuario));
    }

    @Test
    void updateItem_WithUnauthorizedUser_ShouldThrowBadRequestException() {
        Usuario otherUser = Usuario.builder()
                .id(2L)
                .build();
        when(itemCarritoRepository.findById(1L)).thenReturn(Optional.of(itemCarrito));

        assertThrows(BadRequestException.class,
            () -> carritoService.updateItem(1L, updateItemDTO, otherUser));
    }

    @Test
    void removeItem_WithValidData_ShouldRemoveItemFromCart() {
        carrito.getItems().add(itemCarrito);
        when(itemCarritoRepository.findById(1L)).thenReturn(Optional.of(itemCarrito));
        when(carritoRepository.findById(carrito.getId())).thenReturn(Optional.of(carrito));

        CarritoDTO result = carritoService.removeItem(1L, usuario);

        assertNotNull(result);
        verify(itemCarritoRepository).delete(itemCarrito);
    }

    @Test
    void removeItem_WithInvalidItemId_ShouldThrowResourceNotFoundException() {
        when(itemCarritoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> carritoService.removeItem(999L, usuario));
    }

    @Test
    void removeItem_WithUnauthorizedUser_ShouldThrowBadRequestException() {
        Usuario otherUser = Usuario.builder()
                .id(2L)
                .build();
        when(itemCarritoRepository.findById(1L)).thenReturn(Optional.of(itemCarrito));

        assertThrows(BadRequestException.class,
            () -> carritoService.removeItem(1L, otherUser));
    }
}
