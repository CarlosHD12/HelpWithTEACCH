package com.teach.helpwithteacch.Specification;

import com.teach.helpwithteacch.Entidades.Nino;
import com.teach.helpwithteacch.Enum.Estado;
import com.teach.helpwithteacch.Security.Config.CommonSpecification;
import org.springframework.data.jpa.domain.Specification;

public final class NinoSpecification {

    private NinoSpecification() {
    }

    public static Specification<Nino> conFiltros(
            String nombres,
            String apellidos,
            String sexo,
            Estado estado
    ) {
        return Specification.allOf(
                CommonSpecification.contiene("nombres", nombres),
                CommonSpecification.contiene("apellidos", apellidos),
                CommonSpecification.contiene("sexo", sexo),
                CommonSpecification.igual("estado", estado)
        );
    }
}