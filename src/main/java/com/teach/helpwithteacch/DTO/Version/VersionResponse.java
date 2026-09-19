package com.teach.helpwithteacch.DTO.Version;

import com.teach.helpwithteacch.Auditoria.AuditoriaResponse;
import com.teach.helpwithteacch.Enum.Estado;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
public class VersionResponse extends AuditoriaResponse {
    private Long idVersion;
    private Long idPrueba;
    private String numeroVersion;
    private String descripcion;
    private Estado estado;
    private LocalDateTime fechaPublicacion;
}