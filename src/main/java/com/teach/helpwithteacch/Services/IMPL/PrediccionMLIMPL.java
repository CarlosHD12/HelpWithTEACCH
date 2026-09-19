package com.teach.helpwithteacch.Services.IMPL;

import com.fasterxml.jackson.databind.JsonNode;
import com.teach.helpwithteacch.Client.PrediccionIAClient;
import com.teach.helpwithteacch.DTO.PrediccionML.*;
import com.teach.helpwithteacch.Entidades.*;
import com.teach.helpwithteacch.Enum.*;
import com.teach.helpwithteacch.Mapper.PrediccionMLMapper;
import com.teach.helpwithteacch.Repository.*;
import com.teach.helpwithteacch.Security.Entidades.Usuario;
import com.teach.helpwithteacch.Services.PrediccionMLService;
import com.teach.helpwithteacch.Specification.PrediccionMLSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PrediccionMLIMPL implements PrediccionMLService {

    private final PrediccionMLRepos prediccionMLRepos;
    private final PrediccionMLMapper prediccionMLMapper;
    private final EvaluacionRepos evaluacionRepos;
    private final RespuestaRepos respuestaRepos;
    private final PrediccionIAClient prediccionIAClient;

    @Override
    @Transactional
    public List<PrediccionMLResponse> generar(Long idEvaluacion) {

        if (idEvaluacion == null || idEvaluacion <= 0) {
            throw new RuntimeException(
                    "El ID de la evaluación es obligatorio y debe ser mayor que cero"
            );
        }

        Evaluacion evaluacion = evaluacionRepos
                .findById(idEvaluacion)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Evaluación no encontrada"
                        )
                );

        validarEvaluacion(evaluacion);

        QChatMLRequest request =
                construirRequest(evaluacion);

        QChatMLResponse response =
                prediccionIAClient.predecir(request);

        validarRespuestaIA(response);

        guardarPredicciones(
                evaluacion,
                response
        );

        return listarPorEvaluacion(idEvaluacion);
    }

    private void validarEvaluacion(
            Evaluacion evaluacion
    ) {

        if (evaluacion == null) {
            throw new RuntimeException(
                    "La evaluación es obligatoria"
            );
        }

        if (evaluacion.getEstado() != EstadoEvaluacion.COMPLETADA) {
            throw new RuntimeException(
                    "La evaluación debe estar completada para generar la predicción"
            );
        }

        if (evaluacion.getNino() == null) {
            throw new RuntimeException(
                    "La evaluación no tiene un niño asociado"
            );
        }

        if (evaluacion.getUsuario() == null) {
            throw new RuntimeException(
                    "La evaluación no tiene un usuario asociado"
            );
        }

        if (evaluacion.getVersion() == null) {
            throw new RuntimeException(
                    "La evaluación no tiene una versión de prueba asociada"
            );
        }

        if (evaluacion.getVersion().getPrueba() == null) {
            throw new RuntimeException(
                    "La versión de la evaluación no tiene una prueba asociada"
            );
        }

        if (evaluacion.getVersion()
                .getPrueba()
                .getTipo() != TipoPrueba.QCHAT) {

            throw new RuntimeException(
                    "Las predicciones ML solo están disponibles para Q-CHAT"
            );
        }
    }

    private QChatMLRequest construirRequest(
            Evaluacion evaluacion
    ) {

        Nino nino = evaluacion.getNino();
        Usuario usuario = evaluacion.getUsuario();

        List<Respuesta> respuestas =
                respuestaRepos.findByEvaluacion_IdEvaluacion(
                        evaluacion.getIdEvaluacion()
                );

        if (respuestas == null || respuestas.isEmpty()) {
            throw new RuntimeException(
                    "La evaluación no tiene respuestas registradas"
            );
        }

        Map<Integer, Integer> respuestasQChat =
                obtenerRespuestasQChat(respuestas);

        validarRespuestasQChat(respuestasQChat);

        QChatMLRequest request =
                new QChatMLRequest();

        request.setA1(
                respuestasQChat.get(1)
        );

        request.setA2(
                respuestasQChat.get(2)
        );

        request.setA3(
                respuestasQChat.get(3)
        );

        request.setA4(
                respuestasQChat.get(4)
        );

        request.setA5(
                respuestasQChat.get(5)
        );

        request.setA6(
                respuestasQChat.get(6)
        );

        request.setA7(
                respuestasQChat.get(7)
        );

        request.setA8(
                respuestasQChat.get(8)
        );

        request.setA9(
                respuestasQChat.get(9)
        );

        request.setA10(
                respuestasQChat.get(10)
        );

        request.setEdad(
                calcularEdad(nino)
        );

        request.setSexo(
                convertirSexo(nino.getSexo())
        );

        request.setJaundice(
                convertirBooleano(
                        nino.getIctericia(),
                        "ictericia"
                )
        );

        request.setFamilia_asd(
                convertirBooleano(
                        nino.getFamiliarConTea(),
                        "antecedente familiar de TEA"
                )
        );

        request.setEtnia(
                validarEtnia(nino.getEtnia())
        );

        request.setQuien_completo(
                determinarQuienCompleto(usuario)
        );

        return request;
    }

    private Map<Integer, Integer> obtenerRespuestasQChat(
            List<Respuesta> respuestas
    ) {

        Map<Integer, Integer> valores =
                new HashMap<>();

        for (Respuesta respuesta : respuestas) {

            if (respuesta == null) {
                continue;
            }

            Integer itemId =
                    respuesta.getItemId();

            if (itemId == null) {
                continue;
            }

            if (itemId < 1 || itemId > 10) {
                continue;
            }

            Integer valor =
                    obtenerValorRespuesta(
                            respuesta.getValor()
                    );

            if (valores.containsKey(itemId)) {
                throw new RuntimeException(
                        "Existe más de una respuesta para el ítem Q-CHAT A"
                                + itemId
                );
            }

            valores.put(
                    itemId,
                    valor
            );
        }

        return valores;
    }

    private Integer obtenerValorRespuesta(
            JsonNode valor
    ) {

        if (valor == null ||
                valor.isNull()) {

            throw new RuntimeException(
                    "Una respuesta Q-CHAT no tiene valor"
            );
        }

        /*
         * Valor numérico:
         *
         * 0
         * 1
         */
        if (valor.isInt() ||
                valor.isLong()) {

            return validarValorQChat(
                    valor.asInt()
            );
        }

        /*
         * Valor textual:
         *
         * "0"
         * "1"
         */
        if (valor.isTextual()) {

            String texto =
                    valor.asText().trim();

            if ("0".equals(texto)) {
                return 0;
            }

            if ("1".equals(texto)) {
                return 1;
            }
        }

        /*
         * Objeto:
         *
         * {
         *     "value": 0
         * }
         */
        if (valor.isObject() &&
                valor.has("value")) {

            JsonNode value =
                    valor.get("value");

            if (value == null ||
                    value.isNull()) {

                throw new RuntimeException(
                        "El campo 'value' de la respuesta Q-CHAT no puede ser nulo"
                );
            }

            if (value.isInt() ||
                    value.isLong()) {

                return validarValorQChat(
                        value.asInt()
                );
            }

            if (value.isTextual()) {

                String texto =
                        value.asText().trim();

                if ("0".equals(texto)) {
                    return 0;
                }

                if ("1".equals(texto)) {
                    return 1;
                }
            }
        }

        throw new RuntimeException(
                "Valor de respuesta Q-CHAT no válido: "
                        + valor
        );
    }

    private Integer validarValorQChat(
            Integer valor
    ) {

        if (valor == null ||
                (valor != 0 && valor != 1)) {

            throw new RuntimeException(
                    "El valor de una respuesta Q-CHAT debe ser 0 o 1"
            );
        }

        return valor;
    }

    private void validarRespuestasQChat(
            Map<Integer, Integer> respuestas
    ) {

        if (respuestas == null) {
            throw new RuntimeException(
                    "No se encontraron respuestas Q-CHAT"
            );
        }

        for (int itemId = 1; itemId <= 10; itemId++) {

            if (!respuestas.containsKey(itemId)) {

                throw new RuntimeException(
                        "Falta la respuesta Q-CHAT A"
                                + itemId
                );
            }

            Integer valor =
                    respuestas.get(itemId);

            validarValorQChat(valor);
        }

        if (respuestas.size() != 10) {

            throw new RuntimeException(
                    "La evaluación Q-CHAT debe contener exactamente 10 respuestas"
            );
        }
    }

    private Integer calcularEdad(
            Nino nino
    ) {

        if (nino.getFechaNacimiento() == null) {
            throw new RuntimeException(
                    "El niño no tiene fecha de nacimiento"
            );
        }

        LocalDate fechaNacimiento =
                nino.getFechaNacimiento();

        LocalDate hoy =
                LocalDate.now();

        if (fechaNacimiento.isAfter(hoy)) {
            throw new RuntimeException(
                    "La fecha de nacimiento no puede ser posterior a la fecha actual"
            );
        }

        int edad =
                Period.between(
                        fechaNacimiento,
                        hoy
                ).getYears();

        if (edad < 0) {
            throw new RuntimeException(
                    "La edad calculada no puede ser negativa"
            );
        }

        return edad;
    }

    private Integer convertirSexo(
            String sexo
    ) {

        if (sexo == null ||
                sexo.isBlank()) {

            throw new RuntimeException(
                    "El sexo del niño es obligatorio"
            );
        }

        return switch (
                sexo.trim().toUpperCase()
                ) {

            case "F", "FEMENINO" ->
                    0;

            case "M", "MASCULINO" ->
                    1;

            default ->
                    throw new RuntimeException(
                            "Sexo no válido para el modelo de IA: "
                                    + sexo
                    );
        };
    }

    private Integer convertirBooleano(
            Boolean valor,
            String campo
    ) {

        if (valor == null) {
            throw new RuntimeException(
                    "El campo "
                            + campo
                            + " es obligatorio"
            );
        }

        return Boolean.TRUE.equals(valor)
                ? 1
                : 0;
    }

    private String validarEtnia(
            String etnia
    ) {

        if (etnia == null ||
                etnia.isBlank()) {

            throw new RuntimeException(
                    "La etnia del niño es obligatoria para generar la predicción"
            );
        }

        return switch (
                etnia.trim().toLowerCase()
                ) {

            case "middle eastern" ->
                    "Middle Eastern";

            case "white european" ->
                    "White European";

            case "white-european" ->
                    "White-European";

            case "hispanic" ->
                    "Hispanic";

            case "black" ->
                    "Black";

            case "asian" ->
                    "Asian";

            case "south asian" ->
                    "South Asian";

            case "native indian" ->
                    "Native Indian";

            case "others", "other" ->
                    "Others";

            case "latino" ->
                    "Latino";

            case "mixed" ->
                    "Mixed";

            case "pacifica" ->
                    "Pacifica";

            case "turkish" ->
                    "Turkish";

            case "?" ->
                    "?";

            default ->
                    throw new RuntimeException(
                            "Etnia no válida para el modelo de IA: "
                                    + etnia
                    );
        };
    }

    private String determinarQuienCompleto(
            Usuario usuario
    ) {

        if (usuario.getRol() == null) {
            throw new RuntimeException(
                    "El usuario no tiene un rol asignado"
            );
        }

        if (usuario.getRol().getNombre() == null) {
            throw new RuntimeException(
                    "El usuario tiene un rol sin nombre"
            );
        }

        return switch (
                usuario.getRol().getNombre()
                ) {

            case PADRE ->
                    "Family Member";

            case DOCENTE ->
                    "Teacher";

            case ADMIN ->
                    "Administrator";

            default ->
                    throw new RuntimeException(
                            "El rol del usuario no es válido para el modelo de IA"
                    );
        };
    }

    private void validarRespuestaIA(
            QChatMLResponse response
    ) {

        if (response == null) {
            throw new RuntimeException(
                    "El servicio de inteligencia artificial no devolvió una respuesta"
            );
        }

        if (response.getRandom_forest() == null) {
            throw new RuntimeException(
                    "La IA no devolvió la predicción de Random Forest"
            );
        }

        if (response.getXgboost() == null) {
            throw new RuntimeException(
                    "La IA no devolvió la predicción de XGBoost"
            );
        }

        validarModeloResponse(
                response.getRandom_forest(),
                "Random Forest"
        );

        validarModeloResponse(
                response.getXgboost(),
                "XGBoost"
        );
    }

    private void validarModeloResponse(
            ModeloMLResponse response,
            String nombreModelo
    ) {

        if (response.getPrediction() == null) {
            throw new RuntimeException(
                    "La IA no devolvió la predicción de "
                            + nombreModelo
            );
        }

        if (response.getPrediction() != 0 &&
                response.getPrediction() != 1) {

            throw new RuntimeException(
                    "La predicción de "
                            + nombreModelo
                            + " debe ser 0 o 1"
            );
        }

        if (response.getResultado() == null ||
                response.getResultado().isBlank()) {

            throw new RuntimeException(
                    "La IA no devolvió el resultado de "
                            + nombreModelo
            );
        }

        validarProbabilidad(
                response.getProbabilidad_asd()
        );
    }

    private void guardarPredicciones(
            Evaluacion evaluacion,
            QChatMLResponse response
    ) {

        LocalDateTime ahora =
                LocalDateTime.now();

        guardarPrediccion(
                evaluacion,
                ModeloML.RANDOM_FOREST,
                response.getRandom_forest(),
                ahora
        );

        guardarPrediccion(
                evaluacion,
                ModeloML.XGBOOST,
                response.getXgboost(),
                ahora
        );
    }

    private void guardarPrediccion(
            Evaluacion evaluacion,
            ModeloML modelo,
            ModeloMLResponse modeloResponse,
            LocalDateTime fechaPrediccion
    ) {

        if (modeloResponse == null) {
            throw new RuntimeException(
                    "La IA no devolvió la predicción para "
                            + modelo
            );
        }

        BigDecimal probabilidad =
                validarProbabilidad(
                        modeloResponse.getProbabilidad_asd()
                );

        PrediccionML prediccion =
                prediccionMLRepos
                        .findByEvaluacion_IdEvaluacionAndModelo(
                                evaluacion.getIdEvaluacion(),
                                modelo
                        )
                        .orElseGet(
                                PrediccionML::new
                        );

        prediccion.setEvaluacion(
                evaluacion
        );

        prediccion.setModelo(
                modelo
        );

        prediccion.setResultado(
                modeloResponse.getResultado()
        );

        prediccion.setProbabilidad(
                probabilidad
        );

        prediccion.setFechaPrediccion(
                fechaPrediccion
        );

        prediccionMLRepos.save(
                prediccion
        );
    }

    private BigDecimal validarProbabilidad(
            BigDecimal probabilidad
    ) {

        if (probabilidad == null) {
            throw new RuntimeException(
                    "La IA no devolvió la probabilidad"
            );
        }

        if (probabilidad.compareTo(
                BigDecimal.ZERO
        ) < 0 ||
                probabilidad.compareTo(
                        BigDecimal.ONE
                ) > 0) {

            throw new RuntimeException(
                    "La probabilidad devuelta por la IA no es válida"
            );
        }

        return probabilidad;
    }

    @Override
    @Transactional(readOnly = true)
    public PrediccionMLResponse obtenerPorId(
            Long idPrediccion
    ) {

        if (idPrediccion == null ||
                idPrediccion <= 0) {

            throw new RuntimeException(
                    "El ID de la predicción es obligatorio y debe ser mayor que cero"
            );
        }

        PrediccionML prediccion =
                prediccionMLRepos
                        .findById(idPrediccion)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Predicción ML no encontrada"
                                )
                        );

        return prediccionMLMapper.toResponse(
                prediccion
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrediccionMLResponse> listarPorEvaluacion(
            Long idEvaluacion
    ) {

        if (idEvaluacion == null ||
                idEvaluacion <= 0) {

            throw new RuntimeException(
                    "El ID de la evaluación es obligatorio y debe ser mayor que cero"
            );
        }

        return prediccionMLRepos
                .findByEvaluacion_IdEvaluacion(
                        idEvaluacion
                )
                .stream()
                .map(prediccionMLMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PrediccionMLResponse> listar(
            Long idEvaluacion,
            ModeloML modelo,
            String resultado,
            LocalDateTime fechaDesde,
            LocalDateTime fechaHasta,
            Pageable pageable
    ) {

        if (idEvaluacion != null &&
                idEvaluacion <= 0) {

            throw new RuntimeException(
                    "El ID de la evaluación debe ser mayor que cero"
            );
        }

        if (fechaDesde != null &&
                fechaHasta != null &&
                fechaDesde.isAfter(fechaHasta)) {

            throw new RuntimeException(
                    "La fecha desde no puede ser posterior a la fecha hasta"
            );
        }

        if (resultado != null &&
                resultado.isBlank()) {

            resultado = null;
        }

        Pageable pageableOrdenado =
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        Sort.by(
                                Sort.Direction.DESC,
                                "idPrediccion"
                        )
                );

        Specification<PrediccionML> specification =
                PrediccionMLSpecification.conFiltros(
                        idEvaluacion,
                        modelo,
                        resultado,
                        fechaDesde,
                        fechaHasta
                );

        return prediccionMLRepos
                .findAll(
                        specification,
                        pageableOrdenado
                )
                .map(prediccionMLMapper::toResponse);
    }
}