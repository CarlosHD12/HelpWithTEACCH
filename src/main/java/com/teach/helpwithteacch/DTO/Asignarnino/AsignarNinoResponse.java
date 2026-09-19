package com.teach.helpwithteacch.DTO.Asignarnino;

import com.teach.helpwithteacch.Auditoria.AuditoriaResponse;
import com.teach.helpwithteacch.Enum.Estado;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
public class AsignarNinoResponse extends AuditoriaResponse {
    private Long idAsignarNino;
    private Long idUsuario;
    private Long idNino;
    private LocalDateTime fechaAsignacion;
    private Estado estado;
}