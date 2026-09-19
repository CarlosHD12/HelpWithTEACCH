package com.teach.helpwithteacch.Security.Repository;

import com.teach.helpwithteacch.Security.Entidades.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepos extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {
    @Query("""
        SELECT u
        FROM Usuario u
        JOIN FETCH u.rol
        WHERE u.email = :email
        """)
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdUsuarioNot(String email, Long idUsuario);
    Page<Usuario> findAllByOrderByIdUsuarioDesc(Pageable pageable);
}