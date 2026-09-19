package com.teach.helpwithteacch.Security.DTO.Auth;

import com.teach.helpwithteacch.Security.DTO.Usuario.UsuarioResponse;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String tipo;
    private UsuarioResponse usuario;
    private String rol;
}