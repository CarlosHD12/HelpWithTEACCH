package com.teach.helpwithteacch.Specification;

import com.teach.helpwithteacch.Entidades.Prueba;
import com.teach.helpwithteacch.Enum.Estado;
import com.teach.helpwithteacch.Enum.TipoPrueba;
import com.teach.helpwithteacch.Security.Config.CommonSpecification;
import org.springframework.data.jpa.domain.Specification;

public final class PruebaSpecification {

    private PruebaSpecification() {
    }

    public static Specification<Prueba> conFiltros(
            String nombre,
            TipoPrueba tipo,
            Estado estado
    ) {
        return Specification.allOf(
                CommonSpecification.contiene("nombre", nombre),
                CommonSpecification.igual("tipo", tipo),
                CommonSpecification.igual("estado", estado)
        );
    }
}