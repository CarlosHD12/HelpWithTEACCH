package com.teach.helpwithteacch.DTO.Resultado;

import com.teach.helpwithteacch.Auditoria.AuditoriaResponse;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
public class ResultadoResponse extends AuditoriaResponse {
    private Long idResultado;
    private Long idEvaluacion;
    private Integer puntajeTotal;
    private LocalDateTime fechaResultado;
}