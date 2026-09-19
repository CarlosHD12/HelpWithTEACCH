package com.teach.helpwithteacch.Security.Mapper;

import com.teach.helpwithteacch.Security.Config.CommonMapper;
import com.teach.helpwithteacch.Security.DTO.Usuario.*;
import com.teach.helpwithteacch.Security.Entidades.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", config = CommonMapper.class)
public interface UsuarioMapper {
    @Mapping(target = "idUsuario", ignore = true)
    @Mapping(target = "rol", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "estado", ignore = true)
    Usuario toEntity(UsuarioRequest request);
    @Mapping(target = "idUsuario", ignore = true)
    @Mapping(target = "rol", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "estado", ignore = true)
    void updateEntity(UsuarioEditRequest request, @MappingTarget Usuario usuario);
    @Mapping(target = "idRol", source = "rol.idRol")
    UsuarioResponse toResponse(Usuario usuario);
}