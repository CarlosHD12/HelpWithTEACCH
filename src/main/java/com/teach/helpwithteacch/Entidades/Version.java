package com.teach.helpwithteacch.Entidades;

import com.teach.helpwithteacch.Enum.Estado;
import com.teach.helpwithteacch.Auditoria.Auditoria;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "version",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_version_prueba_version", columnNames = {"id_prueba", "version"})
        },
        indexes = {
                @Index(name = "idx_version_prueba", columnList = "id_prueba"),
                @Index(name = "idx_version_estado", columnList = "estado")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Version extends Auditoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_version")
    private Long idVersion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_prueba", nullable = false, foreignKey = @ForeignKey(name = "fk_version_prueba"))
    private Prueba prueba;

    @Column(name = "numero_version", nullable = false, length = 20)
    private String numeroVersion;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private Estado estado = Estado.ACTIVO;

    @Column(name = "fecha_publicacion")
    private LocalDateTime fechaPublicacion;
}