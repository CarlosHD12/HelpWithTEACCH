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

        if (tipoPrueba == null || tipoPrueba.isBlank()) {
            throw new IllegalArgumentException(
                    "El tipo de prueba no puede estar vacío"
            );
        }

        if (numeroVersion == null || numeroVersion.isBlank()) {
            throw new IllegalArgumentException(
                    "El número de versión no puede estar vacío"
            );
        }

        String carpeta = tipoPrueba
                .trim()
                .toLowerCase();

        String version = numeroVersion.trim();

        String ruta = String.format(
                "evaluaciones/%s/prototype-%s.json",
                carpeta,
                version
        );

        ClassPathResource resource =
                new ClassPathResource(ruta);

        if (!resource.exists()) {
            throw new IllegalArgumentException(
                    "No existe la configuración de la evaluación: "
                            + ruta
            );
        }

        try {
            return objectMapper.readValue(
                    resource.getInputStream(),
                    EvaluacionConfigResponse.class
            );

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Error al leer la configuración de la evaluación: "
                            + ruta,
                    e
            );
        }
    }
}