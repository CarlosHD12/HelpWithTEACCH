package com.teach.helpwithteacch.Services.IMPL;

import com.teach.helpwithteacch.DTO.Evaluacion.*;
import com.teach.helpwithteacch.Entidades.Evaluacion;
import com.teach.helpwithteacch.Entidades.Nino;
import com.teach.helpwithteacch.Entidades.Version;
import com.teach.helpwithteacch.Enum.Estado;
import com.teach.helpwithteacch.Enum.EstadoEvaluacion;
import com.teach.helpwithteacch.Mapper.EvaluacionMapper;
import com.teach.helpwithteacch.Repository.EvaluacionRepos;
import com.teach.helpwithteacch.Repository.NinoRepos;
import com.teach.helpwithteacch.Repository.VersionRepos;
import com.teach.helpwithteacch.Security.Entidades.Usuario;
import com.teach.helpwithteacch.Security.Exceptions.*;
import com.teach.helpwithteacch.Security.Repository.UsuarioRepos;
import com.teach.helpwithteacch.Services.EvaluacionService;
import com.teach.helpwithteacch.Specification.EvaluacionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class EvaluacionIMPL implements EvaluacionService {

    private final EvaluacionRepos evaluacionRepos;
    private final NinoRepos ninoRepos;
    private final UsuarioRepos usuarioRepos;
    private final VersionRepos versionRepos;
    private final EvaluacionMapper evaluacionMapper;

    @Override
    public EvaluacionResponse crear(EvaluacionRequest request) {
        Nino nino = buscarNino(request.getIdNino());
        validarNinoActivo(nino);

        Usuario usuario = buscarUsuario(request.getIdUsuario());
        validarUsuarioActivo(usuario);

        Version version = buscarVersion(request.getIdVersion());
        validarVersionActiva(version);

        validarEvaluacionEnCurso(nino.getIdNino(), usuario.getIdUsuario());

        Evaluacion evaluacion = evaluacionMapper.toEntity(request);
        evaluacion.setNino(nino);
        evaluacion.setUsuario(usuario);
        evaluacion.setVersion(version);
        evaluacion.setFechaEvaluacion(LocalDateTime.now());
        evaluacion.setEstado(EstadoEvaluacion.EN_PROGRESO);
        evaluacion.setItemActual(1);
        evaluacion.setSerieActual(1);
        evaluacion.setProgreso(0);
        evaluacion.setFechaInicio(LocalDateTime.now());
        evaluacion.setFechaUltimoAcceso(LocalDateTime.now());

        return evaluacionMapper.toResponse(evaluacionRepos.save(evaluacion));
    }

    @Override
    public EvaluacionResponse actualizarProgreso(Long idEvaluacion, EvaluacionEditRequest request) {
        Evaluacion evaluacion = buscarEvaluacion(idEvaluacion);
        validarPuedeEditar(evaluacion);

        evaluacion.setItemActual(request.getItemActual());
        evaluacion.setSerieActual(request.getSerieActual());
        evaluacion.setProgreso(request.getProgreso());
        evaluacion.setFechaUltimoAcceso(LocalDateTime.now());

        return evaluacionMapper.toResponse(evaluacionRepos.save(evaluacion));
    }

    @Override
    @Transactional(readOnly = true)
    public EvaluacionResponse obtenerPorId(Long idEvaluacion) {
        return evaluacionMapper.toResponse(buscarEvaluacion(idEvaluacion));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EvaluacionResponse> listar(
            Long idNino,
            Long idUsuario,
            Long idPrueba,
            EstadoEvaluacion estado,
            LocalDateTime fechaDesde,
            LocalDateTime fechaHasta,
            Pageable pageable
    ) {
        Pageable pageableOrdenado = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "idEvaluacion")
        );

        Specification<Evaluacion> specification =
                EvaluacionSpecification.conFiltros(
                        idNino,
                        idUsuario,
                        idPrueba,
                        estado,
                        fechaDesde,
                        fechaHasta
                );

        return evaluacionRepos.findAll(
                specification,
                pageableOrdenado
        ).map(evaluacionMapper::toResponse);
    }

    @Override
    public EvaluacionResponse pausar(Long idEvaluacion) {
        Evaluacion evaluacion = buscarEvaluacion(idEvaluacion);

        if (evaluacion.getEstado() != EstadoEvaluacion.EN_PROGRESO) {
            throw new BadRequestException(
                    "La evaluación con ID " + idEvaluacion + " no se encuentra en progreso"
            );
        }

        evaluacion.setEstado(EstadoEvaluacion.PAUSADA);
        evaluacion.setFechaUltimoAcceso(LocalDateTime.now());

        return evaluacionMapper.toResponse(evaluacionRepos.save(evaluacion));
    }

    @Override
    public EvaluacionResponse reanudar(Long idEvaluacion) {
        Evaluacion evaluacion = buscarEvaluacion(idEvaluacion);

        if (evaluacion.getEstado() != EstadoEvaluacion.PAUSADA) {
            throw new BadRequestException(
                    "La evaluación con ID " + idEvaluacion + " no se encuentra pausada"
            );
        }

        validarNinoActivo(evaluacion.getNino());
        validarUsuarioActivo(evaluacion.getUsuario());
        validarVersionActiva(evaluacion.getVersion());

        evaluacion.setEstado(EstadoEvaluacion.EN_PROGRESO);
        evaluacion.setFechaUltimoAcceso(LocalDateTime.now());

        return evaluacionMapper.toResponse(evaluacionRepos.save(evaluacion));
    }

    @Override
    public EvaluacionResponse cancelar(Long idEvaluacion) {
        Evaluacion evaluacion = buscarEvaluacion(idEvaluacion);

        if (evaluacion.getEstado() == EstadoEvaluacion.COMPLETADA) {
            throw new BadRequestException(
                    "No se puede cancelar una evaluación completada"
            );
        }

        if (evaluacion.getEstado() == EstadoEvaluacion.CANCELADA) {
            throw new BadRequestException(
                    "La evaluación con ID " + idEvaluacion + " ya se encuentra cancelada"
            );
        }

        evaluacion.setEstado(EstadoEvaluacion.CANCELADA);
        evaluacion.setFechaUltimoAcceso(LocalDateTime.now());

        return evaluacionMapper.toResponse(evaluacionRepos.save(evaluacion));
    }

    private Evaluacion buscarEvaluacion(Long idEvaluacion) {
        return evaluacionRepos.findById(idEvaluacion)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la evaluación con ID: " + idEvaluacion
                ));
    }

    private Nino buscarNino(Long idNino) {
        return ninoRepos.findById(idNino)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el niño con ID: " + idNino
                ));
    }

    private Usuario buscarUsuario(Long idUsuario) {
        return usuarioRepos.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el usuario con ID: " + idUsuario
                ));
    }

    private Version buscarVersion(Long idVersion) {
        return versionRepos.findById(idVersion)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la versión con ID: " + idVersion
                ));
    }

    private void validarNinoActivo(Nino nino) {
        if (nino.getEstado() != Estado.ACTIVO) {
            throw new BadRequestException(
                    "El niño con ID " + nino.getIdNino() + " se encuentra inactivo"
            );
        }
    }

    private void validarUsuarioActivo(Usuario usuario) {
        if (usuario.getEstado() != Estado.ACTIVO) {
            throw new BadRequestException(
                    "El usuario con ID " + usuario.getIdUsuario() + " se encuentra inactivo"
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

    private void validarEvaluacionEnCurso(Long idNino, Long idUsuario) {
        if (evaluacionRepos.existsByNino_IdNinoAndUsuario_IdUsuarioAndEstado(
                idNino, idUsuario, EstadoEvaluacion.EN_PROGRESO)) {
            throw new BadRequestException(
                    "El usuario ya tiene una evaluación en progreso para este niño"
            );
        }

        if (evaluacionRepos.existsByNino_IdNinoAndUsuario_IdUsuarioAndEstado(
                idNino, idUsuario, EstadoEvaluacion.PAUSADA)) {
            throw new BadRequestException(
                    "El usuario ya tiene una evaluación pausada para este niño"
            );
        }
    }

    private void validarPuedeEditar(Evaluacion evaluacion) {
        if (evaluacion.getEstado() != EstadoEvaluacion.EN_PROGRESO
                && evaluacion.getEstado() != EstadoEvaluacion.PAUSADA) {
            throw new BadRequestException(
                    "La evaluación con ID " + evaluacion.getIdEvaluacion()
                            + " no puede actualizarse porque se encuentra "
                            + evaluacion.getEstado()
            );
        }
    }
}