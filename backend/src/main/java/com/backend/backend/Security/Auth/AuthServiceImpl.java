package com.backend.backend.Security.Auth;

import com.backend.backend.Comerciante.domain.Comerciante;
import com.backend.backend.Comerciante.infrastructure.ComercianteRepository;
import com.backend.backend.Security.Auth.dto.AuthResponseDTO;
import com.backend.backend.Security.Auth.dto.LoginRequestDTO;
import com.backend.backend.Security.Auth.dto.RegisterRequestDTO;
import com.backend.backend.Security.JwtService;
import com.backend.backend.common.exception.BadRequestException;
import com.backend.backend.common.exception.DuplicateResourceException;
import com.backend.backend.common.exception.UnauthorizedException;
import com.backend.backend.common.mapper.UsuarioMapper;
import com.backend.backend.users.domain.TipoUsuario;
import com.backend.backend.users.domain.Usuario;
import com.backend.backend.users.infrastrucure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl {

    private final UserRepository userRepository;
    private final ComercianteRepository comercianteRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        try {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new DuplicateResourceException("Email already exists");
            }

            if (request.getTipoUsuario() == TipoUsuario.COMERCIANTE) {
                if (request.getCif() == null || request.getCif().isEmpty()) {
                    throw new BadRequestException("CIF is required for merchants");
                }
                if (comercianteRepository.existsByCif(request.getCif())) {
                    throw new DuplicateResourceException("CIF already exists");
                }
            }

            Usuario usuario = Usuario.builder()
                    .nombre(request.getNombre())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .tipoUsuario(request.getTipoUsuario())
                    .build();

            usuario = userRepository.save(usuario);

            if (request.getTipoUsuario() == TipoUsuario.COMERCIANTE) {
                Comerciante comerciante = Comerciante.builder()
                        .usuario(usuario)
                        .cif(request.getCif())
                        .nombreNegocio(request.getNombreNegocio())
                        .direccion(request.getDireccion())
                        .descripcion(request.getDescripcion())
                        .phoneNumber(request.getPhoneNumber())
                        .activo(true)
                        .build();
                comercianteRepository.save(comerciante);
            }

            String token = jwtService.generateToken(usuario);

            return AuthResponseDTO.builder()
                    .token(token)
                    .usuario(UsuarioMapper.toDTO(usuario))
                    .build();
        } catch (DuplicateResourceException | BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error during registration: " + e.getMessage());
        }
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        try {
            Usuario usuario = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

            if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
                throw new UnauthorizedException("Invalid credentials");
            }

            String token = jwtService.generateToken(usuario);

            return AuthResponseDTO.builder()
                    .token(token)
                    .usuario(UsuarioMapper.toDTO(usuario))
                    .build();
        } catch (UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error during login: " + e.getMessage());
        }
    }
}
