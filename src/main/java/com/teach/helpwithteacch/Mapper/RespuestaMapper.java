package com.teach.helpwithteacch.Mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teach.helpwithteacch.DTO.Respuesta.*;
import com.teach.helpwithteacch.Entidades.Respuesta;
import com.teach.helpwithteacch.Security.Config.CommonMapper;
import org.mapstruct.*;

@Mapper(componentModel = "spring", config = CommonMapper.class)
public interface RespuestaMapper {
    @Mapping(target = "idRespuesta", ignore = true)
    @Mapping(target = "evaluacion", ignore = true)
    @Mapping(target = "correcta", ignore = true)
    @Mapping(target = "puntaje", ignore = true)
    Respuesta toEntity(RespuestaItemRequest request);
    @Mapping(target = "idRespuesta", ignore = true)
    @Mapping(target = "evaluacion", ignore = true)
    @Mapping(target = "correcta", ignore = true)
    @Mapping(target = "puntaje", ignore = true)
    void updateEntity(RespuestaItemRequest request, @MappingTarget Respuesta respuesta);
    @Mapping(target = "idEvaluacion", source = "evaluacion.idEvaluacion")
    RespuestaResponse toResponse(Respuesta respuesta);
    default JsonNode map(Object value) {
        return new ObjectMapper().valueToTree(value);
    }
}