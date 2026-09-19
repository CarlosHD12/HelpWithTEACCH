package com.teach.helpwithteacch.Entidades;

import com.teach.helpwithteacch.Enum.ModeloML;
import com.teach.helpwithteacch.Auditoria.Auditoria;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "prediccion_ml",
        indexes = {
                @Index(name = "idx_prediccion_evaluacion", columnList = "id_evaluacion"),
                @Index(name = "idx_prediccion_modelo", columnList = "modelo"),
                @Index(name = "idx_prediccion_fecha", columnList = "fecha_prediccion")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrediccionML extends Auditoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prediccion")
    private Long idPrediccion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_evaluacion", nullable = false, foreignKey = @ForeignKey(name = "fk_prediccion_evaluacion"))
    private Evaluacion evaluacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "modelo", nullable = false, length = 30)
    private ModeloML modelo;

    @Column(name = "resultado", nullable = false, length = 100)
    private String resultado;

    @Column(name = "probabilidad", nullable = false, precision = 5, scale = 4)
    private BigDecimal probabilidad;

    @Column(name = "fecha_prediccion", nullable = false)
    private LocalDateTime fechaPrediccion;
}