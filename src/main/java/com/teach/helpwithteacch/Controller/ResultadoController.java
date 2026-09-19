package com.teach.helpwithteacch.Controller;

import com.teach.helpwithteacch.DTO.Resultado.*;
import com.teach.helpwithteacch.Services.ResultadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resultados")
@RequiredArgsConstructor
public class ResultadoController {

    private final ResultadoService resultadoService;

    @PostMapping("/evaluacion/{idEvaluacion}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE', 'PADRE')")
    public ResponseEntity<ResultadoResponse> generar(
            @PathVariable Long idEvaluacion
    ) {
        return ResponseEntity.ok(
                resultadoService.generar(idEvaluacion)
        );
    }

    @GetMapping("/{idResultado}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE', 'PADRE')")
    public ResponseEntity<ResultadoResponse> obtenerPorId(
            @PathVariable Long idResultado
    ) {
        return ResponseEntity.ok(
                resultadoService.obtenerPorId(idResultado)
        );
    }

    @GetMapping("/evaluacion/{idEvaluacion}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE', 'PADRE')")
    public ResponseEntity<ResultadoResponse> obtenerPorEvaluacion(
            @PathVariable Long idEvaluacion
    ) {
        return ResponseEntity.ok(
                resultadoService.obtenerPorEvaluacion(idEvaluacion)
        );
    }

    @GetMapping("/{idResultado}/subtests")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE', 'PADRE')")
    public ResponseEntity<List<ResultadoSubtestResponse>> listarSubtests(
            @PathVariable Long idResultado
    ) {
        return ResponseEntity.ok(
                resultadoService.listarSubtests(idResultado)
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ResultadoResponse>> listar(
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                resultadoService.listar(pageable)
        );
    }
}