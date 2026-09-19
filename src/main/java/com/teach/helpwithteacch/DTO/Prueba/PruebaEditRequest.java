package com.teach.helpwithteacch.DTO.Prueba;

import com.teach.helpwithteacch.Enum.TipoPrueba;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class PruebaEditRequest {
    @NotBlank(message = "Nombre requerido")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @NotNull(message = "Tipo de prueba requerido")
    private TipoPrueba tipo;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String descripcion;
}