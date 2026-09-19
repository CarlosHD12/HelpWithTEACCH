package com.teach.helpwithteacch.DTO.Respuesta;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class RespuestaItemRequest {
    @NotNull(message = "ID del ítem requerido")
    @Positive(message = "El ID del ítem debe ser mayor a cero")
    private Integer itemId;

    @Positive(message = "El ID de la serie debe ser mayor a cero")
    private Integer serieId;

    @NotBlank(message = "Tipo de respuesta requerido")
    @Size(max = 30, message = "El tipo de respuesta no puede superar los 30 caracteres")
    private String tipo;

    @NotNull(message = "Valor de respuesta requerido")
    private Object valor;
}