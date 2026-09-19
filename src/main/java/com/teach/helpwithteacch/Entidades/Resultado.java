package com.teach.helpwithteacch.Entidades;

import com.teach.helpwithteacch.Auditoria.Auditoria;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "resultado",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_resultado_evaluacion", columnNames = "id_evaluacion")
        },
        indexes = {
                @Index(name = "idx_resultado_evaluacion", columnList = "id_evaluacion"),
                @Index(name = "idx_resultado_fecha", columnList = "fecha_resultado")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resultado extends Auditoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resultado")
    private Long idResultado;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_evaluacion", nullable = false, foreignKey = @ForeignKey(name = "fk_resultado_evaluacion"))
    private Evaluacion evaluacion;

    @Column(name = "puntaje_total", nullable = false)
    private Integer puntajeTotal;

    @Column(name = "fecha_resultado", nullable = false)
    private LocalDateTime fechaResultado;
}