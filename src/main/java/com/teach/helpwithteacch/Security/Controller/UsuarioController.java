package com.teach.helpwithteacch.Security.Controller;
import com.teach.helpwithteacch.DTO.Common.CambiarEstadoRequest;
import com.teach.helpwithteacch.Enum.Estado;
import com.teach.helpwithteacch.Enum.RolNombre;
import com.teach.helpwithteacch.Security.DTO.Usuario.*;
import com.teach.helpwithteacch.Security.Service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(
            @Valid @RequestBody UsuarioRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.crear(request));
    }

    @PutMapping("/{idUsuario}")
    public ResponseEntity<UsuarioResponse> editar(
            @PathVariable Long idUsuario,
            @Valid @RequestBody UsuarioEditRequest request
    ) {
        return ResponseEntity.ok(
                usuarioService.editar(idUsuario, request)
        );
    }

    @PatchMapping("/{idUsuario}/rol")
    public ResponseEntity<UsuarioResponse> cambiarRol(
            @PathVariable Long idUsuario,
            @Valid @RequestBody CambiarRolRequest request
    ) {
        return ResponseEntity.ok(
                usuarioService.cambiarRol(idUsuario, request)
        );
    }

    @PatchMapping("/estado")
    public ResponseEntity<Void> cambiarEstado(
            @Valid @RequestBody CambiarEstadoRequest request
    ) {
        usuarioService.cambiarEstado(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity<UsuarioResponse> obtenerPorId(
            @PathVariable Long idUsuario
    ) {
        return ResponseEntity.ok(
                usuarioService.obtenerPorId(idUsuario)
        );
    }

    @GetMapping
    public ResponseEntity<Page<UsuarioResponse>> listar(
            @RequestParam(required = false) String nombres,
            @RequestParam(required = false) String apellidos,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) RolNombre rol,
            @RequestParam(required = false) Estado estado,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                usuarioService.listar(
                        nombres,
                        apellidos,
                        email,
                        rol,
                        estado,
                        pageable
                )
        );
    }
}