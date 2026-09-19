package com.teach.helpwithteacch.Security.Entidades;

import com.teach.helpwithteacch.Enum.Estado;
import com.teach.helpwithteacch.Enum.RolNombre;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "rol",
        indexes = {
                @Index(name = "idx_rol_nombre", columnList = "nombre"),
                @Index(name = "idx_rol_estado", columnList = "estado")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Long idRol;

    @Enumerated(EnumType.STRING)
    @Column(name = "nombre", nullable = false, unique = true, length = 50)
    private RolNombre nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private Estado estado;
}