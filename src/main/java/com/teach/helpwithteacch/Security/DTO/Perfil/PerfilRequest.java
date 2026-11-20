package com.teach.helpwithteacch.Security.DTO.Perfil;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Datos que el propio usuario puede modificar de su perfil.
 * El rol solo puede cambiar entre PADRE y DOCENTE ("ocupación");
 * nunca a ADMIN.
 */
@Getter
@Setter
public class PerfilRequest {

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

    /**
     * PADRE o DOCENTE. Si se omite, se mantiene la ocupación actual.
     */
    private String ocupacion;
}
