package com.teach.helpwithteacch.DTO.Respuesta;

import com.teach.helpwithteacch.Auditoria.AuditoriaResponse;
import lombok.*;

@Getter
@Setter
public class RespuestaResponse extends AuditoriaResponse {
    private Long idRespuesta;
    private Long idEvaluacion;
    private Integer itemId;
    private Integer serieId;
    private String tipo;
    private Object valor;
    private Boolean correcta;
    private Integer puntaje;
}