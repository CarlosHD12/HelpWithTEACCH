package com.teach.helpwithteacch.Entidades;

import com.teach.helpwithteacch.Enum.Estado;
import com.teach.helpwithteacch.Auditoria.Auditoria;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "nino",
        indexes = {
                @Index(name = "idx_nino_apellidos", columnList = "apellidos"),
                @Index(name = "idx_nino_sexo", columnList = "sexo"),
                @Index(name = "idx_nino_estado", columnList = "estado")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Nino extends Auditoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nino")
    private Long idNino;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(name = "sexo", nullable = false, length = 20)
    private String sexo;

    @Column(name = "etnia", length = 100)
    private String etnia;

    @Column(name = "ictericia", nullable = false)
    private Boolean ictericia;

    @Column(name = "familiar_con_tea", nullable = false)
    private Boolean familiarConTea;

    @Column(name = "foto_url", length = 500)
    private String fotoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private Estado estado = Estado.ACTIVO;
}