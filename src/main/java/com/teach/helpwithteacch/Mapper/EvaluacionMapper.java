package com.teach.helpwithteacch.Mapper;

import com.teach.helpwithteacch.DTO.Evaluacion.*;
import com.teach.helpwithteacch.Entidades.Evaluacion;
import com.teach.helpwithteacch.Security.Config.CommonMapper;
import org.mapstruct.*;

@Mapper(componentModel = "spring", config = CommonMapper.class)
public interface EvaluacionMapper {
    @Mapping(target = "idEvaluacion", ignore = true)
    @Mapping(target = "nino", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "fechaEvaluacion", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "itemActual", ignore = true)
    @Mapping(target = "serieActual", ignore = true)
    @Mapping(target = "progreso", ignore = true)
    @Mapping(target = "fechaUltimoAcceso", ignore = true)
    @Mapping(target = "fechaInicio", ignore = true)
    @Mapping(target = "fechaFinalizacion", ignore = true)
    Evaluacion toEntity(EvaluacionRequest request);
    @Mapping(target = "idEvaluacion", ignore = true)
    @Mapping(target = "nino", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "fechaEvaluacion", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "itemActual", ignore = true)
    @Mapping(target = "serieActual", ignore = true)
    @Mapping(target = "progreso", ignore = true)
    @Mapping(target = "fechaUltimoAcceso", ignore = true)
    @Mapping(target = "fechaInicio", ignore = true)
    @Mapping(target = "fechaFinalizacion", ignore = true)
    void updateEntity(EvaluacionEditRequest request, @MappingTarget Evaluacion evaluacion);
    @Mapping(target = "idNino", source = "nino.idNino")
    @Mapping(target = "idUsuario", source = "usuario.idUsuario")
    @Mapping(target = "idVersion", source = "version.idVersion")
    EvaluacionResponse toResponse(Evaluacion evaluacion);
}