package com.teach.helpwithteacch.DTO.Common;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
public class CambiarEstadoRequest {
    @NotEmpty(message = "Debe enviar al menos un elemento")
    @Valid
    private List<CambiarEstadoItemRequest> items;
}