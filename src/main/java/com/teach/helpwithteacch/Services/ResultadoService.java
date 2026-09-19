package com.teach.helpwithteacch.Services;

import com.teach.helpwithteacch.DTO.Resultado.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ResultadoService {
    ResultadoResponse generar(Long idEvaluacion);
    ResultadoResponse obtenerPorId(Long idResultado);
    ResultadoResponse obtenerPorEvaluacion(Long idEvaluacion);
    List<ResultadoSubtestResponse> listarSubtests(Long idResultado);
    Page<ResultadoResponse> listar(Pageable pageable);
}