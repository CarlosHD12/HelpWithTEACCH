package com.teach.helpwithteacch.Security.Service.IMPL;

import com.teach.helpwithteacch.DTO.Common.CambiarEstadoRequest;
import com.teach.helpwithteacch.Enum.Estado;
import com.teach.helpwithteacch.Enum.RolNombre;
import com.teach.helpwithteacch.Security.DTO.Usuario.*;
import com.teach.helpwithteacch.Security.Entidades.Rol;
import com.teach.helpwithteacch.Security.Entidades.Usuario;
import com.teach.helpwithteacch.Security.Exceptions.*;
import com.teach.helpwithteacch.Security.Mapper.UsuarioMapper;
import com.teach.helpwithteacch.Security.Repository.*;
import com.teach.helpwithteacch.Security.Service.UsuarioService;
import com.teach.helpwithteacch.Specification.UsuarioSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioIMPL implements UsuarioService {

    private final UsuarioRepos usuarioRepos;
    private final RolRepos rolRepos;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UsuarioResponse crear(UsuarioRequest request) {
        validarCorreoDisponible(request.getEmail());
        Rol rol = buscarRolActivo(request.getIdRol());
        Usuario usuario = usuarioMapper.toEntity(request);
        usuario.setRol(rol);
        usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        usuario.setEstado(Estado.ACTIVO);
        return usuarioMapper.toResponse(usuarioRepos.save(usuario));
    }

    @Override
    public UsuarioResponse editar(Long idUsuario, UsuarioEditRequest request) {
        Usuario usuario = buscarUsuario(idUsuario);
        validarUsuarioActivo(usuario);
        validarCorreoDisponible(request.getEmail(), idUsuario);
        usuarioMapper.updateEntity(request, usuario);
        return usuarioMapper.toResponse(usuarioRepos.save(usuario));
    }

    @Override
    public UsuarioResponse cambiarRol(Long idUsuario, CambiarRolRequest request) {
        Usuario usuario = buscarUsuario(idUsuario);
        validarUsuarioActivo(usuario);
        Rol rol = buscarRolActivo(request.getIdRol());
        usuario.setRol(rol);
        return usuarioMapper.toResponse(usuarioRepos.save(usuario));
    }

    @Override
    public void cambiarEstado(CambiarEstadoRequest request) {
        request.getItems().forEach(item -> {
            Usuario usuario = buscarUsuario(item.getId());
            usuario.setEstado(item.getEstado());
        });
        usuarioRepos.flush();
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPorId(Long idUsuario) {
        return usuarioMapper.toResponse(buscarUsuario(idUsuario));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioResponse> listar(
            String nombres,
            String apellidos,
            String email,
            RolNombre rol,
            Estado estado,
            Pageable pageable
    ) {
        Pageable pageableOrdenado = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "idUsuario")
        );

        Specification<Usuario> specification =
                UsuarioSpecification.conFiltros(
                        nombres,
                        apellidos,
                        email,
                        rol,
                        estado
                );

        return usuarioRepos.findAll(
                specification,
                pageableOrdenado
        ).map(usuarioMapper::toResponse);
    }

    private Usuario buscarUsuario(Long idUsuario) {
        return usuarioRepos.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el usuario con ID: " + idUsuario
                ));
    }

    private Rol buscarRolActivo(Long idRol) {
        Rol rol = rolRepos.findById(idRol)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el rol con ID: " + idRol
                ));
        if (rol.getEstado() != Estado.ACTIVO) {
            throw new BadRequestException(
                    "El rol con ID " + idRol + " se encuentra inactivo"
            );
        }
        return rol;
    }

    private void validarUsuarioActivo(Usuario usuario) {
        if (usuario.getEstado() != Estado.ACTIVO) {
            throw new BadRequestException(
                    "El usuario con ID " + usuario.getIdUsuario() + " se encuentra inactivo"
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

    private void validarCorreoDisponible(String email, Long idUsuario) {
        if (usuarioRepos.existsByEmailAndIdUsuarioNot(email, idUsuario)) {
            throw new ConflictException(
                    "El correo electrónico ya está registrado por otro usuario"
            );
        }
    }
}