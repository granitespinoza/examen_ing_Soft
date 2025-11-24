package com.backend.backend.Security.Auth;

import com.backend.backend.Security.Auth.dto.AuthResponseDTO;
import com.backend.backend.Security.Auth.dto.LoginRequestDTO;
import com.backend.backend.Security.Auth.dto.RegisterRequestDTO;
import com.backend.backend.common.dto.ResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthControllerImpl {

    private final AuthServiceImpl authService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<AuthResponseDTO>> register(@Valid @RequestBody RegisterRequestDTO request) {
        try {
            AuthResponseDTO response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseDTO.success("User registered successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDTO.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<AuthResponseDTO>> login(@Valid @RequestBody LoginRequestDTO request) {
        try {
            AuthResponseDTO response = authService.login(request);
            return ResponseEntity.ok(ResponseDTO.success("Login successful", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ResponseDTO.error(e.getMessage()));
        }
    }
}
