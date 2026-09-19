package com.teach.helpwithteacch.DTO.PrediccionML;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
public class ModeloMLResponse {
    private Integer prediction;
    private String resultado;
    private BigDecimal probabilidad_asd;
}