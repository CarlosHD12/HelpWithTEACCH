package com.teach.helpwithteacch.DTO.Evaluacion;

import com.teach.helpwithteacch.Auditoria.AuditoriaResponse;
import com.teach.helpwithteacch.Enum.EstadoEvaluacion;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
public class EvaluacionResponse extends AuditoriaResponse {
    private Long idEvaluacion;
    private Long idNino;
    private Long idUsuario;
    private Long idVersion;
    private LocalDateTime fechaEvaluacion;
    private EstadoEvaluacion estado;
    private Integer itemActual;
    private Integer serieActual;
    private Integer progreso;
    private LocalDateTime fechaUltimoAcceso;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFinalizacion;
}