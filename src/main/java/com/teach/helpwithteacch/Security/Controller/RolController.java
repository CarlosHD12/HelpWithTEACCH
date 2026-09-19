package com.teach.helpwithteacch.Security.Controller;

import com.teach.helpwithteacch.Security.DTO.Rol.RolResponse;
import com.teach.helpwithteacch.Security.Service.RolService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
public class RolController {

    private final RolService rolService;

    @GetMapping("/{idRol}")
    public ResponseEntity<RolResponse> obtenerPorId(
            @PathVariable Long idRol
    ) {
        return ResponseEntity.ok(
                rolService.obtenerPorId(idRol)
        );
    }

    @GetMapping
    public ResponseEntity<Page<RolResponse>> listar(
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                rolService.listar(pageable)
        );
    }
}