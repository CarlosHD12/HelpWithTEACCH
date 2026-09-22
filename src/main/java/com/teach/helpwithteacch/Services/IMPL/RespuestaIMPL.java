package com.teach.helpwithteacch.Services.IMPL;

import java.time.LocalDateTime;

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

import com.teach.helpwithteacch.DTO.EvaluacionConfig.EvaluacionConfigResponse;
import com.teach.helpwithteacch.Entidades.Prueba;
import com.teach.helpwithteacch.Entidades.Version;
import com.teach.helpwithteacch.Services.EvaluacionConfigService;

@Service
@RequiredArgsConstructor
@Transactional
public class RespuestaIMPL implements RespuestaService {

    private final RespuestaRepos respuestaRepos;
    private final EvaluacionRepos evaluacionRepos;
    private final RespuestaMapper respuestaMapper;
    private final EvaluacionConfigService evaluacionConfigService;

    @Override
    public List<RespuestaResponse> guardar(RespuestaRequest request) {

        Evaluacion evaluacion = buscarEvaluacion(
                request.getIdEvaluacion()
        );

        validarEvaluacionActiva(evaluacion);

        List<RespuestaResponse> respuestas =
                request.getItems()
                        .stream()
                        .map(item -> guardarRespuesta(
                                evaluacion,
                                item
                        ))
                        .toList();

        verificarEvaluacionCompletada(evaluacion);

        return respuestas;
    }

    @Override
    @Transactional(readOnly = true)
    public RespuestaResponse obtenerPorId(Long idRespuesta) {

        return respuestaMapper.toResponse(
                buscarRespuesta(idRespuesta)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<RespuestaResponse> listarPorEvaluacion(
            Long idEvaluacion) {

        buscarEvaluacion(idEvaluacion);

        return respuestaRepos
                .findByEvaluacion_IdEvaluacionOrderByItemIdAscSerieIdAsc(
                        idEvaluacion
                )
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
                Sort.by(
                        Sort.Direction.DESC,
                        "idRespuesta"
                )
        );

        return respuestaRepos
                .findAll(pageableOrdenado)
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

            respuestaMapper.updateEntity(
                    request,
                    respuesta
            );
        }

        respuesta.setCorrecta(false);
        respuesta.setPuntaje(0);

        return respuestaMapper.toResponse(
                respuestaRepos.save(respuesta)
        );
    }

    private void verificarEvaluacionCompletada(
            Evaluacion evaluacion) {

        /*
         * Obtener la configuración de la versión
         * de esta evaluación.
         */
        EvaluacionConfigResponse config =
                obtenerConfiguracion(evaluacion);

        /*
         * Cantidad total de preguntas
         * definidas en el JSON.
         */
        int cantidadPreguntas =
                config.getAssessment()
                        .getItems()
                        .size();

        /*
         * Cantidad de respuestas existentes
         * para esta evaluación.
         */
        long cantidadRespuestas =
                respuestaRepos.countByEvaluacion_IdEvaluacion(
                        evaluacion.getIdEvaluacion()
                );

        /*
         * Si ya respondió todas las preguntas,
         * finalizar la evaluación.
         */
        if (cantidadRespuestas >= cantidadPreguntas) {

            evaluacion.setEstado(
                    EstadoEvaluacion.COMPLETADA
            );

            evaluacion.setProgreso(100);

            evaluacion.setFechaFinalizacion(
                    LocalDateTime.now()
            );

            evaluacion.setFechaUltimoAcceso(
                    LocalDateTime.now()
            );

            evaluacionRepos.save(evaluacion);
        }
    }

    private EvaluacionConfigResponse obtenerConfiguracion(
            Evaluacion evaluacion) {

        Version version = evaluacion.getVersion();

        if (version == null) {
            throw new IllegalStateException(
                    "La evaluación no tiene una versión asociada"
            );
        }

        Prueba prueba = version.getPrueba();

        if (prueba == null) {
            throw new IllegalStateException(
                    "La versión no tiene una prueba asociada"
            );
        }

        if (prueba.getTipo() == null) {
            throw new IllegalStateException(
                    "La prueba no tiene un tipo definido"
            );
        }

        if (version.getNumeroVersion() == null
                || version.getNumeroVersion().isBlank()) {

            throw new IllegalStateException(
                    "La versión no tiene un número de versión definido"
            );
        }

        return evaluacionConfigService.obtenerConfiguracion(
                prueba.getTipo().name(),
                version.getNumeroVersion()
        );
    }

    private Evaluacion buscarEvaluacion(
            Long idEvaluacion) {

        return evaluacionRepos.findById(idEvaluacion)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No se encontró la evaluación con ID: "
                                        + idEvaluacion
                        )
                );
    }

    private Respuesta buscarRespuesta(
            Long idRespuesta) {

        return respuestaRepos.findById(idRespuesta)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No se encontró la respuesta con ID: "
                                        + idRespuesta
                        )
                );
    }

    private void validarEvaluacionActiva(
            Evaluacion evaluacion) {

        if (evaluacion.getEstado()
                != EstadoEvaluacion.EN_PROGRESO

                && evaluacion.getEstado()
                != EstadoEvaluacion.PAUSADA) {

            throw new BadRequestException(
                    "No se pueden registrar respuestas en una evaluación con estado: "
                            + evaluacion.getEstado()
            );
        }
    }
}