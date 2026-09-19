package com.teach.helpwithteacch.Controller;

import com.teach.helpwithteacch.DTO.Common.CambiarEstadoRequest;
import com.teach.helpwithteacch.DTO.Nino.*;
import com.teach.helpwithteacch.Enum.Estado;
import com.teach.helpwithteacch.Services.NinoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ninos")
@RequiredArgsConstructor
public class NinoController {

    private final NinoService ninoService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NinoResponse> crear(
            @Valid @RequestBody NinoRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ninoService.crear(request));
    }

    @PutMapping("/{idNino}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NinoResponse> editar(
            @PathVariable Long idNino,
            @Valid @RequestBody NinoEditRequest request
    ) {
        return ResponseEntity.ok(
                ninoService.editar(idNino, request)
        );
    }

    @PatchMapping("/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cambiarEstado(
            @Valid @RequestBody CambiarEstadoRequest request
    ) {
        ninoService.cambiarEstado(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{idNino}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE')")
    public ResponseEntity<NinoResponse> obtenerPorId(
            @PathVariable Long idNino
    ) {
        return ResponseEntity.ok(
                ninoService.obtenerPorId(idNino)
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE')")
    public ResponseEntity<Page<NinoResponse>> listar(
            @RequestParam(required = false) String nombres,
            @RequestParam(required = false) String apellidos,
            @RequestParam(required = false) String sexo,
            @RequestParam(required = false) Estado estado,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                ninoService.listar(
                        nombres,
                        apellidos,
                        sexo,
                        estado,
                        pageable
                )
        );
    }
}