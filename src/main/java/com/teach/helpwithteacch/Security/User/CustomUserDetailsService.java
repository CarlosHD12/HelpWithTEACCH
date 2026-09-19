package com.teach.helpwithteacch.Security.User;

import com.teach.helpwithteacch.Security.Entidades.Usuario;
import com.teach.helpwithteacch.Security.Repository.UsuarioRepos;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepos usuarioRepos;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioRepos.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuario no encontrado"
                        )
                );

        return new CustomUserDetails(
                usuario,
                obtenerAutoridades(usuario)
        );
    }

    private List<GrantedAuthority> obtenerAutoridades(Usuario usuario) {
        return List.of(
                new SimpleGrantedAuthority(
                        "ROLE_" + usuario.getRol().getNombre().name()
                )
        );
    }
}