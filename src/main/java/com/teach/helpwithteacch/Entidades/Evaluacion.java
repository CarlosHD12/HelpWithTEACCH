package com.teach.helpwithteacch.Entidades;

import com.teach.helpwithteacch.Enum.EstadoEvaluacion;
import com.teach.helpwithteacch.Auditoria.Auditoria;
import com.teach.helpwithteacch.Security.Entidades.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "evaluacion",
        indexes = {
                @Index(name = "idx_evaluacion_nino", columnList = "id_nino"),
                @Index(name = "idx_evaluacion_usuario", columnList = "id_usuario"),
                @Index(name = "idx_evaluacion_version", columnList = "id_version"),
                @Index(name = "idx_evaluacion_estado", columnList = "estado"),
                @Index(name = "idx_evaluacion_fecha", columnList = "fecha_evaluacion")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evaluacion extends Auditoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluacion")
    private Long idEvaluacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_nino", nullable = false, foreignKey = @ForeignKey(name = "fk_evaluacion_nino"))
    private Nino nino;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false, foreignKey = @ForeignKey(name = "fk_evaluacion_usuario"))
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_version", nullable = false, foreignKey = @ForeignKey(name = "fk_evaluacion_version"))
    private Version version;

    @Column(name = "fecha_evaluacion", nullable = false)
    private LocalDateTime fechaEvaluacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private EstadoEvaluacion estado = EstadoEvaluacion.PENDIENTE;

    @Column(name = "item_actual")
    private Integer itemActual;

    @Column(name = "serie_actual")
    private Integer serieActual;

    @Builder.Default
    @Column(name = "progreso", nullable = false)
    private Integer progreso = 0;

    @Column(name = "fecha_ultimo_acceso")
    private LocalDateTime fechaUltimoAcceso;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_finalizacion")
    private LocalDateTime fechaFinalizacion;
}