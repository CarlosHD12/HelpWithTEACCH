package com.teach.helpwithteacch.Services;

import com.teach.helpwithteacch.DTO.Common.CambiarEstadoRequest;
import com.teach.helpwithteacch.DTO.Prueba.*;
import com.teach.helpwithteacch.Enum.Estado;
import com.teach.helpwithteacch.Enum.TipoPrueba;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PruebaService {
    PruebaResponse crear(PruebaRequest request);
    PruebaResponse editar(Long idPrueba, PruebaEditRequest request);
    void cambiarEstado(CambiarEstadoRequest request);
    PruebaResponse obtenerPorId(Long idPrueba);
    Page<PruebaResponse> listar(
            String nombre,
            TipoPrueba tipo,
            Estado estado,
            Pageable pageable
    );
}