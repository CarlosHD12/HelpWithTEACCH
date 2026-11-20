package com.teach.helpwithteacch.Security.Service.IMPL;

import com.teach.helpwithteacch.Enum.Estado;
import com.teach.helpwithteacch.Enum.RolNombre;
import com.teach.helpwithteacch.Security.DTO.Auth.CambiarPasswordRequest;
import com.teach.helpwithteacch.Security.DTO.Auth.LoginResponse;
import com.teach.helpwithteacch.Security.DTO.Perfil.PerfilRequest;
import com.teach.helpwithteacch.Security.DTO.Perfil.PerfilResponse;
import com.teach.helpwithteacch.Security.Entidades.Rol;
import com.teach.helpwithteacch.Security.Entidades.Usuario;
import com.teach.helpwithteacch.Security.Exceptions.BadRequestException;
import com.teach.helpwithteacch.Security.Exceptions.ConflictException;
import com.teach.helpwithteacch.Security.Exceptions.ResourceNotFoundException;
import com.teach.helpwithteacch.Security.Jwt.JwtService;
import com.teach.helpwithteacch.Security.Mapper.UsuarioMapper;
import com.teach.helpwithteacch.Security.Repository.RolRepos;
import com.teach.helpwithteacch.Security.Repository.UsuarioRepos;
import com.teach.helpwithteacch.Security.Service.PerfilService;
import com.teach.helpwithteacch.Security.User.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PerfilIMPL implements PerfilService {

    private final UsuarioRepos usuarioRepos;
    private final RolRepos rolRepos;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    // =========================================================
    // VER PERFIL
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public PerfilResponse obtenerPerfil(String emailAutenticado) {

        Usuario usuario = buscarUsuarioAutenticado(emailAutenticado);

        return toPerfilResponse(usuario);
    }

    // =========================================================
    // ACTUALIZAR PERFIL (datos personales y ocupación)
    // =========================================================

    @Override
    public LoginResponse actualizarPerfil(
            String emailAutenticado,
            PerfilRequest request
    ) {

        Usuario usuario = buscarUsuarioAutenticado(emailAutenticado);

        validarUsuarioActivo(usuario);

        String emailNuevo = request.getEmail().trim();

        if (usuarioRepos.existsByEmailAndIdUsuarioNot(emailNuevo, usuario.getIdUsuario())) {
            throw new ConflictException(
                    "El correo electrónico ya está registrado"
            );
        }

        usuario.setNombres(request.getNombres().trim());
        usuario.setApellidos(request.getApellidos().trim());
        usuario.setEmail(emailNuevo);

        aplicarOcupacion(usuario, request.getOcupacion());

        Usuario guardado = usuarioRepos.save(usuario);

        // El token lleva el correo como "subject": si el correo cambió,
        // el token anterior deja de servir, así que se emite uno nuevo.
        UserDetails userDetails =
                userDetailsService.loadUserByUsername(guardado.getEmail());

        String token = jwtService.generateToken(userDetails);

        return new LoginResponse(
                token,
                "Bearer",
                usuarioMapper.toResponse(guardado),
                guardado.getRol().getNombre().name()
        );
    }

    // =========================================================
    // CAMBIAR CONTRASEÑA (solo la propia)
    // =========================================================

    @Override
    public void cambiarPassword(
            String emailAutenticado,
            CambiarPasswordRequest request
    ) {

        Usuario usuario = buscarUsuarioAutenticado(emailAutenticado);

        validarUsuarioActivo(usuario);

        if (!passwordEncoder.matches(
                request.getPasswordActual(),
                usuario.getPasswordHash()
        )) {
            throw new BadRequestException(
                    "La contraseña actual es incorrecta"
            );
        }

        if (!request.getPasswordNueva().equals(request.getConfirmarPassword())) {
            throw new BadRequestException(
                    "Las contraseñas nuevas no coinciden"
            );
        }

        if (passwordEncoder.matches(
                request.getPasswordNueva(),
                usuario.getPasswordHash()
        )) {
            throw new BadRequestException(
                    "La nueva contraseña debe ser diferente a la actual"
            );
        }

        usuario.setPasswordHash(
                passwordEncoder.encode(request.getPasswordNueva())
        );

        usuarioRepos.save(usuario);
    }

    // =========================================================
    // AUXILIARES
    // =========================================================

    /**
     * Cambia el rol solo entre PADRE y DOCENTE.
     * Un ADMIN conserva su rol: desde el perfil nadie se vuelve
     * administrador ni deja de serlo.
     */
    private void aplicarOcupacion(Usuario usuario, String ocupacion) {

        if (ocupacion == null || ocupacion.isBlank()) {
            return;
        }

        RolNombre rolActual = usuario.getRol().getNombre();

        if (rolActual == RolNombre.ADMIN) {
            return;
        }

        RolNombre rolNuevo;

        try {
            rolNuevo = RolNombre.valueOf(ocupacion.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(
                    "La ocupación debe ser PADRE o DOCENTE"
            );
        }

        if (rolNuevo != RolNombre.PADRE && rolNuevo != RolNombre.DOCENTE) {
            throw new BadRequestException(
                    "La ocupación debe ser PADRE o DOCENTE"
            );
        }

        if (rolNuevo == rolActual) {
            return;
        }

        Rol rol = rolRepos.findByNombre(rolNuevo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el rol " + rolNuevo
                ));

        if (rol.getEstado() != Estado.ACTIVO) {
            throw new BadRequestException(
                    "El rol seleccionado se encuentra inactivo"
            );
        }

        usuario.setRol(rol);
    }

    private Usuario buscarUsuarioAutenticado(String email) {
        return usuarioRepos.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el usuario autenticado"
                ));
    }

    private void validarUsuarioActivo(Usuario usuario) {
        if (usuario.getEstado() != Estado.ACTIVO) {
            throw new BadRequestException(
                    "El usuario se encuentra inactivo"
            );
        }
    }

    private PerfilResponse toPerfilResponse(Usuario usuario) {
        return new PerfilResponse(
                usuario.getIdUsuario(),
                usuario.getNombres(),
                usuario.getApellidos(),
                usuario.getEmail(),
                usuario.getRol().getNombre().name(),
                usuario.getEstado()
        );
    }
}
