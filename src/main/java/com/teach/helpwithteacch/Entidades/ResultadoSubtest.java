package com.teach.helpwithteacch.Entidades;

import com.teach.helpwithteacch.Enum.TipoSubtest;
import com.teach.helpwithteacch.Auditoria.Auditoria;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "resultado_subtest",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_resultado_subtest_resultado_tipo", columnNames = {"id_resultado", "tipo"})
        },
        indexes = {
                @Index(name = "idx_resultado_subtest_resultado", columnList = "id_resultado"),
                @Index(name = "idx_resultado_subtest_tipo", columnList = "tipo")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultadoSubtest extends Auditoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resultado_subtest")
    private Long idResultadoSubtest;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_resultado", nullable = false, foreignKey = @ForeignKey(name = "fk_resultado_subtest_resultado"))
    private Resultado resultado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 30)
    private TipoSubtest tipo;

    @Column(name = "puntaje", nullable = false)
    private Integer puntaje;
}