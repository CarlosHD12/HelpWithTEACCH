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


    // =========================================================
    // LOGIN
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        Usuario usuario =
                buscarUsuarioPorEmail(request.getEmail());

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
                userDetailsService.loadUserByUsername(
                        usuario.getEmail()
                );

        String token =
                jwtService.generateToken(userDetails);

        return construirLoginResponse(
                token,
                usuario
        );
    }


    // =========================================================
    // REGISTRO
    // =========================================================

    @Override
    public LoginResponse registrar(
            RegistroRequest request
    ) {

        // 1. Verificar que el correo no exista
        validarCorreoDisponible(
                request.getEmail()
        );


        // 2. Verificar que las contraseñas coincidan
        validarConfirmacionPassword(
                request
        );


        // 3. Buscar el rol seleccionado
        Rol rol =
                buscarRolRegistro(
                        request.getIdRol()
                );


        // 4. Verificar que el rol esté activo
        validarRolActivo(rol);


        // 5. Crear usuario
        Usuario usuario = new Usuario();

        usuario.setNombres(
                request.getNombres()
        );

        usuario.setApellidos(
                request.getApellidos()
        );

        usuario.setEmail(
                request.getEmail()
        );

        usuario.setPasswordHash(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        usuario.setRol(rol);

        usuario.setEstado(
                Estado.ACTIVO
        );


        // 6. Guardar usuario
        Usuario usuarioGuardado =
                usuarioRepos.save(usuario);


        // 7. Crear UserDetails
        UserDetails userDetails =
                userDetailsService.loadUserByUsername(
                        usuarioGuardado.getEmail()
                );


        // 8. Generar JWT
        String token =
                jwtService.generateToken(
                        userDetails
                );


        // 9. Devolver respuesta
        return construirLoginResponse(
                token,
                usuarioGuardado
        );
    }


    // =========================================================
    // CAMBIAR PASSWORD
    // =========================================================

    @Override
    public void cambiarPassword(
            Long idUsuario,
            CambiarPasswordRequest request
    ) {

        Usuario usuario =
                buscarUsuario(idUsuario);

        validarUsuarioActivo(usuario);


        if (!passwordEncoder.matches(
                request.getPasswordActual(),
                usuario.getPasswordHash()
        )) {

            throw new BadRequestException(
                    "La contraseña actual es incorrecta"
            );

        }


        validarConfirmacionPassword(
                request
        );


        if (passwordEncoder.matches(
                request.getPasswordNueva(),
                usuario.getPasswordHash()
        )) {

            throw new BadRequestException(
                    "La nueva contraseña debe ser diferente a la actual"
            );

        }


        usuario.setPasswordHash(
                passwordEncoder.encode(
                        request.getPasswordNueva()
                )
        );


        usuarioRepos.save(usuario);
    }


    // =========================================================
    // BUSCAR USUARIO POR ID
    // =========================================================

    private Usuario buscarUsuario(
            Long idUsuario
    ) {

        return usuarioRepos.findById(idUsuario)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No se encontró el usuario con ID: "
                                        + idUsuario
                        )
                );
    }


    // =========================================================
    // BUSCAR USUARIO POR EMAIL
    // =========================================================

    private Usuario buscarUsuarioPorEmail(
            String email
    ) {

        return usuarioRepos.findByEmail(email)
                .orElseThrow(() ->
                        new BadRequestException(
                                "Correo o contraseña incorrectos"
                        )
                );
    }


    // =========================================================
    // BUSCAR ROL PARA REGISTRO
    // =========================================================

    private Rol buscarRolRegistro(
            Long idRol
    ) {

        Rol rol =
                rolRepos.findById(idRol)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No se encontró el rol con ID: "
                                                + idRol
                                )
                        );


        /*
         * Por seguridad, el registro público
         * solamente permite DOCENTE y PADRE.
         *
         * ADMIN no puede registrarse desde
         * este formulario.
         */

        if (
                rol.getNombre() != RolNombre.DOCENTE
                        &&
                rol.getNombre() != RolNombre.PADRE
        ) {

            throw new BadRequestException(
                    "El rol seleccionado no está permitido para el registro"
            );

        }


        return rol;
    }


    // =========================================================
    // VALIDAR USUARIO ACTIVO
    // =========================================================

    private void validarUsuarioActivo(
            Usuario usuario
    ) {

        if (
                usuario.getEstado()
                        != Estado.ACTIVO
        ) {

            throw new BadRequestException(
                    "El usuario se encuentra inactivo"
            );

        }
    }


    // =========================================================
    // VALIDAR ROL ACTIVO
    // =========================================================

    private void validarRolActivo(
            Rol rol
    ) {

        if (
                rol.getEstado()
                        != Estado.ACTIVO
        ) {

            throw new BadRequestException(
                    "El rol seleccionado se encuentra inactivo"
            );

        }
    }


    // =========================================================
    // VALIDAR EMAIL
    // =========================================================

    private void validarCorreoDisponible(
            String email
    ) {

        if (
                usuarioRepos.existsByEmail(email)
        ) {

            throw new ConflictException(
                    "El correo electrónico ya está registrado"
            );

        }
    }


    // =========================================================
    // VALIDAR PASSWORD REGISTRO
    // =========================================================

    private void validarConfirmacionPassword(
            RegistroRequest request
    ) {

        if (
                !request.getPassword()
                        .equals(
                                request.getConfirmarPassword()
                        )
        ) {

            throw new BadRequestException(
                    "Las contraseñas no coinciden"
            );

        }
    }


    // =========================================================
    // VALIDAR PASSWORD CAMBIO
    // =========================================================

    private void validarConfirmacionPassword(
            CambiarPasswordRequest request
    ) {

        if (
                !request.getPasswordNueva()
                        .equals(
                                request.getConfirmarPassword()
                        )
        ) {

            throw new BadRequestException(
                    "Las contraseñas nuevas no coinciden"
            );

        }
    }


    // =========================================================
    // LOGIN RESPONSE
    // =========================================================

    private LoginResponse construirLoginResponse(
            String token,
            Usuario usuario
    ) {

        return new LoginResponse(
                token,
                "Bearer",
                usuarioMapper.toResponse(usuario),
                usuario.getRol()
                        .getNombre()
                        .name()
        );
    }
}