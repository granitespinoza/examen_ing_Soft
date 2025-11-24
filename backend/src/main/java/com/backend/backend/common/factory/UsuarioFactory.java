package com.backend.backend.common.factory;

import com.backend.backend.Security.Auth.dto.RegisterRequestDTO;
import com.backend.backend.users.domain.TipoUsuario;
import com.backend.backend.users.domain.Usuario;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UsuarioFactory {

    private final PasswordEncoder passwordEncoder;

    public UsuarioFactory(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario createUsuario(RegisterRequestDTO request) {
        return Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .tipoUsuario(request.getTipoUsuario())
                .build();
    }

    public Usuario createCliente(String nombre, String email, String password) {
        return Usuario.builder()
                .nombre(nombre)
                .email(email)
                .password(passwordEncoder.encode(password))
                .tipoUsuario(TipoUsuario.CLIENTE)
                .build();
    }

    public Usuario createComerciante(String nombre, String email, String password) {
        return Usuario.builder()
                .nombre(nombre)
                .email(email)
                .password(passwordEncoder.encode(password))
                .tipoUsuario(TipoUsuario.COMERCIANTE)
                .build();
    }
}
