package com.teach.helpwithteacch.DTO.Version;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class VersionEditRequest {
    @NotNull(message = "ID de la prueba requerido")
    @Positive(message = "El ID de la prueba debe ser mayor a cero")
    private Long idPrueba;

    @NotBlank(message = "Versión requerida")
    @Size(max = 20, message = "La versión no puede superar los 20 caracteres")
    private String numeroVersion;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String descripcion;
}