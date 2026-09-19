package com.teach.helpwithteacch.Security.Entidades;

import com.teach.helpwithteacch.Auditoria.Auditoria;
import com.teach.helpwithteacch.Enum.Estado;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "usuario",
        indexes = {
                @Index(name = "idx_usuario_id_rol", columnList = "id_rol"),
                @Index(name = "idx_usuario_email", columnList = "email"),
                @Index(name = "idx_usuario_estado", columnList = "estado")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario extends Auditoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_rol", nullable = false, foreignKey = @ForeignKey(name = "fk_usuario_rol"))
    private Rol rol;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private Estado estado = Estado.ACTIVO;
}