package com.teach.helpwithteacch.Services.IMPL;

import com.teach.helpwithteacch.DTO.Common.CambiarEstadoRequest;
import com.teach.helpwithteacch.DTO.Nino.*;
import com.teach.helpwithteacch.Entidades.Nino;
import com.teach.helpwithteacch.Enum.Estado;
import com.teach.helpwithteacch.Mapper.NinoMapper;
import com.teach.helpwithteacch.Repository.NinoRepos;
import com.teach.helpwithteacch.Security.Exceptions.*;
import com.teach.helpwithteacch.Services.NinoService;
import com.teach.helpwithteacch.Specification.NinoSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NinoIMPL implements NinoService {

    private final NinoRepos ninoRepos;
    private final NinoMapper ninoMapper;

    @Override
    public NinoResponse crear(NinoRequest request) {
        Nino nino = ninoMapper.toEntity(request);
        nino.setEstado(Estado.ACTIVO);
        return ninoMapper.toResponse(ninoRepos.save(nino));
    }

    @Override
    public NinoResponse editar(Long idNino, NinoEditRequest request) {
        Nino nino = buscarNino(idNino);
        validarNinoActivo(nino);
        ninoMapper.updateEntity(request, nino);
        return ninoMapper.toResponse(ninoRepos.save(nino));
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
        return ninoMapper.toResponse(buscarNino(idNino));
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
                Sort.by(Sort.Direction.DESC, "idNino")
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
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el niño con ID: " + idNino
                ));
    }

    private void validarNinoActivo(Nino nino) {
        if (nino.getEstado() != Estado.ACTIVO) {
            throw new BadRequestException(
                    "El niño con ID " + nino.getIdNino() + " se encuentra inactivo"
            );
        }
    }
}