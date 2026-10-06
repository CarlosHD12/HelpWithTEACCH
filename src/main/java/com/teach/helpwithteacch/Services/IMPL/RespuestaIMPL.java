package com.teach.helpwithteacch.Services.IMPL;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import com.teach.helpwithteacch.DTO.EvaluacionConfig.EvaluacionConfigResponse;
import com.teach.helpwithteacch.DTO.EvaluacionConfig.ItemConfig;
import com.teach.helpwithteacch.DTO.EvaluacionConfig.OpcionConfig;
import com.teach.helpwithteacch.DTO.EvaluacionConfig.SerieConfig;
import com.teach.helpwithteacch.DTO.Respuesta.*;

import com.teach.helpwithteacch.Entidades.Evaluacion;
import com.teach.helpwithteacch.Entidades.Respuesta;

import com.teach.helpwithteacch.Enum.EstadoEvaluacion;

import com.teach.helpwithteacch.Mapper.RespuestaMapper;

import com.teach.helpwithteacch.Repository.EvaluacionRepos;
import com.teach.helpwithteacch.Repository.RespuestaRepos;

import com.teach.helpwithteacch.Security.Exceptions.*;

import com.teach.helpwithteacch.Services.EvaluacionConfigService;
import com.teach.helpwithteacch.Services.RespuestaService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
@Transactional
public class RespuestaIMPL implements RespuestaService {


    private final RespuestaRepos respuestaRepos;

    private final EvaluacionRepos evaluacionRepos;

    private final RespuestaMapper respuestaMapper;

    private final EvaluacionConfigService evaluacionConfigService;


    // =====================================================
    // GUARDAR RESPUESTAS
    // =====================================================

    @Override
    public List<RespuestaResponse> guardar(
            RespuestaRequest request) {

        Evaluacion evaluacion =
                buscarEvaluacion(
                        request.getIdEvaluacion()
                );

        validarEvaluacionActiva(
                evaluacion
        );


        List<RespuestaResponse> respuestas =
                request.getItems()
                        .stream()
                        .map(item ->
                                guardarRespuesta(
                                        evaluacion,
                                        item
                                )
                        )
                        .toList();


        actualizarEstadoEvaluacion(
                evaluacion
        );


        return respuestas;
    }


    // =====================================================
    // OBTENER RESPUESTA POR ID
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public RespuestaResponse obtenerPorId(
            Long idRespuesta) {

        return respuestaMapper.toResponse(
                buscarRespuesta(idRespuesta)
        );
    }


    // =====================================================
    // LISTAR RESPUESTAS POR EVALUACIÓN
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<RespuestaResponse> listarPorEvaluacion(
            Long idEvaluacion) {

        buscarEvaluacion(
                idEvaluacion
        );


        return respuestaRepos
                .findByEvaluacion_IdEvaluacionOrderByItemIdAscSerieIdAsc(
                        idEvaluacion
                )
                .stream()
                .map(respuestaMapper::toResponse)
                .toList();
    }


    // =====================================================
    // LISTAR RESPUESTAS
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public Page<RespuestaResponse> listar(
            Pageable pageable) {

        Pageable pageableOrdenado =
                PageRequest.of(
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


    // =====================================================
    // GUARDAR UNA RESPUESTA
    // =====================================================

    private RespuestaResponse guardarRespuesta(
            Evaluacion evaluacion,
            RespuestaItemRequest request) {


        EvaluacionConfigResponse configuracion =
                evaluacionConfigService.obtenerConfiguracion(
                        evaluacion
                                .getVersion()
                                .getPrueba()
                                .getTipo()
                                .name(),

                        evaluacion
                                .getVersion()
                                .getNumeroVersion()
                );


        ItemConfig item =
                validarItemYSerie(
                        configuracion,
                        request
                );


        Respuesta respuesta =
                respuestaRepos
                        .findByEvaluacion_IdEvaluacionAndItemIdAndSerieId(
                                evaluacion.getIdEvaluacion(),
                                request.getItemId(),
                                request.getSerieId()
                        )
                        .orElse(null);


        if (respuesta == null) {

            respuesta =
                    respuestaMapper.toEntity(
                            request
                    );

            respuesta.setEvaluacion(
                    evaluacion
            );

        } else {

            respuestaMapper.updateEntity(
                    request,
                    respuesta
            );
        }


        ResultadoRespuesta resultado =
                evaluarRespuesta(
                        configuracion,
                        item,
                        request
                );


        respuesta.setCorrecta(
                resultado.correcta()
        );


        respuesta.setPuntaje(
                resultado.puntaje()
        );


        return respuestaMapper.toResponse(
                respuestaRepos.save(
                        respuesta
                )
        );
    }


    // =====================================================
    // EVALUAR RESPUESTA
    // =====================================================

    private ResultadoRespuesta evaluarRespuesta(
            EvaluacionConfigResponse configuracion,
            ItemConfig item,
            RespuestaItemRequest request) {


        String codigo =
                configuracion
                        .getAssessment()
                        .getCode();


        JsonNode valorRespuesta =
                convertirAJsonNode(
                        request.getValor()
                );


        // -------------------------------------------------
        // Q-CHAT
        // -------------------------------------------------

        if ("QCHAT".equalsIgnoreCase(codigo)) {

            return evaluarQChat(
                    item,
                    valorRespuesta
            );
        }


        // -------------------------------------------------
        // K-ABC
        // -------------------------------------------------

        if ("KABC".equalsIgnoreCase(codigo)) {

            return evaluarKabc(
                    item,
                    request.getSerieId(),
                    valorRespuesta
            );
        }


        throw new BadRequestException(
                "Tipo de evaluación no soportado: "
                        + codigo
        );
    }


    // =====================================================
    // EVALUAR Q-CHAT
    // =====================================================

    private ResultadoRespuesta evaluarQChat(
            ItemConfig item,
            JsonNode valorRespuesta) {


        if (item.getOptions() == null
                || item.getOptions().isEmpty()) {

            throw new BadRequestException(
                    "El ítem "
                            + item.getId()
                            + " no tiene opciones configuradas"
            );
        }


        OpcionConfig opcionCorrecta =
                item.getOptions()
                        .stream()
                        .filter(opcion ->
                                opcion.getValue() != null
                                        && valorRespuesta != null
                                        && valorRespuesta.isNumber()
                                        && valorRespuesta.intValue()
                                        == opcion.getValue()
                        )
                        .findFirst()
                        .orElse(null);


        if (opcionCorrecta == null) {

            throw new BadRequestException(
                    "El valor enviado para el ítem "
                            + item.getId()
                            + " no corresponde a una opción válida"
            );
        }


        boolean correcta =
                Integer.valueOf(1)
                        .equals(
                                opcionCorrecta.getValue()
                        );


        int puntaje =
                correcta
                        ? 1
                        : 0;


        return new ResultadoRespuesta(
                correcta,
                puntaje
        );
    }


    // =====================================================
    // EVALUAR K-ABC
    // =====================================================

    private ResultadoRespuesta evaluarKabc(
            ItemConfig item,
            Integer serieId,
            JsonNode valorRespuesta) {


        // -------------------------------------------------
        // VALIDAR SERIE
        // -------------------------------------------------

        if (serieId == null) {

            throw new BadRequestException(
                    "La serie es requerida para K-ABC"
            );
        }


        if (item == null) {

            throw new BadRequestException(
                    "El ítem de K-ABC es nulo"
            );
        }


        if (item.getSeries() == null
                || item.getSeries().isEmpty()) {

            throw new BadRequestException(
                    "El ítem "
                            + item.getId()
                            + " no tiene series configuradas"
            );
        }


        // -------------------------------------------------
        // BUSCAR SERIE
        // -------------------------------------------------

        SerieConfig serie =
                item.getSeries()
                        .stream()
                        .filter(s ->
                                Objects.equals(
                                        s.getId(),
                                        serieId
                                )
                        )
                        .findFirst()
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "La serie "
                                                + serieId
                                                + " no pertenece al ítem "
                                                + item.getId()
                                )
                        );


        // -------------------------------------------------
        // VALIDAR RESPUESTA ESPERADA
        // -------------------------------------------------

        if (serie.getExpectedAnswer() == null) {

            throw new BadRequestException(
                    "La serie "
                            + serieId
                            + " del ítem "
                            + item.getId()
                            + " no tiene respuesta esperada"
            );
        }


        // -------------------------------------------------
        // VALIDAR RESPUESTA RECIBIDA
        // -------------------------------------------------

        if (valorRespuesta == null) {

            throw new BadRequestException(
                    "La respuesta del ítem "
                            + item.getId()
                            + ", serie "
                            + serieId
                            + " no puede ser nula"
            );
        }


        // -------------------------------------------------
        // OBJECT MAPPER
        // -------------------------------------------------

        ObjectMapper objectMapper =
                new ObjectMapper();


        // -------------------------------------------------
        // CONVERTIR EXPECTED ANSWER
        // -------------------------------------------------

        JsonNode esperado =
                convertirAJsonNode(
                        serie.getExpectedAnswer()
                );


        // -------------------------------------------------
        // RESPUESTA RECIBIDA
        // -------------------------------------------------

        JsonNode recibido =
                valorRespuesta;


        // -------------------------------------------------
        // NORMALIZAR EXPECTED
        // -------------------------------------------------

        esperado =
                normalizarRespuestaKabc(
                        esperado,
                        objectMapper
                );


        // -------------------------------------------------
        // NORMALIZAR RECIBIDO
        // -------------------------------------------------

        recibido =
                normalizarRespuestaKabc(
                        recibido,
                        objectMapper
                );


        // =================================================
        // DEBUG K-ABC
        // =================================================

        System.out.println();
        System.out.println(
                "========================================"
        );

        System.out.println(
                "             DEBUG K-ABC"
        );

        System.out.println(
                "========================================"
        );

        System.out.println(
                "ITEM: "
                        + item.getId()
        );

        System.out.println(
                "SERIE: "
                        + serieId
        );

        System.out.println();
System.out.println("==================================================");
System.out.println("                 DEBUG K-ABC");
System.out.println("==================================================");

System.out.println("ITEM ID: " + item.getId());
System.out.println("SERIE ID: " + serieId);

System.out.println();
System.out.println("----- EXPECTED ORIGINAL -----");
System.out.println("Valor Java: " + serie.getExpectedAnswer());
System.out.println(
        "Clase Java: "
                + (
                serie.getExpectedAnswer() == null
                        ? "NULL"
                        : serie.getExpectedAnswer().getClass().getName()
        )
);

System.out.println();
System.out.println("----- EXPECTED JSON -----");
System.out.println("Esperado: " + esperado);
System.out.println("Esperado.toString(): " + esperado.toString());
System.out.println(
        "Esperado.isArray(): "
                + esperado.isArray()
);
System.out.println(
        "Esperado.isTextual(): "
                + esperado.isTextual()
);

System.out.println();
System.out.println("----- RECIBIDO -----");
System.out.println("Recibido: " + recibido);
System.out.println("Recibido.toString(): " + recibido.toString());
System.out.println(
        "Recibido.isArray(): "
                + recibido.isArray()
);
System.out.println(
        "Recibido.isTextual(): "
                + recibido.isTextual()
);

System.out.println();
System.out.println("----- COMPARACIÓN -----");

System.out.println(
        "EXPECTED.equals(RECIBIDO): "
                + esperado.equals(recibido)
);

System.out.println(
        "EXPECTED TEXT: "
                + esperado.toString()
);

System.out.println(
        "RECIBIDO TEXT: "
                + recibido.toString()
);
System.out.println(
        "EXPECTED SIZE: "
                + (
                esperado.isArray()
                        ? esperado.size()
                        : "NO ES ARRAY"
        )
);

System.out.println(
        "RECIBIDO SIZE: "
                + (
                recibido.isArray()
                        ? recibido.size()
                        : "NO ES ARRAY"
        )
);

if (esperado.isArray()) {

    System.out.println();
    System.out.println("----- ELEMENTOS EXPECTED -----");

    for (int i = 0; i < esperado.size(); i++) {

        JsonNode elemento =
                esperado.get(i);

        System.out.println(
                "EXPECTED["
                        + i
                        + "] = "
                        + elemento
                        + " | tipo="
                        + elemento.getNodeType()
        );
    }
}

if (recibido.isArray()) {

    System.out.println();
    System.out.println("----- ELEMENTOS RECIBIDO -----");

    for (int i = 0; i < recibido.size(); i++) {

        JsonNode elemento =
                recibido.get(i);

        System.out.println(
                "RECIBIDO["
                        + i
                        + "] = "
                        + elemento
                        + " | tipo="
                        + elemento.getNodeType()
        );
    }
}

System.out.println();
System.out.println("==================================================");
System.out.println();

        System.out.println(
                "EXPECTED TYPE: "
                        + (
                        esperado == null
                                ? "NULL"
                                : esperado
                                .getClass()
                                .getName()
                )
        );

        System.out.println(
                "RECIBIDO: "
                        + recibido
        );

        System.out.println(
                "RECIBIDO TYPE: "
                        + (
                        recibido == null
                                ? "NULL"
                                : recibido
                                .getClass()
                                .getName()
                )
        );

        System.out.println(
                "EQUALS: "
                        + (
                        esperado != null
                                && esperado.equals(
                                recibido
                        )
                )
        );

        System.out.println(
                "========================================"
        );

        System.out.println();


        // -------------------------------------------------
        // COMPARAR
        // -------------------------------------------------

        boolean correcta =
                esperado != null
                        && esperado.equals(
                        recibido
                );


        int puntaje =
                correcta
                        ? 1
                        : 0;


        return new ResultadoRespuesta(
                correcta,
                puntaje
        );
    }


    // =====================================================
    // NORMALIZAR RESPUESTA K-ABC
    // =====================================================

    private JsonNode normalizarRespuestaKabc(
            JsonNode valor,
            ObjectMapper objectMapper) {


        if (valor == null) {

            return null;
        }


        // Si ya es un array, objeto,
        // número, boolean, etc.
        // se mantiene tal cual.

        if (!valor.isTextual()) {

            return valor;
        }


        String texto =
                valor.asText();


        if (texto == null) {

            return valor;
        }


        texto =
                texto.trim();


        if (texto.isEmpty()) {

            return valor;
        }


        // -------------------------------------------------
        // INTENTAR INTERPRETAR EL TEXTO COMO JSON
        // -------------------------------------------------

        try {

            JsonNode convertido =
                    objectMapper.readTree(
                            texto
                    );


            if (convertido != null) {

                return convertido;
            }

        } catch (Exception ignored) {

            // Si no es JSON válido,
            // se conserva como texto.
        }


        return valor;
    }


    // =====================================================
    // VALIDAR ITEM Y SERIE
    // =====================================================

    private ItemConfig validarItemYSerie(
            EvaluacionConfigResponse configuracion,
            RespuestaItemRequest request) {


        if (configuracion == null
                || configuracion.getAssessment() == null
                || configuracion
                .getAssessment()
                .getItems() == null) {

            throw new BadRequestException(
                    "La configuración de la evaluación no es válida"
            );
        }


        List<ItemConfig> items =
                configuracion
                        .getAssessment()
                        .getItems();


        ItemConfig item =
                items.stream()
                        .filter(i ->
                                Objects.equals(
                                        i.getId(),
                                        request.getItemId()
                                )
                        )
                        .findFirst()
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "El ítem "
                                                + request.getItemId()
                                                + " no pertenece a la evaluación"
                                )
                        );


        String codigo =
                configuracion
                        .getAssessment()
                        .getCode();


        // -------------------------------------------------
        // Q-CHAT
        // -------------------------------------------------

        if ("QCHAT".equalsIgnoreCase(codigo)) {

            if (request.getSerieId() == null
                    || request.getSerieId() != 1) {

                throw new BadRequestException(
                        "Q-CHAT utiliza la serie 1 para cada ítem"
                );
            }


            return item;
        }


        // -------------------------------------------------
        // K-ABC
        // -------------------------------------------------

        if ("KABC".equalsIgnoreCase(codigo)) {

            if (request.getSerieId() == null) {

                throw new BadRequestException(
                        "La serie es requerida para K-ABC"
                );
            }


            if (item.getSeries() == null
                    || item.getSeries()
                    .stream()
                    .noneMatch(
                            serie ->
                                    Objects.equals(
                                            serie.getId(),
                                            request.getSerieId()
                                    )
                    )) {

                throw new BadRequestException(
                        "La serie "
                                + request.getSerieId()
                                + " no pertenece al ítem "
                                + request.getItemId()
                );
            }


            return item;
        }


        throw new BadRequestException(
                "Tipo de evaluación no soportado: "
                        + codigo
        );
    }


    // =====================================================
    // CONVERTIR VALOR A JSON NODE
    // =====================================================

    private JsonNode convertirAJsonNode(Object valor) {

    if (valor == null) {
        return null;
    }

    if (valor instanceof JsonNode) {
        return (JsonNode) valor;
    }

    try {
        ObjectMapper objectMapper =
                new ObjectMapper();

        return objectMapper.valueToTree(valor);

    } catch (Exception e) {

        throw new BadRequestException(
                "No se pudo procesar el valor de la respuesta"
        );
    }
}


    // =====================================================
    // ACTUALIZAR ESTADO DE EVALUACIÓN
    // =====================================================

    private void actualizarEstadoEvaluacion(
            Evaluacion evaluacion) {


        EvaluacionConfigResponse configuracion =
                evaluacionConfigService.obtenerConfiguracion(
                        evaluacion
                                .getVersion()
                                .getPrueba()
                                .getTipo()
                                .name(),

                        evaluacion
                                .getVersion()
                                .getNumeroVersion()
                );


        if (configuracion == null
                || configuracion.getAssessment() == null
                || configuracion
                .getAssessment()
                .getItems() == null
                || configuracion
                .getAssessment()
                .getItems()
                .isEmpty()) {

            throw new BadRequestException(
                    "La configuración de la evaluación no contiene ítems"
            );
        }


        int respuestasEsperadas =
                calcularRespuestasEsperadas(
                        configuracion
                );


        if (respuestasEsperadas <= 0) {

            throw new BadRequestException(
                    "La configuración de la evaluación no contiene respuestas esperadas"
            );
        }


        List<Respuesta> respuestas =
                respuestaRepos
                        .findByEvaluacion_IdEvaluacionOrderByItemIdAscSerieIdAsc(
                                evaluacion
                                        .getIdEvaluacion()
                        );


        int respuestasRegistradas =
                respuestas.size();


        int progreso =
                (int) Math.floor(
                        respuestasRegistradas
                                * 100.0
                                / respuestasEsperadas
                );


        evaluacion.setProgreso(
                Math.min(
                        progreso,
                        100
                )
        );


        evaluacion.setFechaUltimoAcceso(
                LocalDateTime.now()
        );


        if (!respuestas.isEmpty()) {

            Respuesta ultimaRespuesta =
                    respuestas.get(
                            respuestas.size() - 1
                    );


            evaluacion.setItemActual(
                    ultimaRespuesta.getItemId()
            );


            evaluacion.setSerieActual(
                    ultimaRespuesta.getSerieId()
            );
        }


        if (respuestasRegistradas
                >= respuestasEsperadas) {

            evaluacion.setEstado(
                    EstadoEvaluacion.COMPLETADA
            );


            evaluacion.setProgreso(
                    100
            );


            evaluacion.setFechaFinalizacion(
                    LocalDateTime.now()
            );
        }


        evaluacionRepos.save(
                evaluacion
        );
    }


    // =====================================================
    // CALCULAR RESPUESTAS ESPERADAS
    // =====================================================

    private int calcularRespuestasEsperadas(
            EvaluacionConfigResponse configuracion) {


        String codigo =
                configuracion
                        .getAssessment()
                        .getCode();


        List<ItemConfig> items =
                configuracion
                        .getAssessment()
                        .getItems();


        // -------------------------------------------------
        // Q-CHAT
        // -------------------------------------------------

        if ("QCHAT".equalsIgnoreCase(codigo)) {

            return items.size();
        }


        // -------------------------------------------------
        // K-ABC
        // -------------------------------------------------

        return items.stream()
                .map(ItemConfig::getSeries)
                .filter(Objects::nonNull)
                .mapToInt(List::size)
                .sum();
    }


    // =====================================================
    // BUSCAR EVALUACIÓN
    // =====================================================

    private Evaluacion buscarEvaluacion(
            Long idEvaluacion) {

        return evaluacionRepos
                .findById(
                        idEvaluacion
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No se encontró la evaluación con ID: "
                                        + idEvaluacion
                        )
                );
    }


    // =====================================================
    // BUSCAR RESPUESTA
    // =====================================================

    private Respuesta buscarRespuesta(
            Long idRespuesta) {

        return respuestaRepos
                .findById(
                        idRespuesta
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No se encontró la respuesta con ID: "
                                        + idRespuesta
                        )
                );
    }


    // =====================================================
    // VALIDAR EVALUACIÓN ACTIVA
    // =====================================================

    private void validarEvaluacionActiva(
            Evaluacion evaluacion) {


        if (evaluacion.getEstado()
                != EstadoEvaluacion.EN_PROGRESO) {

            throw new BadRequestException(
                    "No se pueden registrar respuestas en una evaluación con estado: "
                            + evaluacion.getEstado()
            );
        }
    }


    // =====================================================
    // RESULTADO INTERNO
    // =====================================================

    private record ResultadoRespuesta(
            boolean correcta,
            int puntaje
    ) {
    }
}