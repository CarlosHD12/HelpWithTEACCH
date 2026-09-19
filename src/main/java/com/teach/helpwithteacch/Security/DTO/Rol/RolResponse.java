package com.teach.helpwithteacch.Security.DTO.Rol;

import com.teach.helpwithteacch.Auditoria.AuditoriaResponse;
import com.teach.helpwithteacch.Enum.Estado;
import com.teach.helpwithteacch.Enum.RolNombre;
import lombok.*;

@Getter
@Setter
public class RolResponse extends AuditoriaResponse {
    private Long idRol;
    private RolNombre nombre;
    private String descripcion;
    private Estado estado;
}