package com.teach.helpwithteacch.Security.DTO.Auth;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class CambiarPasswordRequest {
    @NotBlank(message = "Contraseña actual requerida")
    private String passwordActual;

    @NotBlank(message = "Nueva contraseña requerida")
    @Size(min = 8, max = 100, message = "La nueva contraseña debe tener entre 8 y 100 caracteres")
    private String passwordNueva;

    @NotBlank(message = "La confirmación de contraseña es requerida")
    private String confirmarPassword;
}