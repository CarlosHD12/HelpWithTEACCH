package com.teach.helpwithteacch.Services.IMPL;

import com.teach.helpwithteacch.DTO.Common.CambiarEstadoRequest;
import com.teach.helpwithteacch.DTO.Nino.*;
import com.teach.helpwithteacch.Entidades.AsignarNino;
import com.teach.helpwithteacch.Entidades.Nino;
import com.teach.helpwithteacch.Enum.Estado;
import com.teach.helpwithteacch.Enum.RolNombre;
import com.teach.helpwithteacch.Mapper.NinoMapper;
import com.teach.helpwithteacch.Repository.AsignarNinoRepos;
import com.teach.helpwithteacch.Repository.NinoRepos;
import com.teach.helpwithteacch.Security.Entidades.Usuario;
import com.teach.helpwithteacch.Security.Exceptions.*;
import com.teach.helpwithteacch.Security.Repository.UsuarioRepos;
import com.teach.helpwithteacch.Services.NinoService;
import com.teach.helpwithteacch.Specification.NinoSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class NinoIMPL implements NinoService {

    private final NinoRepos ninoRepos;
    private final NinoMapper ninoMapper;

    // Repositorios necesarios para la autoasignación
    private final AsignarNinoRepos asignarNinoRepos;
    private final UsuarioRepos usuarioRepos;


    @Override
    public NinoResponse crear(NinoRequest request) {

        // =====================================================
        // 1. CREAR NIÑO
        // =====================================================

        Nino nino = ninoMapper.toEntity(request);
        nino.setEstado(Estado.ACTIVO);

        Nino ninoGuardado = ninoRepos.save(nino);


        // =====================================================
        // 2. OBTENER USUARIO AUTENTICADO
        // =====================================================

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
    throw new BadRequestException(
            "No se pudo identificar al usuario autenticado"
    );
}

        String emailUsuario = authentication.getName();

        Usuario usuario = usuarioRepos.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el usuario autenticado"
                ));


        // =====================================================
        // 3. AUTOASIGNAR SOLO A PADRE O DOCENTE
        // =====================================================

        RolNombre rol = usuario.getRol().getNombre();

        if (rol == RolNombre.PADRE || rol == RolNombre.DOCENTE) {

            // Verificar que no exista ya la relación
            if (!asignarNinoRepos.existsByUsuario_IdUsuarioAndNino_IdNino(
                    usuario.getIdUsuario(),
                    ninoGuardado.getIdNino()
            )) {

                AsignarNino asignarNino = new AsignarNino();

                asignarNino.setUsuario(usuario);
                asignarNino.setNino(ninoGuardado);
                asignarNino.setFechaAsignacion(LocalDateTime.now());
                asignarNino.setEstado(Estado.ACTIVO);

                asignarNinoRepos.save(asignarNino);
            }
        }


        // =====================================================
        // 4. DEVOLVER NIÑO CREADO
        // =====================================================

        return ninoMapper.toResponse(ninoGuardado);
    }


    @Override
    public NinoResponse editar(Long idNino, NinoEditRequest request) {

        Nino nino = buscarNino(idNino);

        validarNinoActivo(nino);

        ninoMapper.updateEntity(request, nino);

        return ninoMapper.toResponse(
                ninoRepos.save(nino)
        );
    }


    @Override
    public void cambiarEstado(CambiarEstadoRequest request) {

        request.getItems().forEach(item -> {

            Nino nino = buscarNino(item.getId());

            nino.setEstado(item.getEstado());
        });

        ninoRepos.flush();
    }


    @Override
    @Transactional(readOnly = true)
    public NinoResponse obtenerPorId(Long idNino) {

        return ninoMapper.toResponse(
                buscarNino(idNino)
        );
    }


    @Override
    @Transactional(readOnly = true)
    public Page<NinoResponse> listar(
            String nombres,
            String apellidos,
            String sexo,
            Estado estado,
            Pageable pageable
    ) {

        Pageable pageableOrdenado = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(
                        Sort.Direction.DESC,
                        "idNino"
                )
        );

        Specification<Nino> specification =
                NinoSpecification.conFiltros(
                        nombres,
                        apellidos,
                        sexo,
                        estado
                );

        return ninoRepos.findAll(
                specification,
                pageableOrdenado
        ).map(ninoMapper::toResponse);
    }


    private Nino buscarNino(Long idNino) {

        return ninoRepos.findById(idNino)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No se encontró el niño con ID: " + idNino
                        )
                );
    }


    private void validarNinoActivo(Nino nino) {

        if (nino.getEstado() != Estado.ACTIVO) {

            throw new BadRequestException(
                    "El niño con ID " +
                            nino.getIdNino() +
                            " se encuentra inactivo"
            );
        }
    }
}