package com.teach.helpwithteacch.Services;

import com.teach.helpwithteacch.DTO.Asignarnino.*;
import com.teach.helpwithteacch.DTO.Common.CambiarEstadoRequest;
import com.teach.helpwithteacch.Enum.Estado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface AsignarNinoService {
    void asignar(AsignarNinoRequest request);
    AsignarNinoResponse editar(Long idAsignarNino, AsignarNinoEditRequest request);
    void cambiarEstado(CambiarEstadoRequest request);
    AsignarNinoResponse obtenerPorId(Long idAsignarNino);
    Page<AsignarNinoResponse> listar(
            Long idUsuario,
            Long idNino,
            Estado estado,
            LocalDateTime fechaDesde,
            LocalDateTime fechaHasta,
            Pageable pageable
    );
}