package com.teach.helpwithteacch.Entidades;

import com.fasterxml.jackson.databind.JsonNode;
import com.teach.helpwithteacch.Auditoria.Auditoria;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(
        name = "respuesta",
        indexes = {
                @Index(name = "idx_respuesta_evaluacion", columnList = "id_evaluacion"),
                @Index(name = "idx_respuesta_item", columnList = "item_id"),
                @Index(name = "idx_respuesta_serie", columnList = "serie_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Respuesta extends Auditoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_respuesta")
    private Long idRespuesta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_evaluacion", nullable = false, foreignKey = @ForeignKey(name = "fk_respuesta_evaluacion"))
    private Evaluacion evaluacion;

    @Column(name = "item_id", nullable = false)
    private Integer itemId;

    @Column(name = "serie_id")
    private Integer serieId;

    @Column(name = "tipo", nullable = false, length = 30)
    private String tipo;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "valor", nullable = false, columnDefinition = "jsonb")
    private JsonNode valor;

    @Column(name = "correcta", nullable = false)
    private Boolean correcta;

    @Column(name = "puntaje", nullable = false)
    private Integer puntaje;
}