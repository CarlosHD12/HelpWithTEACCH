package com.teach.helpwithteacch.Services;

import com.teach.helpwithteacch.DTO.EvaluacionConfig.EvaluacionConfigResponse;

public interface EvaluacionConfigService {
    EvaluacionConfigResponse obtenerConfiguracion(String tipoPrueba, String numeroVersion);
}