package com.teach.helpwithteacch.Security.Service;

import com.teach.helpwithteacch.DTO.Common.CambiarEstadoRequest;
import com.teach.helpwithteacch.Enum.Estado;
import com.teach.helpwithteacch.Enum.RolNombre;
import com.teach.helpwithteacch.Security.DTO.Usuario.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuarioService {
    UsuarioResponse crear(UsuarioRequest request);
    UsuarioResponse editar(Long idUsuario, UsuarioEditRequest request);
    UsuarioResponse cambiarRol(Long idUsuario, CambiarRolRequest request);
    void cambiarEstado(CambiarEstadoRequest request);
    UsuarioResponse obtenerPorId(Long idUsuario);
    Page<UsuarioResponse> listar(
            String nombres,
            String apellidos,
            String email,
            RolNombre rol,
            Estado estado,
            Pageable pageable
    );
}