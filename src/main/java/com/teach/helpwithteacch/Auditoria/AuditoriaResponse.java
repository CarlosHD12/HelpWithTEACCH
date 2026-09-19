package com.teach.helpwithteacch.Auditoria;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
public class AuditoriaResponse {
    private String creadoPor;
    private LocalDateTime fechaCreacion;
    private String modificadoPor;
    private LocalDateTime fechaModificacion;
    private Long versionLock;
}