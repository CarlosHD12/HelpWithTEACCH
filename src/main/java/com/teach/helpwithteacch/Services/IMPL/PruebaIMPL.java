package com.teach.helpwithteacch.Services.IMPL;

import com.teach.helpwithteacch.DTO.Common.CambiarEstadoRequest;
import com.teach.helpwithteacch.DTO.Prueba.*;
import com.teach.helpwithteacch.Entidades.Prueba;
import com.teach.helpwithteacch.Enum.Estado;
import com.teach.helpwithteacch.Enum.TipoPrueba;
import com.teach.helpwithteacch.Mapper.PruebaMapper;
import com.teach.helpwithteacch.Repository.PruebaRepos;
import com.teach.helpwithteacch.Security.Exceptions.*;
import com.teach.helpwithteacch.Services.PruebaService;
import com.teach.helpwithteacch.Specification.PruebaSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PruebaIMPL implements PruebaService {

    private final PruebaRepos pruebaRepos;
    private final PruebaMapper pruebaMapper;

    @Override
    public PruebaResponse crear(PruebaRequest request) {
        validarTipoDisponible(request.getTipo());

        Prueba prueba = pruebaMapper.toEntity(request);
        prueba.setEstado(Estado.ACTIVO);

        return pruebaMapper.toResponse(pruebaRepos.save(prueba));
    }

    @Override
    public PruebaResponse editar(Long idPrueba, PruebaEditRequest request) {
        Prueba prueba = buscarPrueba(idPrueba);
        validarPruebaActiva(prueba);
        validarTipoDisponible(request.getTipo(), idPrueba);

        pruebaMapper.updateEntity(request, prueba);

        return pruebaMapper.toResponse(pruebaRepos.save(prueba));
    }

    @Override
    public void cambiarEstado(CambiarEstadoRequest request) {
        request.getItems().forEach(item -> {
            Prueba prueba = buscarPrueba(item.getId());
            prueba.setEstado(item.getEstado());
        });

        pruebaRepos.flush();
    }

    @Override
    @Transactional(readOnly = true)
    public PruebaResponse obtenerPorId(Long idPrueba) {
        return pruebaMapper.toResponse(buscarPrueba(idPrueba));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PruebaResponse> listar(
            String nombre,
            TipoPrueba tipo,
            Estado estado,
            Pageable pageable
    ) {
        Pageable pageableOrdenado = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "idPrueba")
        );

        Specification<Prueba> specification =
                PruebaSpecification.conFiltros(
                        nombre,
                        tipo,
                        estado
                );

        return pruebaRepos.findAll(
                specification,
                pageableOrdenado
        ).map(pruebaMapper::toResponse);
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

    private void validarTipoDisponible(com.teach.helpwithteacch.Enum.TipoPrueba tipo) {
        if (pruebaRepos.existsByTipo(tipo)) {
            throw new ConflictException(
                    "Ya existe una prueba registrada con el tipo: " + tipo
            );
        }
    }

    private void validarTipoDisponible(com.teach.helpwithteacch.Enum.TipoPrueba tipo, Long idPrueba) {
        if (pruebaRepos.existsByTipoAndIdPruebaNot(tipo, idPrueba)) {
            throw new ConflictException(
                    "Ya existe otra prueba registrada con el tipo: " + tipo
            );
        }
    }
}