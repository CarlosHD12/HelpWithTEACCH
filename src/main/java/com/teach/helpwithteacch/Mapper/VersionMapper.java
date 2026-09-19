package com.teach.helpwithteacch.Mapper;

import com.teach.helpwithteacch.DTO.Version.*;
import com.teach.helpwithteacch.Entidades.Version;
import com.teach.helpwithteacch.Security.Config.CommonMapper;
import org.mapstruct.*;

@Mapper(componentModel = "spring", config = CommonMapper.class)
public interface VersionMapper {
    @Mapping(target = "idVersion", ignore = true)
    @Mapping(target = "prueba", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "fechaPublicacion", ignore = true)
    Version toEntity(VersionRequest request);
    @Mapping(target = "idVersion", ignore = true)
    @Mapping(target = "prueba", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "fechaPublicacion", ignore = true)
    void updateEntity(VersionEditRequest request, @MappingTarget Version version);
    @Mapping(target = "idPrueba", source = "prueba.idPrueba")
    VersionResponse toResponse(Version version);
}