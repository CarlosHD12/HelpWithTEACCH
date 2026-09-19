package com.teach.helpwithteacch.Controller;

import com.teach.helpwithteacch.DTO.Asignarnino.*;
import com.teach.helpwithteacch.DTO.Common.CambiarEstadoRequest;
import com.teach.helpwithteacch.Enum.Estado;
import com.teach.helpwithteacch.Services.AsignarNinoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/asignaciones")
@RequiredArgsConstructor
public class AsignarNinoController {

    private final AsignarNinoService asignarNinoService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> asignar(
            @Valid @RequestBody AsignarNinoRequest request
    ) {
        asignarNinoService.asignar(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{idAsignarNino}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AsignarNinoResponse> editar(
            @PathVariable Long idAsignarNino,
            @Valid @RequestBody AsignarNinoEditRequest request
    ) {
        return ResponseEntity.ok(
                asignarNinoService.editar(
                        idAsignarNino,
                        request
                )
        );
    }

    @PatchMapping("/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cambiarEstado(
            @Valid @RequestBody CambiarEstadoRequest request
    ) {
        asignarNinoService.cambiarEstado(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{idAsignarNino}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AsignarNinoResponse> obtenerPorId(
            @PathVariable Long idAsignarNino
    ) {
        return ResponseEntity.ok(
                asignarNinoService.obtenerPorId(idAsignarNino)
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AsignarNinoResponse>> listar(
            @RequestParam(required = false) Long idUsuario,
            @RequestParam(required = false) Long idNino,
            @RequestParam(required = false) Estado estado,
            @RequestParam(required = false) LocalDateTime fechaDesde,
            @RequestParam(required = false) LocalDateTime fechaHasta,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                asignarNinoService.listar(
                        idUsuario,
                        idNino,
                        estado,
                        fechaDesde,
                        fechaHasta,
                        pageable
                )
        );
    }
}