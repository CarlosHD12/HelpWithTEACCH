package com.teach.helpwithteacch.DTO.Resultado;

import com.teach.helpwithteacch.Auditoria.AuditoriaResponse;
import com.teach.helpwithteacch.Enum.TipoSubtest;
import lombok.*;

@Getter
@Setter
public class ResultadoSubtestResponse extends AuditoriaResponse {
    private Long idResultadoSubtest;
    private Long idResultado;
    private TipoSubtest tipo;
    private Integer puntaje;
}