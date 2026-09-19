package com.teach.helpwithteacch.Services;

import com.teach.helpwithteacch.DTO.Respuesta.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RespuestaService {
    List<RespuestaResponse> guardar(RespuestaRequest request);
    RespuestaResponse obtenerPorId(Long idRespuesta);
    List<RespuestaResponse> listarPorEvaluacion(Long idEvaluacion);
    Page<RespuestaResponse> listar(Pageable pageable);
}