package com.teach.helpwithteacch.Mapper;

import com.teach.helpwithteacch.DTO.PrediccionML.*;
import com.teach.helpwithteacch.Entidades.PrediccionML;
import com.teach.helpwithteacch.Security.Config.CommonMapper;
import org.mapstruct.*;

@Mapper(componentModel = "spring", config = CommonMapper.class)
public interface PrediccionMLMapper {
    @Mapping(target = "idPrediccion", ignore = true)
    @Mapping(target = "evaluacion", ignore = true)
    @Mapping(target = "fechaPrediccion", ignore = true)
    PrediccionML toEntity(PrediccionMLResponse response);
    @Mapping(target = "idPrediccion", ignore = true)
    @Mapping(target = "evaluacion", ignore = true)
    @Mapping(target = "fechaPrediccion", ignore = true)
    void updateEntity(PrediccionMLResponse response, @MappingTarget PrediccionML prediccionML);
    @Mapping(target = "idEvaluacion", source = "evaluacion.idEvaluacion")
    PrediccionMLResponse toResponse(PrediccionML prediccionML);
}