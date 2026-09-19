package com.teach.helpwithteacch.DTO.PrediccionML;

import com.teach.helpwithteacch.Auditoria.AuditoriaResponse;
import com.teach.helpwithteacch.Enum.ModeloML;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PrediccionMLResponse extends AuditoriaResponse {
    private Long idPrediccion;
    private Long idEvaluacion;
    private ModeloML modelo;
    private String resultado;
    private BigDecimal probabilidad;
    private LocalDateTime fechaPrediccion;
}