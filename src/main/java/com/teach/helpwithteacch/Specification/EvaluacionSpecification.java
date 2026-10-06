package com.teach.helpwithteacch.Specification;

import com.teach.helpwithteacch.Entidades.Evaluacion;
import com.teach.helpwithteacch.Enum.EstadoEvaluacion;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public final class EvaluacionSpecification {

    private EvaluacionSpecification() {
    }

    public static Specification<Evaluacion> conFiltros(
            Long idNino,
            Long idUsuario,
            Long idPrueba,
            EstadoEvaluacion estado,
            LocalDateTime fechaDesde,
            LocalDateTime fechaHasta
    ) {

        Specification<Evaluacion> specification =
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.conjunction();


        // =====================================================
        // FILTRO POR NIÑO
        // =====================================================

        if (idNino != null) {

            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(
                                    root.get("nino")
                                            .get("idNino"),
                                    idNino
                            )
            );

        }


        // =====================================================
        // FILTRO POR USUARIO
        // =====================================================

        if (idUsuario != null) {

            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(
                                    root.get("usuario")
                                            .get("idUsuario"),
                                    idUsuario
                            )
            );

        }


        // =====================================================
        // FILTRO POR PRUEBA
        // =====================================================

        if (idPrueba != null) {

            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(
                                    root.get("version")
                                            .get("prueba")
                                            .get("idPrueba"),
                                    idPrueba
                            )
            );

        }


        // =====================================================
        // FILTRO POR ESTADO
        // =====================================================

        if (estado != null) {

            specification = specification.and(
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

            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.greaterThanOrEqualTo(
                                    root.get("fechaEvaluacion"),
                                    fechaDesde
                            )
            );

        }


        // =====================================================
        // FECHA HASTA
        // =====================================================

        if (fechaHasta != null) {

            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.lessThanOrEqualTo(
                                    root.get("fechaEvaluacion"),
                                    fechaHasta
                            )
            );

        }


        return specification;
    }
}