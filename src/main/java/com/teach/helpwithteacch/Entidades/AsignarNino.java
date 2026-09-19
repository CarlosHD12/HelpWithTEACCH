package com.teach.helpwithteacch.Entidades;

import com.teach.helpwithteacch.Enum.Estado;
import com.teach.helpwithteacch.Auditoria.Auditoria;
import com.teach.helpwithteacch.Security.Entidades.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "asignar_nino",
        indexes = {
                @Index(name = "idx_asignar_nino_usuario", columnList = "id_usuario"),
                @Index(name = "idx_asignar_nino_nino", columnList = "id_nino"),
                @Index(name = "idx_asignar_nino_estado", columnList = "estado")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_asignar_nino_usuario_nino", columnNames = {"id_usuario", "id_nino"})
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsignarNino extends Auditoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asignar_nino")
    private Long idAsignarNino;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false, foreignKey = @ForeignKey(name = "fk_asignar_nino_usuario"))
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_nino", nullable = false, foreignKey = @ForeignKey(name = "fk_asignar_nino_nino"))
    private Nino nino;

    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDateTime fechaAsignacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private Estado estado = Estado.ACTIVO;
}