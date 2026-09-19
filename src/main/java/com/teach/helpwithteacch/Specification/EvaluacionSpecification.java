package com.teach.helpwithteacch.Specification;

import com.teach.helpwithteacch.Entidades.Evaluacion;
import com.teach.helpwithteacch.Enum.EstadoEvaluacion;
import com.teach.helpwithteacch.Security.Config.CommonSpecification;
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
        return Specification.allOf(
                idNino == null
                        ? null
                        : (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(
                                root.get("nino").get("idNino"),
                                idNino
                        ),
                idUsuario == null
                        ? null
                        : (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(
                                root.get("usuario").get("idUsuario"),
                                idUsuario
                        ),
                idPrueba == null
                        ? null
                        : (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(
                                root.get("version")
                                        .get("prueba")
                                        .get("idPrueba"),
                                idPrueba
                        ),
                CommonSpecification.igual("estado", estado),
                CommonSpecification.mayorOIgual(
                        "fechaEvaluacion",
                        fechaDesde
                ),
                CommonSpecification.menorOIgual(
                        "fechaEvaluacion",
                        fechaHasta
                )
        );
    }
}