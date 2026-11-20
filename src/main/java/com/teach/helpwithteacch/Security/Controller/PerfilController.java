package com.teach.helpwithteacch.Security.Controller;

import com.teach.helpwithteacch.Security.DTO.Auth.CambiarPasswordRequest;
import com.teach.helpwithteacch.Security.DTO.Auth.LoginResponse;
import com.teach.helpwithteacch.Security.DTO.Perfil.PerfilRequest;
import com.teach.helpwithteacch.Security.DTO.Perfil.PerfilResponse;
import com.teach.helpwithteacch.Security.Service.PerfilService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Perfil del usuario que inició sesión. Cada operación usa el usuario
 * del token (no un ID en la URL), así nadie puede editar a otro.
 */
@RestController
@RequestMapping("/api/perfil")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE', 'PADRE')")
public class PerfilController {

    private final PerfilService perfilService;

    @GetMapping
    public ResponseEntity<PerfilResponse> obtener(Authentication authentication) {
        return ResponseEntity.ok(
                perfilService.obtenerPerfil(authentication.getName())
        );
    }

    /**
     * Devuelve un token nuevo: el anterior deja de servir si cambia el correo.
     */
    @PutMapping
    public ResponseEntity<LoginResponse> actualizar(
            Authentication authentication,
            @Valid @RequestBody PerfilRequest request
    ) {
        return ResponseEntity.ok(
                perfilService.actualizarPerfil(authentication.getName(), request)
        );
    }

    @PutMapping("/password")
    public ResponseEntity<Void> cambiarPassword(
            Authentication authentication,
            @Valid @RequestBody CambiarPasswordRequest request
    ) {
        perfilService.cambiarPassword(authentication.getName(), request);
        return ResponseEntity.noContent().build();
    }
}
