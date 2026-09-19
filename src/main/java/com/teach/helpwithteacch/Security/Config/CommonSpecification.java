package com.teach.helpwithteacch.Security.Config;

import org.springframework.data.jpa.domain.Specification;

public final class CommonSpecification {

    private CommonSpecification() {
    }

    public static <T> Specification<T> contiene(
            String campo,
            String valor
    ) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(
                                root.get(campo)
                        ),
                        "%" + valor.trim().toLowerCase() + "%"
                );
    }

    public static <T> Specification<T> igual(
            String campo,
            Object valor
    ) {
        if (valor == null) {
            return null;
        }

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get(campo),
                        valor
                );
    }

    public static <T, Y extends Comparable<? super Y>> Specification<T> mayorOIgual(
            String campo,
            Y valor
    ) {
        if (valor == null) {
            return null;
        }

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(
                        root.get(campo),
                        valor
                );
    }

    public static <T, Y extends Comparable<? super Y>> Specification<T> menorOIgual(
            String campo,
            Y valor
    ) {
        if (valor == null) {
            return null;
        }

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(
                        root.get(campo),
                        valor
                );
    }
}