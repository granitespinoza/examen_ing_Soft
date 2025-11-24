package com.backend.backend.Producto.domain;

import com.backend.backend.Comerciante.domain.Comerciante;
import com.backend.backend.Comerciante.infrastructure.ComercianteRepository;
import com.backend.backend.Producto.dto.CreateProductoDTO;
import com.backend.backend.Producto.dto.ProductoDTO;
import com.backend.backend.Producto.dto.UpdateProductoDTO;
import com.backend.backend.Producto.infrastructure.ProductoRepository;
import com.backend.backend.common.exception.ResourceNotFoundException;
import com.backend.backend.common.exception.UnauthorizedException;
import com.backend.backend.users.domain.TipoUsuario;
import com.backend.backend.users.domain.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ComercianteRepository comercianteRepository;

    @InjectMocks
    private ProductoService productoService;

    private Usuario usuario;
    private Comerciante comerciante;
    private Producto producto;
    private CreateProductoDTO createProductoDTO;
    private UpdateProductoDTO updateProductoDTO;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .nombre("Test User")
                .email("test@example.com")
                .tipoUsuario(TipoUsuario.COMERCIANTE)
                .build();

        comerciante = Comerciante.builder()
                .id(1L)
                .usuario(usuario)
                .cif("CIF123456")
                .nombreNegocio("Test Business")
                .build();

        producto = Producto.builder()
                .id(1L)
                .comerciante(comerciante)
                .nombre("Test Product")
                .descripcion("Test Description")
                .categoria(CategoriaProducto.ELECTRONICA)
                .precio(BigDecimal.valueOf(100))
                .stock(10)
                .build();

        createProductoDTO = CreateProductoDTO.builder()
                .nombre("New Product")
                .descripcion("New Description")
                .categoria(CategoriaProducto.ELECTRONICA)
                .precio(BigDecimal.valueOf(150))
                .stock(20)
                .build();

        updateProductoDTO = UpdateProductoDTO.builder()
                .nombre("Updated Product")
                .precio(BigDecimal.valueOf(200))
                .build();
    }

    @Test
    void getAllProductos_ShouldReturnAllProducts() {
        when(productoRepository.findAll()).thenReturn(Arrays.asList(producto));

        List<ProductoDTO> result = productoService.getAllProductos();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productoRepository).findAll();
    }

    @Test
    void getProductosActivos_ShouldReturnOnlyProductsWithStock() {
        when(productoRepository.findByStockGreaterThan(0)).thenReturn(Arrays.asList(producto));

        List<ProductoDTO> result = productoService.getProductosActivos();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productoRepository).findByStockGreaterThan(0);
    }

    @Test
    void getProductoById_WithValidId_ShouldReturnProducto() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        ProductoDTO result = productoService.getProductoById(1L);

        assertNotNull(result);
        assertEquals("Test Product", result.getNombre());
        verify(productoRepository).findById(1L);
    }

    @Test
    void getProductoById_WithInvalidId_ShouldThrowResourceNotFoundException() {
        when(productoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productoService.getProductoById(999L));
    }

    @Test
    void createProducto_WithValidData_ShouldReturnCreatedProducto() {
        when(comercianteRepository.findByUsuarioId(usuario.getId())).thenReturn(Optional.of(comerciante));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        ProductoDTO result = productoService.createProducto(createProductoDTO, usuario);

        assertNotNull(result);
        verify(comercianteRepository).findByUsuarioId(usuario.getId());
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void createProducto_WithNonMerchantUser_ShouldThrowUnauthorizedException() {
        when(comercianteRepository.findByUsuarioId(usuario.getId())).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class,
            () -> productoService.createProducto(createProductoDTO, usuario));
    }

    @Test
    void updateProducto_WithValidData_ShouldReturnUpdatedProducto() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(comercianteRepository.findByUsuarioId(usuario.getId())).thenReturn(Optional.of(comerciante));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        ProductoDTO result = productoService.updateProducto(1L, updateProductoDTO, usuario);

        assertNotNull(result);
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void updateProducto_WithUnauthorizedUser_ShouldThrowUnauthorizedException() {
        Comerciante otherComerciante = Comerciante.builder()
                .id(2L)
                .build();

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(comercianteRepository.findByUsuarioId(usuario.getId())).thenReturn(Optional.of(otherComerciante));

        assertThrows(UnauthorizedException.class,
            () -> productoService.updateProducto(1L, updateProductoDTO, usuario));
    }

    @Test
    void deleteProducto_WithValidData_ShouldDeleteProducto() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(comercianteRepository.findByUsuarioId(usuario.getId())).thenReturn(Optional.of(comerciante));

        productoService.deleteProducto(1L, usuario);

        verify(productoRepository).delete(producto);
    }

    @Test
    void deleteProducto_WithInvalidId_ShouldThrowResourceNotFoundException() {
        when(productoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> productoService.deleteProducto(999L, usuario));
    }
}
