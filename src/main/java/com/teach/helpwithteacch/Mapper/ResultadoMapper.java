package com.teach.helpwithteacch.Mapper;

import com.teach.helpwithteacch.DTO.Resultado.*;
import com.teach.helpwithteacch.Entidades.Resultado;
import com.teach.helpwithteacch.Security.Config.CommonMapper;
import org.mapstruct.*;

@Mapper(componentModel = "spring", config = CommonMapper.class)
public interface ResultadoMapper {
    @Mapping(target = "idResultado", ignore = true)
    @Mapping(target = "evaluacion", ignore = true)
    @Mapping(target = "puntajeTotal", ignore = true)
    @Mapping(target = "fechaResultado", ignore = true)
    Resultado toEntity(ResultadoResponse response);
    @Mapping(target = "idResultado", ignore = true)
    @Mapping(target = "evaluacion", ignore = true)
    @Mapping(target = "puntajeTotal", ignore = true)
    @Mapping(target = "fechaResultado", ignore = true)
    void updateEntity(ResultadoResponse response, @MappingTarget Resultado resultado);
    @Mapping(target = "idEvaluacion", source = "evaluacion.idEvaluacion")
    ResultadoResponse toResponse(Resultado resultado);
}