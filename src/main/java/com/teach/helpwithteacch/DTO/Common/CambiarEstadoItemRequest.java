package com.teach.helpwithteacch.DTO.Common;

import com.teach.helpwithteacch.Enum.Estado;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class CambiarEstadoItemRequest {
    @NotNull(message = "ID requerido")
    @Positive(message = "El ID debe ser mayor a cero")
    private Long id;

    @NotNull(message = "Estado requerido")
    private Estado estado;
}