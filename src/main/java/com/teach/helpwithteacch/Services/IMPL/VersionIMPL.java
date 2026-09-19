package com.teach.helpwithteacch.Services.IMPL;

import com.teach.helpwithteacch.DTO.Common.CambiarEstadoRequest;
import com.teach.helpwithteacch.DTO.Version.*;
import com.teach.helpwithteacch.Entidades.Prueba;
import com.teach.helpwithteacch.Entidades.Version;
import com.teach.helpwithteacch.Enum.Estado;
import com.teach.helpwithteacch.Mapper.VersionMapper;
import com.teach.helpwithteacch.Repository.PruebaRepos;
import com.teach.helpwithteacch.Repository.VersionRepos;
import com.teach.helpwithteacch.Security.Exceptions.*;
import com.teach.helpwithteacch.Services.VersionService;
import com.teach.helpwithteacch.Specification.VersionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class VersionIMPL implements VersionService {

    private final VersionRepos versionRepos;
    private final PruebaRepos pruebaRepos;
    private final VersionMapper versionMapper;

    @Override
    public VersionResponse crear(VersionRequest request) {
        Prueba prueba = buscarPrueba(request.getIdPrueba());
        validarPruebaActiva(prueba);
        validarVersionDisponible(request.getIdPrueba(), request.getNumeroVersion());

        Version version = versionMapper.toEntity(request);
        version.setPrueba(prueba);
        version.setEstado(Estado.ACTIVO);
        version.setFechaPublicacion(java.time.LocalDateTime.now());

        return versionMapper.toResponse(versionRepos.save(version));
    }

    @Override
    public VersionResponse editar(Long idVersion, VersionEditRequest request) {
        Version version = buscarVersion(idVersion);
        validarVersionActiva(version);

        Prueba prueba = buscarPrueba(request.getIdPrueba());
        validarPruebaActiva(prueba);
        validarVersionDisponible(request.getIdPrueba(), request.getNumeroVersion(), idVersion);

        versionMapper.updateEntity(request, version);
        version.setPrueba(prueba);

        return versionMapper.toResponse(versionRepos.save(version));
    }

    @Override
    public void cambiarEstado(CambiarEstadoRequest request) {
        request.getItems().forEach(item -> {
            Version version = buscarVersion(item.getId());
            version.setEstado(item.getEstado());
        });

        versionRepos.flush();
    }

    @Override
    @Transactional(readOnly = true)
    public VersionResponse obtenerPorId(Long idVersion) {
        return versionMapper.toResponse(buscarVersion(idVersion));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VersionResponse> listar(
            Long idPrueba,
            String version,
            Estado estado,
            LocalDateTime fechaDesde,
            LocalDateTime fechaHasta,
            Pageable pageable
    ) {
        Pageable pageableOrdenado = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "idVersion")
        );

        Specification<Version> specification =
                VersionSpecification.conFiltros(
                        idPrueba,
                        version,
                        estado,
                        fechaDesde,
                        fechaHasta
                );

        return versionRepos.findAll(
                specification,
                pageableOrdenado
        ).map(versionMapper::toResponse);
    }

    private Version buscarVersion(Long idVersion) {
        return versionRepos.findById(idVersion)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la versión con ID: " + idVersion
                ));
    }

    private Prueba buscarPrueba(Long idPrueba) {
        return pruebaRepos.findById(idPrueba)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la prueba con ID: " + idPrueba
                ));
    }

    private void validarPruebaActiva(Prueba prueba) {
        if (prueba.getEstado() != Estado.ACTIVO) {
            throw new BadRequestException(
                    "La prueba con ID " + prueba.getIdPrueba() + " se encuentra inactiva"
            );
        }
    }

    private void validarVersionActiva(Version version) {
        if (version.getEstado() != Estado.ACTIVO) {
            throw new BadRequestException(
                    "La versión con ID " + version.getIdVersion() + " se encuentra inactiva"
            );
        }
    }

    private void validarVersionDisponible(Long idPrueba, String nombreVersion) {
        if (versionRepos.existsByPrueba_IdPruebaAndNumeroVersion(idPrueba, nombreVersion)) {
            throw new ConflictException(
                    "Ya existe una versión registrada con el nombre: " + nombreVersion
            );
        }
    }

    private void validarVersionDisponible(Long idPrueba, String nombreVersion, Long idVersion) {
        if (versionRepos.existsByPrueba_IdPruebaAndNumeroVersionAndIdVersionNot(
                idPrueba, nombreVersion, idVersion)) {
            throw new ConflictException(
                    "Ya existe otra versión registrada con el nombre: " + nombreVersion
            );
        }
    }
}