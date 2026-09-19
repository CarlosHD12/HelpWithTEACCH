package com.teach.helpwithteacch.Mapper;

import com.teach.helpwithteacch.DTO.Prueba.*;
import com.teach.helpwithteacch.Entidades.Prueba;
import com.teach.helpwithteacch.Security.Config.CommonMapper;
import org.mapstruct.*;

@Mapper(componentModel = "spring", config = CommonMapper.class)
public interface PruebaMapper {
    @Mapping(target = "idPrueba", ignore = true)
    @Mapping(target = "estado", ignore = true)
    Prueba toEntity(PruebaRequest request);
    @Mapping(target = "idPrueba", ignore = true)
    @Mapping(target = "estado", ignore = true)
    void updateEntity(PruebaEditRequest request, @MappingTarget Prueba prueba);
    PruebaResponse toResponse(Prueba prueba);
}