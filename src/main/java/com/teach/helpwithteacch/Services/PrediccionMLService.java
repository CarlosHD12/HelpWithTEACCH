package com.teach.helpwithteacch.Services;

import com.teach.helpwithteacch.DTO.PrediccionML.*;
import com.teach.helpwithteacch.Enum.ModeloML;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface PrediccionMLService {
    List<PrediccionMLResponse> generar(Long idEvaluacion);
    PrediccionMLResponse obtenerPorId(Long idPrediccion);
    List<PrediccionMLResponse> listarPorEvaluacion(Long idEvaluacion);
    Page<PrediccionMLResponse> listar(
            Long idEvaluacion,
            ModeloML modelo,
            String resultado,
            LocalDateTime fechaDesde,
            LocalDateTime fechaHasta,
            Pageable pageable
    );
}