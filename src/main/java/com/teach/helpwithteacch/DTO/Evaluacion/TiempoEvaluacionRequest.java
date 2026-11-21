package com.teach.helpwithteacch.DTO.Evaluacion;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Tiempo activo acumulado de una evaluación, en segundos.
 */
@Getter
@Setter
public class TiempoEvaluacionRequest {

    @NotNull(message = "Los segundos son obligatorios")
    @Min(value = 0, message = "Los segundos no pueden ser negativos")
    @Max(value = 86400, message = "El tiempo no puede superar las 24 horas")
    private Integer segundos;
}
