package com.teach.helpwithteacch.Services.IMPL;

import com.teach.helpwithteacch.DTO.Asignarnino.*;
import com.teach.helpwithteacch.DTO.Common.CambiarEstadoRequest;
import com.teach.helpwithteacch.Entidades.AsignarNino;
import com.teach.helpwithteacch.Entidades.Nino;
import com.teach.helpwithteacch.Enum.Estado;
import com.teach.helpwithteacch.Mapper.AsignarNinoMapper;
import com.teach.helpwithteacch.Repository.AsignarNinoRepos;
import com.teach.helpwithteacch.Repository.NinoRepos;
import com.teach.helpwithteacch.Security.Entidades.Usuario;
import com.teach.helpwithteacch.Security.Exceptions.*;
import com.teach.helpwithteacch.Security.Repository.UsuarioRepos;
import com.teach.helpwithteacch.Services.AsignarNinoService;
import com.teach.helpwithteacch.Specification.AsignarNinoSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AsignarNinoIMPL implements AsignarNinoService {

    private final AsignarNinoRepos asignarNinoRepos;
    private final UsuarioRepos usuarioRepos;
    private final NinoRepos ninoRepos;
    private final AsignarNinoMapper asignarNinoMapper;

    @Override
    public void asignar(AsignarNinoRequest request) {
        Usuario usuario = buscarUsuario(request.getIdUsuario());
        validarUsuarioActivo(usuario);

        request.getItems().forEach(item -> {
            Nino nino = buscarNino(item.getIdNino());
            validarNinoActivo(nino);
            validarAsignacionDisponible(usuario.getIdUsuario(), nino.getIdNino());

            AsignarNino asignarNino = new AsignarNino();
            asignarNino.setUsuario(usuario);
            asignarNino.setNino(nino);
            asignarNino.setFechaAsignacion(java.time.LocalDateTime.now());
            asignarNino.setEstado(Estado.ACTIVO);

            asignarNinoRepos.save(asignarNino);
        });
    }

    @Override
    public AsignarNinoResponse editar(Long idAsignarNino, AsignarNinoEditRequest request) {
        AsignarNino asignarNino = buscarAsignacion(idAsignarNino);
        validarAsignacionActiva(asignarNino);

        Usuario usuario = buscarUsuario(request.getIdUsuario());
        validarUsuarioActivo(usuario);

        Nino nino = buscarNino(request.getIdNino());
        validarNinoActivo(nino);

        if (!asignarNino.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())
                || !asignarNino.getNino().getIdNino().equals(nino.getIdNino())) {
            validarAsignacionDisponible(usuario.getIdUsuario(), nino.getIdNino());
        }

        asignarNino.setUsuario(usuario);
        asignarNino.setNino(nino);

        return asignarNinoMapper.toResponse(asignarNinoRepos.save(asignarNino));
    }

    @Override
    public void cambiarEstado(CambiarEstadoRequest request) {
        request.getItems().forEach(item -> {
            AsignarNino asignarNino = buscarAsignacion(item.getId());
            asignarNino.setEstado(item.getEstado());
        });

        asignarNinoRepos.flush();
    }

    @Override
    @Transactional(readOnly = true)
    public AsignarNinoResponse obtenerPorId(Long idAsignarNino) {
        return asignarNinoMapper.toResponse(buscarAsignacion(idAsignarNino));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AsignarNinoResponse> listar(
            Long idUsuario,
            Long idNino,
            Estado estado,
            LocalDateTime fechaDesde,
            LocalDateTime fechaHasta,
            Pageable pageable
    ) {
        Pageable pageableOrdenado = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "idAsignarNino")
        );

        Specification<AsignarNino> specification =
                AsignarNinoSpecification.conFiltros(
                        idUsuario,
                        idNino,
                        estado,
                        fechaDesde,
                        fechaHasta
                );

        return asignarNinoRepos.findAll(
                specification,
                pageableOrdenado
        ).map(asignarNinoMapper::toResponse);
    }

    private AsignarNino buscarAsignacion(Long idAsignarNino) {
        return asignarNinoRepos.findById(idAsignarNino)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la asignación de niño con ID: " + idAsignarNino
                ));
    }

    private Usuario buscarUsuario(Long idUsuario) {
        return usuarioRepos.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el usuario con ID: " + idUsuario
                ));
    }

    private Nino buscarNino(Long idNino) {
        return ninoRepos.findById(idNino)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el niño con ID: " + idNino
                ));
    }

    private void validarUsuarioActivo(Usuario usuario) {
        if (usuario.getEstado() != Estado.ACTIVO) {
            throw new BadRequestException(
                    "El usuario con ID " + usuario.getIdUsuario() + " se encuentra inactivo"
            );
        }
    }

    private void validarNinoActivo(Nino nino) {
        if (nino.getEstado() != Estado.ACTIVO) {
            throw new BadRequestException(
                    "El niño con ID " + nino.getIdNino() + " se encuentra inactivo"
            );
        }
    }

    private void validarAsignacionActiva(AsignarNino asignarNino) {
        if (asignarNino.getEstado() != Estado.ACTIVO) {
            throw new BadRequestException(
                    "La asignación con ID " + asignarNino.getIdAsignarNino() + " se encuentra inactiva"
            );
        }
    }

    private void validarAsignacionDisponible(Long idUsuario, Long idNino) {
        if (asignarNinoRepos.existsByUsuario_IdUsuarioAndNino_IdNino(idUsuario, idNino)) {
            throw new ConflictException(
                    "El niño ya se encuentra asignado al usuario"
            );
        }
    }
}