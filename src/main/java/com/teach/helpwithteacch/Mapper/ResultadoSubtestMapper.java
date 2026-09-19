package com.teach.helpwithteacch.Mapper;

import com.teach.helpwithteacch.DTO.Resultado.*;
import com.teach.helpwithteacch.Entidades.ResultadoSubtest;
import com.teach.helpwithteacch.Security.Config.CommonMapper;
import org.mapstruct.*;

@Mapper(componentModel = "spring", config = CommonMapper.class)
public interface ResultadoSubtestMapper {
    @Mapping(target = "idResultado", source = "resultado.idResultado")
    ResultadoSubtestResponse toResponse(ResultadoSubtest resultadoSubtest);
}