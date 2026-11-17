package com.teach.helpwithteacch.Services.IMPL;

import com.teach.helpwithteacch.DTO.EvaluacionConfig.*;
import com.teach.helpwithteacch.Security.Exceptions.ResourceNotFoundException;
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

        // "1", "1.0" y " 1.0 " se tratan como la misma versión.
        String version = normalizarVersion(numeroVersion);

        String ruta = String.format(
                "evaluaciones/%s/prototype-%s.json",
                carpeta,
                version
        );

        ClassPathResource resource = new ClassPathResource(ruta);

        if (!resource.exists()) {
            throw new ResourceNotFoundException(
                    "No existe la configuración de la evaluación: " + ruta
                            + " (versión registrada: '" + numeroVersion + "')"
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
                    || !normalizarVersion(configuracion.getAssessment().getVersion())
                    .equals(version)) {
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

    /**
     * Normaliza el número de versión para que coincida con el nombre del archivo:
     * "1" → "1.0", " 2 " → "2.0", "1.0" → "1.0", "v1.0" → "1.0".
     */
    private String normalizarVersion(String numeroVersion) {

        if (numeroVersion == null) {
            return "";
        }

        String version = numeroVersion.trim();

        if (version.toLowerCase().startsWith("v")) {
            version = version.substring(1).trim();
        }

        if (version.matches("\\d+")) {
            version = version + ".0";
        }

        return version;
    }
}