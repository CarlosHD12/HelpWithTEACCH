package com.teach.helpwithteacch.Security.Service.IMPL;

import com.teach.helpwithteacch.Enum.Estado;
import com.teach.helpwithteacch.Enum.RolNombre;
import com.teach.helpwithteacch.Security.DTO.Auth.*;
import com.teach.helpwithteacch.Security.Entidades.Rol;
import com.teach.helpwithteacch.Security.Entidades.Usuario;
import com.teach.helpwithteacch.Security.Exceptions.*;
import com.teach.helpwithteacch.Security.Jwt.JwtService;
import com.teach.helpwithteacch.Security.Mapper.UsuarioMapper;
import com.teach.helpwithteacch.Security.Repository.RolRepos;
import com.teach.helpwithteacch.Security.Repository.UsuarioRepos;
import com.teach.helpwithteacch.Security.Service.AuthService;
import com.teach.helpwithteacch.Security.User.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthIMPL implements AuthService {

    private final UsuarioRepos usuarioRepos;
    private final RolRepos rolRepos;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = buscarUsuarioPorEmail(request.getEmail());

        validarUsuarioActivo(usuario);

        if (!passwordEncoder.matches(
                request.getPassword(),
                usuario.getPasswordHash()
        )) {
            throw new BadRequestException(
                    "Correo o contraseña incorrectos"
            );
        }

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(usuario.getEmail());

        String token = jwtService.generateToken(userDetails);

        return construirLoginResponse(token, usuario);
    }

    @Override
    public LoginResponse registrar(RegistroRequest request) {
        validarCorreoDisponible(request.getEmail());
        validarConfirmacionPassword(request);

        Rol rol = buscarRolPadre();

        validarRolActivo(rol);

        Usuario usuario = new Usuario();

        usuario.setNombres(request.getNombres());
        usuario.setApellidos(request.getApellidos());
        usuario.setEmail(request.getEmail());
        usuario.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );
        usuario.setRol(rol);
        usuario.setEstado(Estado.ACTIVO);

        Usuario usuarioGuardado = usuarioRepos.save(usuario);

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(
                        usuarioGuardado.getEmail()
                );

        String token = jwtService.generateToken(userDetails);

        return construirLoginResponse(token, usuarioGuardado);
    }

    @Override
    public void cambiarPassword(
            Long idUsuario,
            CambiarPasswordRequest request
    ) {
        Usuario usuario = buscarUsuario(idUsuario);

        validarUsuarioActivo(usuario);

        if (!passwordEncoder.matches(
                request.getPasswordActual(),
                usuario.getPasswordHash()
        )) {
            throw new BadRequestException(
                    "La contraseña actual es incorrecta"
            );
        }

        validarConfirmacionPassword(request);

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

    private Usuario buscarUsuario(Long idUsuario) {
        return usuarioRepos.findById(idUsuario)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No se encontró el usuario con ID: " + idUsuario
                        )
                );
    }

    private Usuario buscarUsuarioPorEmail(String email) {
        return usuarioRepos.findByEmail(email)
                .orElseThrow(() ->
                        new BadRequestException(
                                "Correo o contraseña incorrectos"
                        )
                );
    }

    private Rol buscarRolPadre() {
        return rolRepos.findByNombre(RolNombre.PADRE)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No se encontró el rol PADRE"
                        )
                );
    }

    private void validarUsuarioActivo(Usuario usuario) {
        if (usuario.getEstado() != Estado.ACTIVO) {
            throw new BadRequestException(
                    "El usuario se encuentra inactivo"
            );
        }
    }

    private void validarRolActivo(Rol rol) {
        if (rol.getEstado() != Estado.ACTIVO) {
            throw new BadRequestException(
                    "El rol seleccionado se encuentra inactivo"
            );
        }
    }

    private void validarCorreoDisponible(String email) {
        if (usuarioRepos.existsByEmail(email)) {
            throw new ConflictException(
                    "El correo electrónico ya está registrado"
            );
        }
    }

    private void validarConfirmacionPassword(
            RegistroRequest request
    ) {
        if (!request.getPassword().equals(
                request.getConfirmarPassword()
        )) {
            throw new BadRequestException(
                    "Las contraseñas no coinciden"
            );
        }
    }

    private void validarConfirmacionPassword(
            CambiarPasswordRequest request
    ) {
        if (!request.getPasswordNueva().equals(
                request.getConfirmarPassword()
        )) {
            throw new BadRequestException(
                    "Las contraseñas nuevas no coinciden"
            );
        }
    }

    private LoginResponse construirLoginResponse(
            String token,
            Usuario usuario
    ) {
        return new LoginResponse(
                token,
                "Bearer",
                usuarioMapper.toResponse(usuario),
                usuario.getRol().getNombre().name()
        );
    }
}