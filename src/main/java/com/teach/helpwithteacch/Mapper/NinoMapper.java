package com.teach.helpwithteacch.Mapper;

import com.teach.helpwithteacch.DTO.Nino.*;
import com.teach.helpwithteacch.Entidades.Nino;
import com.teach.helpwithteacch.Security.Config.CommonMapper;
import org.mapstruct.*;

@Mapper(componentModel = "spring", config = CommonMapper.class)
public interface NinoMapper {
    @Mapping(target = "idNino", ignore = true)
    @Mapping(target = "estado", ignore = true)
    Nino toEntity(NinoRequest request);
    @Mapping(target = "idNino", ignore = true)
    @Mapping(target = "estado", ignore = true)
    void updateEntity(NinoEditRequest request, @MappingTarget Nino nino);
    NinoResponse toResponse(Nino nino);
}