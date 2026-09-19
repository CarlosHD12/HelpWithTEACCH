package com.teach.helpwithteacch.Services;

import com.teach.helpwithteacch.DTO.Common.CambiarEstadoRequest;
import com.teach.helpwithteacch.DTO.Version.*;
import com.teach.helpwithteacch.Enum.Estado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface VersionService {
    VersionResponse crear(VersionRequest request);
    VersionResponse editar(Long idVersion, VersionEditRequest request);
    void cambiarEstado(CambiarEstadoRequest request);
    VersionResponse obtenerPorId(Long idVersion);
    Page<VersionResponse> listar(
            Long idPrueba,
            String numeroVersion,
            Estado estado,
            LocalDateTime fechaDesde,
            LocalDateTime fechaHasta,
            Pageable pageable
    );
}