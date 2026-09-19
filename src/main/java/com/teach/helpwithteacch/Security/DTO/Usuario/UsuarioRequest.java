package com.teach.helpwithteacch.Security.DTO.Usuario;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class UsuarioRequest {
    @NotNull(message = "ID del rol requerido")
    @Positive(message = "El ID del rol debe ser mayor a cero")
    private Long idRol;

    @NotBlank(message = "Nombres requeridos")
    @Size(max = 100, message = "Los nombres no pueden superar los 100 caracteres")
    private String nombres;

    @NotBlank(message = "Apellidos requeridos")
    @Size(max = 100, message = "Los apellidos no pueden superar los 100 caracteres")
    private String apellidos;

    @NotBlank(message = "Correo electrónico requerido")
    @Email(message = "El correo electrónico no tiene un formato válido")
    @Size(max = 150, message = "El correo electrónico no puede superar los 150 caracteres")
    private String email;

    @NotBlank(message = "Contraseña requerida")
    @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
    private String password;
}