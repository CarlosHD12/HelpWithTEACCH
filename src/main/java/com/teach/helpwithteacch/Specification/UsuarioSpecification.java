package com.teach.helpwithteacch.Specification;

import com.teach.helpwithteacch.Enum.Estado;
import com.teach.helpwithteacch.Enum.RolNombre;
import com.teach.helpwithteacch.Security.Config.CommonSpecification;
import com.teach.helpwithteacch.Security.Entidades.Usuario;
import org.springframework.data.jpa.domain.Specification;

public final class UsuarioSpecification {

    private UsuarioSpecification() {
    }

    public static Specification<Usuario> conFiltros(
            String nombres,
            String apellidos,
            String email,
            RolNombre rol,
            Estado estado
    ) {
        return Specification.allOf(
                CommonSpecification.contiene("nombres", nombres),
                CommonSpecification.contiene("apellidos", apellidos),
                CommonSpecification.contiene("email", email),
                (root, query, criteriaBuilder) ->
                        rol == null
                                ? null
                                : criteriaBuilder.equal(
                                root.get("rol").get("nombre"),
                                rol
                        ),
                CommonSpecification.igual("estado", estado)
        );
    }
}