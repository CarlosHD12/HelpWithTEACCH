package com.teach.helpwithteacch.DTO.Evaluacion;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class EvaluacionEditRequest {
    @NotNull(message = "Ítem actual requerido")
    @PositiveOrZero(message = "El ítem actual no puede ser negativo")
    private Integer itemActual;

    @NotNull(message = "Serie actual requerida")
    @PositiveOrZero(message = "La serie actual no puede ser negativa")
    private Integer serieActual;

    @NotNull(message = "Progreso requerido")
    @Min(value = 0, message = "El progreso no puede ser menor a 0")
    @Max(value = 100, message = "El progreso no puede ser mayor a 100")
    private Integer progreso;
}
