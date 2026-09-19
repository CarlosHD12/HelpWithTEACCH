package com.teach.helpwithteacch.Services.IMPL;

import com.teach.helpwithteacch.DTO.Respuesta.*;
import com.teach.helpwithteacch.Entidades.Evaluacion;
import com.teach.helpwithteacch.Entidades.Respuesta;
import com.teach.helpwithteacch.Enum.EstadoEvaluacion;
import com.teach.helpwithteacch.Mapper.RespuestaMapper;
import com.teach.helpwithteacch.Repository.EvaluacionRepos;
import com.teach.helpwithteacch.Repository.RespuestaRepos;
import com.teach.helpwithteacch.Security.Exceptions.*;
import com.teach.helpwithteacch.Services.RespuestaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RespuestaIMPL implements RespuestaService {

    private final RespuestaRepos respuestaRepos;
    private final EvaluacionRepos evaluacionRepos;
    private final RespuestaMapper respuestaMapper;

    @Override
    public List<RespuestaResponse> guardar(RespuestaRequest request) {
        Evaluacion evaluacion = buscarEvaluacion(request.getIdEvaluacion());
        validarEvaluacionActiva(evaluacion);

        return request.getItems().stream()
                .map(item -> guardarRespuesta(evaluacion, item))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RespuestaResponse obtenerPorId(Long idRespuesta) {
        return respuestaMapper.toResponse(buscarRespuesta(idRespuesta));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RespuestaResponse> listarPorEvaluacion(Long idEvaluacion) {
        buscarEvaluacion(idEvaluacion);

        return respuestaRepos
                .findByEvaluacion_IdEvaluacionOrderByItemIdAscSerieIdAsc(idEvaluacion)
                .stream()
                .map(respuestaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RespuestaResponse> listar(Pageable pageable) {
        Pageable pageableOrdenado = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "idRespuesta")
        );

        return respuestaRepos.findAll(pageableOrdenado)
                .map(respuestaMapper::toResponse);
    }

    private RespuestaResponse guardarRespuesta(
            Evaluacion evaluacion,
            RespuestaItemRequest request) {

        Respuesta respuesta = respuestaRepos
                .findByEvaluacion_IdEvaluacionAndItemIdAndSerieId(
                        evaluacion.getIdEvaluacion(),
                        request.getItemId(),
                        request.getSerieId()
                )
                .orElse(null);

        if (respuesta == null) {
            respuesta = respuestaMapper.toEntity(request);
            respuesta.setEvaluacion(evaluacion);
        } else {
            respuestaMapper.updateEntity(request, respuesta);
        }

        respuesta.setCorrecta(false);
        respuesta.setPuntaje(0);

        return respuestaMapper.toResponse(respuestaRepos.save(respuesta));
    }

    private Evaluacion buscarEvaluacion(Long idEvaluacion) {
        return evaluacionRepos.findById(idEvaluacion)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la evaluación con ID: " + idEvaluacion
                ));
    }

    private Respuesta buscarRespuesta(Long idRespuesta) {
        return respuestaRepos.findById(idRespuesta)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la respuesta con ID: " + idRespuesta
                ));
    }

    private void validarEvaluacionActiva(Evaluacion evaluacion) {
        if (evaluacion.getEstado() != EstadoEvaluacion.EN_PROGRESO
                && evaluacion.getEstado() != EstadoEvaluacion.PAUSADA) {
            throw new BadRequestException(
                    "No se pueden registrar respuestas en una evaluación con estado: "
                            + evaluacion.getEstado()
            );
        }
    }
}