package com.teach.helpwithteacch.Security.User;

import com.teach.helpwithteacch.Enum.Estado;
import com.teach.helpwithteacch.Security.Entidades.Usuario;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUserDetails extends User {

    private final String nombreCompleto;

    public CustomUserDetails(
            Usuario usuario,
            Collection<? extends GrantedAuthority> authorities
    ) {
        super(
                usuario.getEmail(),
                usuario.getPasswordHash(),
                usuario.getEstado() == Estado.ACTIVO,
                true,
                true,
                true,
                authorities
        );

        this.nombreCompleto =
                usuario.getNombres() + " " + usuario.getApellidos();
    }
}