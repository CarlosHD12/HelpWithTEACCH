package com.teach.helpwithteacch.Security.DTO.Perfil;

import com.teach.helpwithteacch.Enum.Estado;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PerfilResponse {

    private Long idUsuario;

    private String nombres;

    private String apellidos;

    private String email;

    /** ADMIN, DOCENTE o PADRE */
    private String rol;

    private Estado estado;
}
