package com.teach.helpwithteacch.DTO.Nino;

import com.teach.helpwithteacch.Auditoria.AuditoriaResponse;
import com.teach.helpwithteacch.Enum.Estado;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
public class NinoResponse extends AuditoriaResponse {
    private Long idNino;
    private String nombres;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private String sexo;
    private String etnia;
    private Boolean ictericia;
    private Boolean familiarConTea;
    private String fotoUrl;
    private Estado estado;
}