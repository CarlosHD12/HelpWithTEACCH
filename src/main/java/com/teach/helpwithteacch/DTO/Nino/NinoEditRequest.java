package com.teach.helpwithteacch.DTO.Nino;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
public class NinoEditRequest {
    @NotBlank(message = "Nombres requeridos")
    @Size(max = 100, message = "Los nombres no pueden superar los 100 caracteres")
    private String nombres;

    @NotBlank(message = "Apellidos requeridos")
    @Size(max = 100, message = "Los apellidos no pueden superar los 100 caracteres")
    private String apellidos;

    @NotNull(message = "Fecha de nacimiento requerida")
    @Past(message = "La fecha de nacimiento debe ser anterior a la fecha actual")
    private LocalDate fechaNacimiento;

    @NotBlank(message = "Sexo requerido")
    @Size(max = 20, message = "El sexo no puede superar los 20 caracteres")
    private String sexo;

    @Size(max = 100, message = "La etnia no puede superar los 100 caracteres")
    private String etnia;

    @NotNull(message = "Debe indicar si presenta ictericia")
    private Boolean ictericia;

    @NotNull(message = "Debe indicar si tiene un familiar con TEA")
    private Boolean familiarConTea;

    @Size(max = 500, message = "La URL de la foto no puede superar los 500 caracteres")
    private String fotoUrl;
}