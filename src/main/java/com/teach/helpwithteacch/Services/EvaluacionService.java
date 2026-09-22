package com.teach.helpwithteacch.Services;

import com.teach.helpwithteacch.DTO.Evaluacion.*;
import com.teach.helpwithteacch.DTO.EvaluacionConfig.EvaluacionConfigResponse;
import com.teach.helpwithteacch.Enum.EstadoEvaluacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface EvaluacionService {
    EvaluacionResponse crear(EvaluacionRequest request);
    EvaluacionResponse actualizarProgreso(Long idEvaluacion, EvaluacionEditRequest request);
    EvaluacionResponse obtenerPorId(Long idEvaluacion);
    Page<EvaluacionResponse> listar(
            Long idNino,
            Long idUsuario,
            Long idPrueba,
            EstadoEvaluacion estado,
            LocalDateTime fechaDesde,
            LocalDateTime fechaHasta,
            Pageable pageable
    );
    EvaluacionConfigResponse obtenerConfiguracion(Long idEvaluacion);
    EvaluacionResponse pausar(Long idEvaluacion);
    EvaluacionResponse reanudar(Long idEvaluacion);
    EvaluacionResponse cancelar(Long idEvaluacion);
}