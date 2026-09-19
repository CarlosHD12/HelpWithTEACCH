package com.teach.helpwithteacch.DTO.Asignarnino;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class AsignarNinoEditRequest {
    @NotNull(message = "ID del usuario requerido")
    @Positive(message = "El ID del usuario debe ser mayor a cero")
    private Long idUsuario;

    @NotNull(message = "ID del niño requerido")
    @Positive(message = "El ID del niño debe ser mayor a cero")
    private Long idNino;
}