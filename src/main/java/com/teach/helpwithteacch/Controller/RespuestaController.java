package com.teach.helpwithteacch.Controller;

import com.teach.helpwithteacch.DTO.Respuesta.*;
import com.teach.helpwithteacch.Services.RespuestaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/respuestas")
@RequiredArgsConstructor
public class RespuestaController {

    private final RespuestaService respuestaService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE', 'PADRE')")
    public ResponseEntity<List<RespuestaResponse>> guardar(
            @Valid @RequestBody RespuestaRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(respuestaService.guardar(request));
    }

    @GetMapping("/{idRespuesta}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE', 'PADRE')")
    public ResponseEntity<RespuestaResponse> obtenerPorId(
            @PathVariable Long idRespuesta
    ) {
        return ResponseEntity.ok(
                respuestaService.obtenerPorId(idRespuesta)
        );
    }

    @GetMapping("/evaluacion/{idEvaluacion}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE', 'PADRE')")
    public ResponseEntity<List<RespuestaResponse>> listarPorEvaluacion(
            @PathVariable Long idEvaluacion
    ) {
        return ResponseEntity.ok(
                respuestaService.listarPorEvaluacion(idEvaluacion)
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<RespuestaResponse>> listar(
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                respuestaService.listar(pageable)
        );
    }
}