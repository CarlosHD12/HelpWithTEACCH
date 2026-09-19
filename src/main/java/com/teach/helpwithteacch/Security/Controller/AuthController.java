package com.teach.helpwithteacch.Security.Controller;

import com.teach.helpwithteacch.Security.DTO.Auth.*;
import com.teach.helpwithteacch.Security.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(
                authService.login(request)
        );
    }

    @PostMapping("/registro")
    public ResponseEntity<LoginResponse> registrar(
            @Valid @RequestBody RegistroRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.registrar(request));
    }

    @PutMapping("/cambiar-password/{idUsuario}")
    public ResponseEntity<Void> cambiarPassword(
            @PathVariable Long idUsuario,
            @Valid @RequestBody CambiarPasswordRequest request
    ) {
        authService.cambiarPassword(idUsuario, request);
        return ResponseEntity.noContent().build();
    }
}