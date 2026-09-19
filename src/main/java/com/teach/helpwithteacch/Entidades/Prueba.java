package com.teach.helpwithteacch.Entidades;

import com.teach.helpwithteacch.Enum.Estado;
import com.teach.helpwithteacch.Enum.TipoPrueba;
import com.teach.helpwithteacch.Auditoria.Auditoria;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "prueba",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_prueba_tipo", columnNames = "tipo")
        },
        indexes = {
                @Index(name = "idx_prueba_nombre", columnList = "nombre"),
                @Index(name = "idx_prueba_estado", columnList = "estado")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prueba extends Auditoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prueba")
    private Long idPrueba;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoPrueba tipo;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private Estado estado = Estado.ACTIVO;
}