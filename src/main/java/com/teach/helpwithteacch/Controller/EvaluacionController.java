package com.teach.helpwithteacch.Controller;

import com.teach.helpwithteacch.DTO.Evaluacion.*;
import com.teach.helpwithteacch.DTO.EvaluacionConfig.EvaluacionConfigResponse;
import com.teach.helpwithteacch.Enum.EstadoEvaluacion;
import com.teach.helpwithteacch.Services.EvaluacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/evaluaciones")
@RequiredArgsConstructor
public class EvaluacionController {

    private final EvaluacionService evaluacionService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE', 'PADRE')")
    public ResponseEntity<EvaluacionResponse> crear(
            @Valid @RequestBody EvaluacionRequest request
    ) {
        return ResponseEntity.ok(
                evaluacionService.crear(request)
        );
    }

    @PutMapping("/{idEvaluacion}/progreso")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE', 'PADRE')")
    public ResponseEntity<EvaluacionResponse> actualizarProgreso(
            @PathVariable Long idEvaluacion,
            @Valid @RequestBody EvaluacionEditRequest request
    ) {
        return ResponseEntity.ok(
                evaluacionService.actualizarProgreso(
                        idEvaluacion,
                        request
                )
        );
    }

    @PatchMapping("/{idEvaluacion}/pausar")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE', 'PADRE')")
    public ResponseEntity<EvaluacionResponse> pausar(
            @PathVariable Long idEvaluacion
    ) {
        return ResponseEntity.ok(
                evaluacionService.pausar(idEvaluacion)
        );
    }

    @PatchMapping("/{idEvaluacion}/reanudar")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE', 'PADRE')")
    public ResponseEntity<EvaluacionResponse> reanudar(
            @PathVariable Long idEvaluacion
    ) {
        return ResponseEntity.ok(
                evaluacionService.reanudar(idEvaluacion)
        );
    }

    @PatchMapping("/{idEvaluacion}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE', 'PADRE')")
    public ResponseEntity<EvaluacionResponse> cancelar(
            @PathVariable Long idEvaluacion
    ) {
        return ResponseEntity.ok(
                evaluacionService.cancelar(idEvaluacion)
        );
    }

    @GetMapping("/{idEvaluacion}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE', 'PADRE')")
    public ResponseEntity<EvaluacionResponse> obtenerPorId(
            @PathVariable Long idEvaluacion
    ) {
        return ResponseEntity.ok(
                evaluacionService.obtenerPorId(idEvaluacion)
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE', 'PADRE')")
    public ResponseEntity<Page<EvaluacionResponse>> listar(
            @RequestParam(required = false) Long idNino,
            @RequestParam(required = false) Long idUsuario,
            @RequestParam(required = false) Long idPrueba,
            @RequestParam(required = false) EstadoEvaluacion estado,
            @RequestParam(required = false) LocalDateTime fechaDesde,
            @RequestParam(required = false) LocalDateTime fechaHasta,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                evaluacionService.listar(
                        idNino,
                        idUsuario,
                        idPrueba,
                        estado,
                        fechaDesde,
                        fechaHasta,
                        pageable
                )
        );
    }

    @GetMapping("/{idEvaluacion}/configuracion")
    public ResponseEntity<EvaluacionConfigResponse> obtenerConfiguracion(
            @PathVariable Long idEvaluacion
    ) {
        return ResponseEntity.ok(
                evaluacionService.obtenerConfiguracion(idEvaluacion)
        );
    }
}