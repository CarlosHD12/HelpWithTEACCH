package com.teach.helpwithteacch.Specification;

import com.teach.helpwithteacch.Entidades.PrediccionML;
import com.teach.helpwithteacch.Enum.ModeloML;
import com.teach.helpwithteacch.Security.Config.CommonSpecification;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public final class PrediccionMLSpecification {

    private PrediccionMLSpecification() {
    }

    public static Specification<PrediccionML> conFiltros(
            Long idEvaluacion,
            ModeloML modelo,
            String resultado,
            LocalDateTime fechaDesde,
            LocalDateTime fechaHasta
    ) {
        return Specification.allOf(
                idEvaluacion == null
                        ? null
                        : (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(
                                root.get("evaluacion").get("idEvaluacion"),
                                idEvaluacion
                        ),
                CommonSpecification.igual("modelo", modelo),
                CommonSpecification.contiene("resultado", resultado),
                CommonSpecification.mayorOIgual(
                        "fechaPrediccion",
                        fechaDesde
                ),
                CommonSpecification.menorOIgual(
                        "fechaPrediccion",
                        fechaHasta
                )
        );
    }
}