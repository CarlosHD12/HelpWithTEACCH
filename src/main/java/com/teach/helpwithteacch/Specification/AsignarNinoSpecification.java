package com.teach.helpwithteacch.Specification;

import com.teach.helpwithteacch.Entidades.AsignarNino;
import com.teach.helpwithteacch.Enum.Estado;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class AsignarNinoSpecification {

    private AsignarNinoSpecification() {
    }

    public static Specification<AsignarNino> conFiltros(
            Long idUsuario,
            Long idNino,
            Estado estado,
            LocalDateTime fechaDesde,
            LocalDateTime fechaHasta
    ) {

        List<Specification<AsignarNino>> filtros =
                new ArrayList<>();


        // =====================================================
        // USUARIO
        // =====================================================

        if (idUsuario != null) {

            filtros.add(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(
                                    root.get("usuario")
                                            .get("idUsuario"),
                                    idUsuario
                            )
            );

        }


        // =====================================================
        // NIÑO
        // =====================================================

        if (idNino != null) {

            filtros.add(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(
                                    root.get("nino")
                                            .get("idNino"),
                                    idNino
                            )
            );

        }


        // =====================================================
        // ESTADO
        // =====================================================

        if (estado != null) {

            filtros.add(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(
                                    root.get("estado"),
                                    estado
                            )
            );

        }


        // =====================================================
        // FECHA DESDE
        // =====================================================

        if (fechaDesde != null) {

            filtros.add(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.greaterThanOrEqualTo(
                                    root.get("fechaAsignacion"),
                                    fechaDesde
                            )
            );

        }


        // =====================================================
        // FECHA HASTA
        // =====================================================

        if (fechaHasta != null) {

            filtros.add(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.lessThanOrEqualTo(
                                    root.get("fechaAsignacion"),
                                    fechaHasta
                            )
            );

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