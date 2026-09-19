package com.teach.helpwithteacch.Security.DTO.Usuario;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class CambiarRolRequest {
    @NotNull(message = "ID del rol requerido")
    @Positive(message = "El ID del rol debe ser mayor a cero")
    private Long idRol;
}