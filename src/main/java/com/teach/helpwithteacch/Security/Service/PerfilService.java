package com.teach.helpwithteacch.Security.Service;

import com.teach.helpwithteacch.Security.DTO.Auth.CambiarPasswordRequest;
import com.teach.helpwithteacch.Security.DTO.Auth.LoginResponse;
import com.teach.helpwithteacch.Security.DTO.Perfil.PerfilRequest;
import com.teach.helpwithteacch.Security.DTO.Perfil.PerfilResponse;

/**
 * Operaciones del usuario autenticado sobre su propia cuenta.
 */
public interface PerfilService {

    PerfilResponse obtenerPerfil(String emailAutenticado);

    LoginResponse actualizarPerfil(String emailAutenticado, PerfilRequest request);

    void cambiarPassword(String emailAutenticado, CambiarPasswordRequest request);
}
