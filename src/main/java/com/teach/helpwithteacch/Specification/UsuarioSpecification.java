package com.teach.helpwithteacch.Specification;

import com.teach.helpwithteacch.Enum.Estado;
import com.teach.helpwithteacch.Enum.RolNombre;
import com.teach.helpwithteacch.Security.Config.CommonSpecification;
import com.teach.helpwithteacch.Security.Entidades.Usuario;

import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class UsuarioSpecification {

    private UsuarioSpecification() {
    }

    public static Specification<Usuario> conFiltros(
            String nombres,
            String apellidos,
            String email,
            RolNombre rol,
            Estado estado
    ) {

        List<Specification<Usuario>> filtros =
                new ArrayList<>();


        // =====================================================
        // NOMBRES
        // =====================================================

        Specification<Usuario> filtroNombres =
                CommonSpecification.contiene(
                        "nombres",
                        nombres
                );

        if (filtroNombres != null) {
            filtros.add(filtroNombres);
        }


        // =====================================================
        // APELLIDOS
        // =====================================================

        Specification<Usuario> filtroApellidos =
                CommonSpecification.contiene(
                        "apellidos",
                        apellidos
                );

        if (filtroApellidos != null) {
            filtros.add(filtroApellidos);
        }


        // =====================================================
        // EMAIL
        // =====================================================

        Specification<Usuario> filtroEmail =
                CommonSpecification.contiene(
                        "email",
                        email
                );

        if (filtroEmail != null) {
            filtros.add(filtroEmail);
        }


        // =====================================================
        // ROL
        // =====================================================

        if (rol != null) {

            Specification<Usuario> filtroRol =
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(
                                    root.get("rol").get("nombre"),
                                    rol
                            );

            filtros.add(filtroRol);
        }


        // =====================================================
        // ESTADO
        // =====================================================

        Specification<Usuario> filtroEstado =
                CommonSpecification.igual(
                        "estado",
                        estado
                );

        if (filtroEstado != null) {
            filtros.add(filtroEstado);
        }


        // =====================================================
        // SIN FILTROS
        // =====================================================

        if (filtros.isEmpty()) {

            return (
                    root,
                    query,
                    criteriaBuilder
            ) -> criteriaBuilder.conjunction();

        }


        // =====================================================
        // APLICAR FILTROS
        // =====================================================

        return Specification.allOf(filtros);
    }
}