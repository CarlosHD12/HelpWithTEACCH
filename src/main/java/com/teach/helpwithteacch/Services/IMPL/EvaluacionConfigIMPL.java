package com.teach.helpwithteacch.Services.IMPL;

import com.teach.helpwithteacch.DTO.EvaluacionConfig.*;
import com.teach.helpwithteacch.Services.EvaluacionConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class EvaluacionConfigIMPL implements EvaluacionConfigService {

    private final ObjectMapper objectMapper;

    @Override
    public EvaluacionConfigResponse obtenerConfiguracion(
            String tipoPrueba,
            String numeroVersion
    ) {
        String carpeta = tipoPrueba.toLowerCase();

        String ruta = String.format(
                "evaluaciones/%s/prototype-%s.json",
                carpeta,
                numeroVersion
        );

        ClassPathResource resource = new ClassPathResource(ruta);

        if (!resource.exists()) {
            throw new IllegalArgumentException(
                    "No existe la configuración de la evaluación: " + ruta
            );
        }

        try {
            EvaluacionConfigResponse configuracion =
                    objectMapper.readValue(
                            resource.getInputStream(),
                            EvaluacionConfigResponse.class
                    );

            if (configuracion.getAssessment() == null) {
                throw new IllegalStateException(
                        "La configuración no contiene assessment: " + ruta
                );
            }

            if (configuracion.getAssessment().getCode() == null
                    || !configuracion.getAssessment().getCode()
                    .equalsIgnoreCase(tipoPrueba)) {
                throw new IllegalStateException(
                        "El tipo de prueba del JSON no coincide con la versión registrada: " + ruta
                );
            }

            if (configuracion.getAssessment().getVersion() == null
                    || !configuracion.getAssessment().getVersion()
                    .equals(numeroVersion)) {
                throw new IllegalStateException(
                        "La versión del JSON no coincide con la versión registrada: " + ruta
                );
            }

            return configuracion;

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Error al leer la configuración de la evaluación: " + ruta,
                    e
            );
        }
    }
}