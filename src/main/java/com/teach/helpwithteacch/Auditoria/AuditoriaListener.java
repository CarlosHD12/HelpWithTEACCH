package com.teach.helpwithteacch.Auditoria;

import com.teach.helpwithteacch.Security.User.CustomUserDetails;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AuditoriaListener {

    @PrePersist
    public void antesDeGuardar(Object entity) {
        if (!(entity instanceof Auditoria auditoria)) {
            return;
        }

        LocalDateTime ahora = LocalDateTime.now();

        auditoria.setFechaCreacion(ahora);
        auditoria.setCreadoPor(obtenerUsuarioActual());
    }

    @PreUpdate
    public void antesDeActualizar(Object entity) {
        if (!(entity instanceof Auditoria auditoria)) {
            return;
        }

        auditoria.setFechaModificacion(LocalDateTime.now());
        auditoria.setModificadoPor(obtenerUsuarioActual());
    }

    private String obtenerUsuarioActual() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {
            return "SYSTEM";
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getNombreCompleto();
        }

        if (principal instanceof String username &&
                !"anonymousUser".equals(username)) {
            return username;
        }

        return "SYSTEM";
    }
}