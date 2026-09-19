package com.teach.helpwithteacch.Security.Service;

import com.teach.helpwithteacch.Security.DTO.Auth.*;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    LoginResponse registrar(RegistroRequest request);
    void cambiarPassword(Long idUsuario, CambiarPasswordRequest request);
}