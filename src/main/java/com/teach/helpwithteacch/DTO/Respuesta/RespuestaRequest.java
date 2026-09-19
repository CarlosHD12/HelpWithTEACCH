package com.teach.helpwithteacch.DTO.Respuesta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
public class RespuestaRequest {
    @NotNull(message = "ID de la evaluación requerido")
    @Positive(message = "El ID de la evaluación debe ser mayor a cero")
    private Long idEvaluacion;

    @NotEmpty(message = "Debe enviar al menos una respuesta")
    @Valid
    private List<RespuestaItemRequest> items;
}