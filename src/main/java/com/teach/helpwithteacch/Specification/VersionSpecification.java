package com.teach.helpwithteacch.Specification;

import com.teach.helpwithteacch.Entidades.Version;
import com.teach.helpwithteacch.Enum.Estado;
import com.teach.helpwithteacch.Security.Config.CommonSpecification;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public final class VersionSpecification {

    private VersionSpecification() {
    }

    public static Specification<Version> conFiltros(
            Long idPrueba,
            String numeroVersion,
            Estado estado,
            LocalDateTime fechaDesde,
            LocalDateTime fechaHasta
    ) {
        return Specification.allOf(
                idPrueba == null
                        ? null
                        : (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(
                                root.get("prueba").get("idPrueba"),
                                idPrueba
                        ),
                CommonSpecification.contiene("numeroVersion", numeroVersion),
                CommonSpecification.igual("estado", estado),
                CommonSpecification.mayorOIgual(
                        "fechaPublicacion",
                        fechaDesde
                ),
                CommonSpecification.menorOIgual(
                        "fechaPublicacion",
                        fechaHasta
                )
        );
    }
}