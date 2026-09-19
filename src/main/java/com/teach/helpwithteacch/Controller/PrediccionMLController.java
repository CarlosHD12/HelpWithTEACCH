package com.teach.helpwithteacch.Controller;

import com.teach.helpwithteacch.DTO.PrediccionML.PrediccionMLResponse;
import com.teach.helpwithteacch.Enum.ModeloML;
import com.teach.helpwithteacch.Services.PrediccionMLService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/predicciones-ml")
@RequiredArgsConstructor
public class PrediccionMLController {

    private final PrediccionMLService prediccionMLService;

    @PostMapping("/evaluacion/{idEvaluacion}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE', 'PADRE')")
    public ResponseEntity<List<PrediccionMLResponse>> generar(
            @PathVariable Long idEvaluacion
    ) {
        return ResponseEntity.ok(
                prediccionMLService.generar(idEvaluacion)
        );
    }

    @GetMapping("/{idPrediccion}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE', 'PADRE')")
    public ResponseEntity<PrediccionMLResponse> obtenerPorId(
            @PathVariable Long idPrediccion
    ) {
        return ResponseEntity.ok(
                prediccionMLService.obtenerPorId(idPrediccion)
        );
    }

    @GetMapping("/evaluacion/{idEvaluacion}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE', 'PADRE')")
    public ResponseEntity<List<PrediccionMLResponse>> listarPorEvaluacion(
            @PathVariable Long idEvaluacion
    ) {
        return ResponseEntity.ok(
                prediccionMLService.listarPorEvaluacion(idEvaluacion)
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PrediccionMLResponse>> listar(
            @RequestParam(required = false) Long idEvaluacion,
            @RequestParam(required = false) ModeloML modelo,
            @RequestParam(required = false) String resultado,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaDesde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaHasta,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                prediccionMLService.listar(
                        idEvaluacion,
                        modelo,
                        resultado,
                        fechaDesde,
                        fechaHasta,
                        pageable
                )
        );
    }
}