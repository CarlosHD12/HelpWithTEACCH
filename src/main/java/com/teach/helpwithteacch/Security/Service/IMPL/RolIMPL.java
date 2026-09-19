package com.teach.helpwithteacch.Security.Service.IMPL;

import com.teach.helpwithteacch.Security.DTO.Rol.RolResponse;
import com.teach.helpwithteacch.Security.Entidades.Rol;
import com.teach.helpwithteacch.Security.Exceptions.ResourceNotFoundException;
import com.teach.helpwithteacch.Security.Mapper.RolMapper;
import com.teach.helpwithteacch.Security.Repository.RolRepos;
import com.teach.helpwithteacch.Security.Service.RolService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RolIMPL implements RolService {

    private final RolRepos rolRepos;
    private final RolMapper rolMapper;

    @Override
    @Transactional(readOnly = true)
    public RolResponse obtenerPorId(Long idRol) {
        return rolMapper.toResponse(buscarRol(idRol));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RolResponse> listar(Pageable pageable) {
        Pageable pageableOrdenado = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "idRol")
        );
        return rolRepos.findAll(pageableOrdenado)
                .map(rolMapper::toResponse);
    }

    private Rol buscarRol(Long idRol) {
        return rolRepos.findById(idRol)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el rol con ID: " + idRol
                ));
    }
}