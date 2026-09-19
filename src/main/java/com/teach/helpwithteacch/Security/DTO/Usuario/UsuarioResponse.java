package com.teach.helpwithteacch.Security.DTO.Usuario;

import com.teach.helpwithteacch.Auditoria.AuditoriaResponse;
import com.teach.helpwithteacch.Enum.Estado;
import lombok.*;

@Getter
@Setter
public class UsuarioResponse extends AuditoriaResponse {
    private Long idUsuario;
    private Long idRol;
    private String nombres;
    private String apellidos;
    private String email;
    private Estado estado;
}