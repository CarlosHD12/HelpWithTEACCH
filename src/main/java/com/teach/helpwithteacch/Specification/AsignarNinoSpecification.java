package com.teach.helpwithteacch.Specification;

import com.teach.helpwithteacch.Entidades.AsignarNino;
import com.teach.helpwithteacch.Enum.Estado;
import com.teach.helpwithteacch.Security.Config.CommonSpecification;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

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
        return Specification.allOf(
                idUsuario == null
                        ? null
                        : (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(
                                root.get("usuario").get("idUsuario"),
                                idUsuario
                        ),
                idNino == null
                        ? null
                        : (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(
                                root.get("nino").get("idNino"),
                                idNino
                        ),
                CommonSpecification.igual("estado", estado),
                CommonSpecification.mayorOIgual(
                        "fechaAsignacion",
                        fechaDesde
                ),
                CommonSpecification.menorOIgual(
                        "fechaAsignacion",
                        fechaHasta
                )
        );
    }
}