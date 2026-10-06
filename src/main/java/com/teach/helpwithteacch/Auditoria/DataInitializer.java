package com.teach.helpwithteacch.Auditoria;

import com.teach.helpwithteacch.Entidades.Prueba;
import com.teach.helpwithteacch.Entidades.Version;
import com.teach.helpwithteacch.Enum.Estado;
import com.teach.helpwithteacch.Enum.RolNombre;
import com.teach.helpwithteacch.Enum.TipoPrueba;
import com.teach.helpwithteacch.Repository.PruebaRepos;
import com.teach.helpwithteacch.Repository.VersionRepos;
import com.teach.helpwithteacch.Security.Entidades.*;
import com.teach.helpwithteacch.Security.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final RolRepos rolRepos;
    private final UsuarioRepos usuarioRepos;
    private final PruebaRepos pruebaRepos;
    private final VersionRepos versionRepos;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        Rol rolAdmin = crearRolSiNoExiste(RolNombre.ADMIN, "Administrador del sistema");
        crearRolSiNoExiste(RolNombre.DOCENTE, "Docente");
        crearRolSiNoExiste(RolNombre.PADRE, "Padre o madre de familia");
        crearUsuarioAdminSiNoExiste(rolAdmin);

        Prueba qchat = crearPruebaSiNoExiste("Q-CHAT", TipoPrueba.QCHAT, "Cuestionario de detección de características relacionadas con TEA");
        Prueba kabc = crearPruebaSiNoExiste("K-ABC", TipoPrueba.KABC, "Evaluación de habilidades cognitivas");

        crearVersionSiNoExiste(qchat, "1.0", "Versión 1.0 del Q-CHAT");
        crearVersionSiNoExiste(kabc, "1.0", "Versión 1.0 del K-ABC");
        crearVersionSiNoExiste(kabc, "2.0", "Versión 2.0 del K-ABC");
    }

    private Rol crearRolSiNoExiste(RolNombre nombre, String descripcion) {
        return rolRepos.findByNombre(nombre).orElseGet(() -> {
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
        usuario.setPasswordHash(passwordEncoder.encode("Admin12345"));
        usuario.setEstado(Estado.ACTIVO);
        usuarioRepos.save(usuario);
    }

    private Prueba crearPruebaSiNoExiste(String nombre, TipoPrueba tipo, String descripcion) {
        return pruebaRepos.findByTipo(tipo).orElseGet(() -> {
            Prueba prueba = new Prueba();
            prueba.setNombre(nombre);
            prueba.setTipo(tipo);
            prueba.setDescripcion(descripcion);
            prueba.setEstado(Estado.ACTIVO);
            return pruebaRepos.save(prueba);
        });
    }

    private void crearVersionSiNoExiste(Prueba prueba, String numeroVersion, String descripcion) {
        versionRepos.findByPrueba_IdPruebaAndNumeroVersion(prueba.getIdPrueba(), numeroVersion).orElseGet(() -> {
            Version version = new Version();
            version.setPrueba(prueba);
            version.setNumeroVersion(numeroVersion);
            version.setDescripcion(descripcion);
            version.setEstado(Estado.ACTIVO);
            version.setFechaPublicacion(LocalDateTime.now());
            return versionRepos.save(version);
        });
    }
}