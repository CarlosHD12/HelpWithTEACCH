package com.teach.helpwithteacch.Security.Mapper;

import com.teach.helpwithteacch.Security.Config.CommonMapper;
import com.teach.helpwithteacch.Security.DTO.Rol.RolResponse;
import com.teach.helpwithteacch.Security.Entidades.Rol;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", config = CommonMapper.class)
public interface RolMapper {
    RolResponse toResponse(Rol rol);
}