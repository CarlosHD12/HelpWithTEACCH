package com.teach.helpwithteacch.Mapper;

import com.teach.helpwithteacch.DTO.Asignarnino.*;
import com.teach.helpwithteacch.Entidades.AsignarNino;
import com.teach.helpwithteacch.Security.Config.CommonMapper;
import org.mapstruct.*;

@Mapper(componentModel = "spring", config = CommonMapper.class)
public interface AsignarNinoMapper {
    @Mapping(target = "idAsignarNino", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "nino", ignore = true)
    @Mapping(target = "fechaAsignacion", ignore = true)
    @Mapping(target = "estado", ignore = true)
    AsignarNino toEntity(AsignarNinoRequest request);
    @Mapping(target = "idAsignarNino", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "nino", ignore = true)
    @Mapping(target = "fechaAsignacion", ignore = true)
    @Mapping(target = "estado", ignore = true)
    void updateEntity(AsignarNinoEditRequest request, @MappingTarget AsignarNino asignarNino);
    @Mapping(target = "idUsuario", source = "usuario.idUsuario")
    @Mapping(target = "idNino", source = "nino.idNino")
    AsignarNinoResponse toResponse(AsignarNino asignarNino);
}