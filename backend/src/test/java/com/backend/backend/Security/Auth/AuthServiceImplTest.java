package com.backend.backend.Security.Auth;

import com.backend.backend.Comerciante.domain.Comerciante;
import com.backend.backend.Comerciante.infrastructure.ComercianteRepository;
import com.backend.backend.Security.Auth.dto.AuthResponseDTO;
import com.backend.backend.Security.Auth.dto.LoginRequestDTO;
import com.backend.backend.Security.Auth.dto.RegisterRequestDTO;
import com.backend.backend.Security.JwtService;
import com.backend.backend.common.exception.DuplicateResourceException;
import com.backend.backend.common.exception.UnauthorizedException;
import com.backend.backend.users.domain.TipoUsuario;
import com.backend.backend.users.domain.Usuario;
import com.backend.backend.users.infrastrucure.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ComercianteRepository comercianteRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequestDTO registerRequest;
    private LoginRequestDTO loginRequest;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequestDTO.builder()
                .nombre("Test User")
                .email("test@example.com")
                .password("password123")
                .tipoUsuario(TipoUsuario.CLIENTE)
                .build();

        loginRequest = LoginRequestDTO.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        usuario = Usuario.builder()
                .id(1L)
                .nombre("Test User")
                .email("test@example.com")
                .password("encodedPassword")
                .tipoUsuario(TipoUsuario.CLIENTE)
                .build();
    }

    @Test
    void register_WithValidClienteData_ShouldReturnAuthResponse() {
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(jwtService.generateToken(any(Usuario.class))).thenReturn("jwt-token");

        AuthResponseDTO response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertNotNull(response.getUsuario());
        assertEquals("Test User", response.getUsuario().getNombre());
        verify(userRepository).save(any(Usuario.class));
        verify(comercianteRepository, never()).save(any(Comerciante.class));
    }

    @Test
    void register_WithValidComercianteData_ShouldReturnAuthResponse() {
        RegisterRequestDTO comercianteRequest = RegisterRequestDTO.builder()
                .nombre("Merchant")
                .email("merchant@example.com")
                .password("password123")
                .tipoUsuario(TipoUsuario.COMERCIANTE)
                .cif("CIF123456")
                .nombreNegocio("Test Business")
                .build();

        when(userRepository.findByEmail(comercianteRequest.getEmail())).thenReturn(Optional.empty());
        when(comercianteRepository.existsByCif(comercianteRequest.getCif())).thenReturn(false);
        when(passwordEncoder.encode(comercianteRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(comercianteRepository.save(any(Comerciante.class))).thenReturn(new Comerciante());
        when(jwtService.generateToken(any(Usuario.class))).thenReturn("jwt-token");

        AuthResponseDTO response = authService.register(comercianteRequest);

        assertNotNull(response);
        verify(userRepository).save(any(Usuario.class));
        verify(comercianteRepository).save(any(Comerciante.class));
    }

    @Test
    void register_WithDuplicateEmail_ShouldThrowDuplicateResourceException() {
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.of(usuario));

        assertThrows(DuplicateResourceException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(Usuario.class));
    }

    @Test
    void register_WithDuplicateCIF_ShouldThrowDuplicateResourceException() {
        RegisterRequestDTO comercianteRequest = RegisterRequestDTO.builder()
                .nombre("Merchant")
                .email("merchant@example.com")
                .password("password123")
                .tipoUsuario(TipoUsuario.COMERCIANTE)
                .cif("CIF123456")
                .build();

        when(userRepository.findByEmail(comercianteRequest.getEmail())).thenReturn(Optional.empty());
        when(comercianteRepository.existsByCif(comercianteRequest.getCif())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(comercianteRequest));
        verify(userRepository, never()).save(any(Usuario.class));
    }

    @Test
    void login_WithValidCredentials_ShouldReturnAuthResponse() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword())).thenReturn(true);
        when(jwtService.generateToken(usuario)).thenReturn("jwt-token");

        AuthResponseDTO response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertNotNull(response.getUsuario());
    }

    @Test
    void login_WithInvalidEmail_ShouldThrowUnauthorizedException() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> authService.login(loginRequest));
    }

    @Test
    void login_WithInvalidPassword_ShouldThrowUnauthorizedException() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword())).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> authService.login(loginRequest));
    }
}
