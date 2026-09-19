package com.teach.helpwithteacch.DTO.Evaluacion;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class EvaluacionRequest {
    @NotNull(message = "ID del niño requerido")
    @Positive(message = "El ID del niño debe ser mayor a cero")
    private Long idNino;

    @NotNull(message = "ID del usuario requerido")
    @Positive(message = "El ID del usuario debe ser mayor a cero")
    private Long idUsuario;

    @NotNull(message = "ID de la versión requerida")
    @Positive(message = "El ID de la versión debe ser mayor a cero")
    private Long idVersion;
}