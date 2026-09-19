package com.teach.helpwithteacch.DTO.Prueba;

import com.teach.helpwithteacch.Auditoria.AuditoriaResponse;
import com.teach.helpwithteacch.Enum.Estado;
import com.teach.helpwithteacch.Enum.TipoPrueba;
import lombok.*;

@Getter
@Setter
public class PruebaResponse extends AuditoriaResponse {
    private Long idPrueba;
    private String nombre;
    private TipoPrueba tipo;
    private String descripcion;
    private Estado estado;
}