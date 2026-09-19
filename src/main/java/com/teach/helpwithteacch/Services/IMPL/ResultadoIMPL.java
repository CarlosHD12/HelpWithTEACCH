package com.teach.helpwithteacch.Services.IMPL;

import com.teach.helpwithteacch.DTO.Resultado.*;
import com.teach.helpwithteacch.Entidades.Evaluacion;
import com.teach.helpwithteacch.Entidades.Resultado;
import com.teach.helpwithteacch.Enum.EstadoEvaluacion;
import com.teach.helpwithteacch.Mapper.ResultadoMapper;
import com.teach.helpwithteacch.Mapper.ResultadoSubtestMapper;
import com.teach.helpwithteacch.Repository.EvaluacionRepos;
import com.teach.helpwithteacch.Repository.ResultadoRepos;
import com.teach.helpwithteacch.Repository.ResultadoSubtestRepos;
import com.teach.helpwithteacch.Security.Exceptions.*;
import com.teach.helpwithteacch.Services.ResultadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ResultadoIMPL implements ResultadoService {

    private final ResultadoRepos resultadoRepos;
    private final ResultadoSubtestRepos resultadoSubtestRepos;
    private final EvaluacionRepos evaluacionRepos;
    private final ResultadoMapper resultadoMapper;
    private final ResultadoSubtestMapper resultadoSubtestMapper;

    @Override
    public ResultadoResponse generar(Long idEvaluacion) {
        Evaluacion evaluacion = buscarEvaluacion(idEvaluacion);

        validarEvaluacionCompletada(evaluacion);
        validarResultadoNoGenerado(idEvaluacion);

        Resultado resultado = new Resultado();
        resultado.setEvaluacion(evaluacion);
        resultado.setPuntajeTotal(0);
        resultado.setFechaResultado(LocalDateTime.now());

        Resultado resultadoGuardado = resultadoRepos.save(resultado);

        return resultadoMapper.toResponse(resultadoGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultadoResponse obtenerPorId(Long idResultado) {
        return resultadoMapper.toResponse(buscarResultado(idResultado));
    }

    @Override
    @Transactional(readOnly = true)
    public ResultadoResponse obtenerPorEvaluacion(Long idEvaluacion) {
        Resultado resultado = resultadoRepos.findByEvaluacion_IdEvaluacion(idEvaluacion)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró un resultado para la evaluación con ID: " + idEvaluacion
                ));

        return resultadoMapper.toResponse(resultado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResultadoSubtestResponse> listarSubtests(Long idResultado) {
        buscarResultado(idResultado);

        return resultadoSubtestRepos
                .findByResultado_IdResultadoOrderByTipoAsc(idResultado)
                .stream()
                .map(resultadoSubtestMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ResultadoResponse> listar(Pageable pageable) {
        Pageable pageableOrdenado = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "idResultado")
        );

        return resultadoRepos.findAll(pageableOrdenado)
                .map(resultadoMapper::toResponse);
    }

    private Evaluacion buscarEvaluacion(Long idEvaluacion) {
        return evaluacionRepos.findById(idEvaluacion)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la evaluación con ID: " + idEvaluacion
                ));
    }

    private Resultado buscarResultado(Long idResultado) {
        return resultadoRepos.findById(idResultado)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el resultado con ID: " + idResultado
                ));
    }

    private void validarEvaluacionCompletada(Evaluacion evaluacion) {
        if (evaluacion.getEstado() != EstadoEvaluacion.COMPLETADA) {
            throw new BadRequestException(
                    "La evaluación con ID " + evaluacion.getIdEvaluacion()
                            + " aún no se encuentra completada"
            );
        }
    }

    private void validarResultadoNoGenerado(Long idEvaluacion) {
        if (resultadoRepos.existsByEvaluacion_IdEvaluacion(idEvaluacion)) {
            throw new ConflictException(
                    "La evaluación con ID " + idEvaluacion + " ya tiene un resultado generado"
            );
        }
    }
}