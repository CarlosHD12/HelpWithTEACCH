package com.teach.helpwithteacch.Controller;

import com.teach.helpwithteacch.DTO.Common.CambiarEstadoRequest;
import com.teach.helpwithteacch.DTO.Version.*;
import com.teach.helpwithteacch.Enum.Estado;
import com.teach.helpwithteacch.Services.VersionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/versiones")
@RequiredArgsConstructor
public class VersionController {

    private final VersionService versionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VersionResponse> crear(
            @Valid @RequestBody VersionRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(versionService.crear(request));
    }

    @PutMapping("/{idVersion}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VersionResponse> editar(
            @PathVariable Long idVersion,
            @Valid @RequestBody VersionEditRequest request
    ) {
        return ResponseEntity.ok(
                versionService.editar(idVersion, request)
        );
    }

    @PatchMapping("/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cambiarEstado(
            @Valid @RequestBody CambiarEstadoRequest request
    ) {
        versionService.cambiarEstado(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{idVersion}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE', 'PADRE')")
    public ResponseEntity<VersionResponse> obtenerPorId(
            @PathVariable Long idVersion
    ) {
        return ResponseEntity.ok(
                versionService.obtenerPorId(idVersion)
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE', 'PADRE')")
    public ResponseEntity<Page<VersionResponse>> listar(
            @RequestParam(required = false) Long idPrueba,
            @RequestParam(required = false) String numeroVersion,
            @RequestParam(required = false) Estado estado,
            @RequestParam(required = false) LocalDateTime fechaDesde,
            @RequestParam(required = false) LocalDateTime fechaHasta,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                versionService.listar(
                        idPrueba,
                        numeroVersion,
                        estado,
                        fechaDesde,
                        fechaHasta,
                        pageable
                )
        );
    }
}