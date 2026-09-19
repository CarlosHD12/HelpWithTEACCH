package com.teach.helpwithteacch.Repository;

import com.teach.helpwithteacch.Entidades.AsignarNino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AsignarNinoRepos extends JpaRepository<AsignarNino, Long>, JpaSpecificationExecutor<AsignarNino> {
    Optional<AsignarNino> findByUsuario_IdUsuarioAndNino_IdNino(Long idUsuario, Long idNino);
    boolean existsByUsuario_IdUsuarioAndNino_IdNino(Long idUsuario, Long idNino);
}