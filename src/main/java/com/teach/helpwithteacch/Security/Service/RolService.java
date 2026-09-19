package com.teach.helpwithteacch.Security.Service;

import com.teach.helpwithteacch.Security.DTO.Rol.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RolService {
    RolResponse obtenerPorId(Long idRol);
    Page<RolResponse> listar(Pageable pageable);
}
