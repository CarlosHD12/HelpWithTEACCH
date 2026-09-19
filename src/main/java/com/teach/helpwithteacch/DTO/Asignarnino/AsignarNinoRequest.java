package com.teach.helpwithteacch.DTO.Asignarnino;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
public class AsignarNinoRequest {
    @NotNull(message = "ID del usuario requerido")
    @Positive(message = "El ID del usuario debe ser mayor a cero")
    private Long idUsuario;

    @NotEmpty(message = "Debe asignar al menos un niño")
    @Valid
    private List<AsignarNinoItemRequest> items;
}