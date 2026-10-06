package com.teach.helpwithteacch.Services.IMPL;

import com.fasterxml.jackson.databind.JsonNode;
import com.teach.helpwithteacch.DTO.EvaluacionConfig.*;
import com.teach.helpwithteacch.DTO.Resultado.*;
import com.teach.helpwithteacch.Entidades.*;
import com.teach.helpwithteacch.Enum.EstadoEvaluacion;
import com.teach.helpwithteacch.Enum.TipoPrueba;
import com.teach.helpwithteacch.Enum.TipoSubtest;
import com.teach.helpwithteacch.Mapper.ResultadoMapper;
import com.teach.helpwithteacch.Mapper.ResultadoSubtestMapper;
import com.teach.helpwithteacch.Repository.EvaluacionRepos;
import com.teach.helpwithteacch.Repository.RespuestaRepos;
import com.teach.helpwithteacch.Repository.ResultadoRepos;
import com.teach.helpwithteacch.Repository.ResultadoSubtestRepos;
import com.teach.helpwithteacch.Security.Exceptions.*;
import com.teach.helpwithteacch.Services.EvaluacionConfigService;
import com.teach.helpwithteacch.Services.ResultadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ResultadoIMPL implements ResultadoService {
    private final ResultadoRepos resultadoRepos;
    private final ResultadoSubtestRepos resultadoSubtestRepos;
    private final EvaluacionRepos evaluacionRepos;
    private final RespuestaRepos respuestaRepos;
    private final ResultadoMapper resultadoMapper;
    private final ResultadoSubtestMapper resultadoSubtestMapper;
    private final EvaluacionConfigService evaluacionConfigService;

    @Override
    public ResultadoResponse generar(Long idEvaluacion) {
        Evaluacion evaluacion = buscarEvaluacion(idEvaluacion);
        validarEvaluacionCompletada(evaluacion);
        validarEvaluacionKabc(evaluacion);
        validarResultadoNoGenerado(idEvaluacion);
        List<Respuesta> respuestas = respuestaRepos.findByEvaluacion_IdEvaluacionOrderByItemIdAscSerieIdAsc(idEvaluacion);
        if (respuestas == null || respuestas.isEmpty()) {
            throw new BadRequestException("La evaluación con ID " + idEvaluacion + " no tiene respuestas registradas");
        }
        Version version = evaluacion.getVersion();
        Prueba prueba = version.getPrueba();
        EvaluacionConfigResponse configuracion = evaluacionConfigService.obtenerConfiguracion(prueba.getTipo().name(), version.getNumeroVersion());
        if (configuracion == null || configuracion.getAssessment() == null || configuracion.getAssessment().getItems() == null || configuracion.getAssessment().getItems().isEmpty()) {
            throw new BadRequestException("La configuración de la evaluación K-ABC no contiene ítems");
        }
        List<ItemConfig> items = configuracion.getAssessment().getItems();
        Map<String, Respuesta> respuestasMap = respuestas.stream().collect(Collectors.toMap(
                respuesta -> construirClave(respuesta.getItemId(), respuesta.getSerieId()),
                Function.identity(),
                (r1, r2) -> {
                    throw new BadRequestException("Existe más de una respuesta para el ítem " + r1.getItemId() + ", serie " + r1.getSerieId());
                }
        ));
        Set<String> clavesConfiguradas = new HashSet<>();
        Map<Integer, TipoSubtest> subtestPorItem = new HashMap<>();
        for (ItemConfig item : items) {
            validarItem(item);
            TipoSubtest tipoSubtest = obtenerTipoSubtest(item.getSubtest());
            subtestPorItem.put(item.getId(), tipoSubtest);
            for (SerieConfig serie : item.getSeries()) {
                validarSerie(item, serie);
                clavesConfiguradas.add(construirClave(item.getId(), serie.getId()));
            }
        }
        for (String clave : clavesConfiguradas) {
            if (!respuestasMap.containsKey(clave)) {
                throw new BadRequestException("Falta una respuesta para la combinación " + clave);
            }
        }
        for (String clave : respuestasMap.keySet()) {
            if (!clavesConfiguradas.contains(clave)) {
                throw new BadRequestException("La respuesta " + clave + " no pertenece a la configuración de la versión " + version.getNumeroVersion());
            }
        }
        Map<TipoSubtest, Integer> puntajesSubtest = new EnumMap<>(TipoSubtest.class);
        int puntajeTotal = 0;
        for (Respuesta respuesta : respuestas) {
            if (respuesta.getPuntaje() == null) {
                throw new BadRequestException("La respuesta del ítem " + respuesta.getItemId() + ", serie " + respuesta.getSerieId() + " no tiene puntaje calculado");
            }
            if (respuesta.getPuntaje() < 0) {
                throw new BadRequestException("La respuesta del ítem " + respuesta.getItemId() + ", serie " + respuesta.getSerieId() + " tiene un puntaje inválido");
            }
            TipoSubtest tipoSubtest = subtestPorItem.get(respuesta.getItemId());
            if (tipoSubtest == null) {
                throw new BadRequestException("No se encontró el subtest para el ítem " + respuesta.getItemId());
            }
            int puntaje = respuesta.getPuntaje();
            puntajeTotal += puntaje;
            puntajesSubtest.merge(tipoSubtest, puntaje, Integer::sum);
        }
        Resultado resultado = new Resultado();
        resultado.setEvaluacion(evaluacion);
        resultado.setPuntajeTotal(puntajeTotal);
        resultado.setFechaResultado(LocalDateTime.now());
        Resultado resultadoGuardado = resultadoRepos.save(resultado);
        for (Map.Entry<TipoSubtest, Integer> entry : puntajesSubtest.entrySet()) {
            ResultadoSubtest resultadoSubtest = new ResultadoSubtest();
            resultadoSubtest.setResultado(resultadoGuardado);
            resultadoSubtest.setTipo(entry.getKey());
            resultadoSubtest.setPuntaje(entry.getValue());
            resultadoSubtestRepos.save(resultadoSubtest);
        }
        return resultadoMapper.toResponse(resultadoGuardado);
    }

    private TipoSubtest obtenerTipoSubtest(String subtest) {
        if (subtest == null || subtest.isBlank()) {
            throw new BadRequestException("El subtest no puede ser nulo o vacío");
        }
        return switch (subtest.trim().toLowerCase()) {
            case "movimientos de manos", "recuerdo de números", "orden de palabras", "ventana mágica" ->
                    TipoSubtest.PROCESAMIENTO_SECUENCIAL;
            case "reconocimiento de caras", "cierre gestáltico", "triángulos", "matrices análogas", "memoria espacial", "series de fotografías" ->
                    TipoSubtest.PROCESAMIENTO_SIMULTANEO;
            case "vocabulario expresivo", "caras y lugares", "aritmética", "adivinanzas", "lectura / decodificación", "lectura / comprensión" ->
                    TipoSubtest.CONOCIMIENTOS;
            default -> throw new BadRequestException("El subtest '" + subtest + "' no es válido");
        };
    }

    private String construirClave(Integer itemId, Integer serieId) {
        return itemId + "-" + serieId;
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
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró un resultado para la evaluación con ID: " + idEvaluacion));
        return resultadoMapper.toResponse(resultado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResultadoSubtestResponse> listarSubtests(Long idResultado) {
        buscarResultado(idResultado);
        return resultadoSubtestRepos.findByResultado_IdResultadoOrderByTipoAsc(idResultado)
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
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la evaluación con ID: " + idEvaluacion));
    }

    private Resultado buscarResultado(Long idResultado) {
        return resultadoRepos.findById(idResultado)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el resultado con ID: " + idResultado));
    }

    private void validarEvaluacionCompletada(Evaluacion evaluacion) {
        if (evaluacion.getEstado() != EstadoEvaluacion.COMPLETADA) {
            throw new BadRequestException("La evaluación con ID " + evaluacion.getIdEvaluacion() + " aún no se encuentra completada");
        }
    }

    private void validarResultadoNoGenerado(Long idEvaluacion) {
        if (resultadoRepos.existsByEvaluacion_IdEvaluacion(idEvaluacion)) {
            throw new ConflictException("La evaluación con ID " + idEvaluacion + " ya tiene un resultado generado");
        }
    }

    private void validarItem(ItemConfig item) {
        if (item == null) {
            throw new BadRequestException("La configuración contiene un ítem nulo");
        }
        if (item.getId() == null) {
            throw new BadRequestException("La configuración contiene un ítem sin ID");
        }
        if (item.getSeries() == null || item.getSeries().isEmpty()) {
            throw new BadRequestException("El ítem " + item.getId() + " no tiene series configuradas");
        }
        if (item.getSubtest() == null || item.getSubtest().isBlank()) {
            throw new BadRequestException("El ítem " + item.getId() + " no tiene subtest configurado");
        }
    }

    private void validarSerie(ItemConfig item, SerieConfig serie) {
        if (serie == null) {
            throw new BadRequestException("El ítem " + item.getId() + " contiene una serie nula");
        }
        if (serie.getId() == null) {
            throw new BadRequestException("El ítem " + item.getId() + " contiene una serie sin ID");
        }
        if (serie.getExpectedAnswer() == null) {
            throw new BadRequestException("La serie " + serie.getId() + " del ítem " + item.getId() + " no tiene respuesta esperada");
        }
    }

    private void validarEvaluacionKabc(Evaluacion evaluacion) {
        if (evaluacion.getVersion() == null || evaluacion.getVersion().getPrueba() == null) {
            throw new BadRequestException("La evaluación no tiene una prueba asociada");
        }
        if (evaluacion.getVersion().getPrueba().getTipo() != TipoPrueba.KABC) {
            throw new BadRequestException("Solo se pueden generar resultados para evaluaciones K-ABC");
        }
    }
}