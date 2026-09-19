package com.teach.helpwithteacch.Services;

import com.teach.helpwithteacch.DTO.Common.CambiarEstadoRequest;
import com.teach.helpwithteacch.DTO.Nino.*;
import com.teach.helpwithteacch.Enum.Estado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NinoService {
    NinoResponse crear(NinoRequest request);
    NinoResponse editar(Long idNino, NinoEditRequest request);
    void cambiarEstado(CambiarEstadoRequest request);
    NinoResponse obtenerPorId(Long idNino);
    Page<NinoResponse> listar(
            String nombres,
            String apellidos,
            String sexo,
            Estado estado,
            Pageable pageable
    );
}