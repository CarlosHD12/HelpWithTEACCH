package com.teach.helpwithteacch.Controller;

import com.teach.helpwithteacch.DTO.Common.CambiarEstadoRequest;
import com.teach.helpwithteacch.DTO.Prueba.*;
import com.teach.helpwithteacch.Enum.Estado;
import com.teach.helpwithteacch.Enum.TipoPrueba;
import com.teach.helpwithteacch.Services.PruebaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pruebas")
@RequiredArgsConstructor
public class PruebaController {

    private final PruebaService pruebaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PruebaResponse> crear(
            @Valid @RequestBody PruebaRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pruebaService.crear(request));
    }

    @PutMapping("/{idPrueba}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PruebaResponse> editar(
            @PathVariable Long idPrueba,
            @Valid @RequestBody PruebaEditRequest request
    ) {
        return ResponseEntity.ok(
                pruebaService.editar(idPrueba, request)
        );
    }

    @PatchMapping("/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cambiarEstado(
            @Valid @RequestBody CambiarEstadoRequest request
    ) {
        pruebaService.cambiarEstado(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{idPrueba}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE', 'PADRE')")
    public ResponseEntity<PruebaResponse> obtenerPorId(
            @PathVariable Long idPrueba
    ) {
        return ResponseEntity.ok(
                pruebaService.obtenerPorId(idPrueba)
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE', 'PADRE')")
    public ResponseEntity<Page<PruebaResponse>> listar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) TipoPrueba tipo,
            @RequestParam(required = false) Estado estado,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                pruebaService.listar(
                        nombre,
                        tipo,
                        estado,
                        pageable
                )
        );
    }
}