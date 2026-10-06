package com.teach.helpwithteacch.Specification;

import com.teach.helpwithteacch.Entidades.Nino;
import com.teach.helpwithteacch.Enum.Estado;
import com.teach.helpwithteacch.Security.Config.CommonSpecification;

import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class NinoSpecification {

    private NinoSpecification() {
    }

    public static Specification<Nino> conFiltros(
            String nombres,
            String apellidos,
            String sexo,
            Estado estado
    ) {

        List<Specification<Nino>> filtros =
                new ArrayList<>();


        // =====================================================
        // NOMBRES
        // =====================================================

        Specification<Nino> filtroNombres =
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

        Specification<Nino> filtroApellidos =
                CommonSpecification.contiene(
                        "apellidos",
                        apellidos
                );

        if (filtroApellidos != null) {
            filtros.add(filtroApellidos);
        }


        // =====================================================
        // SEXO
        // =====================================================

        Specification<Nino> filtroSexo =
                CommonSpecification.contiene(
                        "sexo",
                        sexo
                );

        if (filtroSexo != null) {
            filtros.add(filtroSexo);
        }


        // =====================================================
        // ESTADO
        // =====================================================

        Specification<Nino> filtroEstado =
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