package com.teach.helpwithteacch.Security.Config;

import com.teach.helpwithteacch.Enum.Estado;
import com.teach.helpwithteacch.Enum.RolNombre;
import com.teach.helpwithteacch.Security.Entidades.*;
import com.teach.helpwithteacch.Security.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RolRepos rolRepos;
    private final UsuarioRepos usuarioRepos;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        Rol rolAdmin = crearRolSiNoExiste(RolNombre.ADMIN, "Administrador del sistema");
        crearRolSiNoExiste(RolNombre.DOCENTE, "Docente");
        crearRolSiNoExiste(RolNombre.PADRE, "Padre o madre de familia");
        crearUsuarioAdminSiNoExiste(rolAdmin);
    }

    private Rol crearRolSiNoExiste(
            RolNombre nombre,
            String descripcion
    ) {
        return rolRepos.findByNombre(nombre)
                .orElseGet(() -> {
                    Rol rol = new Rol();
                    rol.setNombre(nombre);
                    rol.setDescripcion(descripcion);
                    rol.setEstado(Estado.ACTIVO);

                    return rolRepos.save(rol);
                });
    }

    private void crearUsuarioAdminSiNoExiste(Rol rolAdmin) {
        if (usuarioRepos.existsByEmail("admin@helpwithteacch.com")) {
            return;
        }

        Usuario usuario = new Usuario();

        usuario.setRol(rolAdmin);
        usuario.setNombres("Administrador");
        usuario.setApellidos("Sistema");
        usuario.setEmail("admin@helpwithteacch.com");
        usuario.setPasswordHash(
                passwordEncoder.encode("Admin12345")
        );
        usuario.setEstado(Estado.ACTIVO);

        usuarioRepos.save(usuario);
    }
}