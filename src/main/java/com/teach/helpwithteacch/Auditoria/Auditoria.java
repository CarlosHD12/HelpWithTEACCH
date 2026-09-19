package com.teach.helpwithteacch.Auditoria;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditoriaListener.class)
@Getter
@Setter
public abstract class Auditoria {
    @Column(name = "creado_por", nullable = false, updatable = false, length = 150)
    private String creadoPor;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "modificado_por", length = 150)
    private String modificadoPor;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @Version
    @Column(name = "version", nullable = false)
    private Long versionLock;
}