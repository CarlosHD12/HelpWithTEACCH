package com.teach.helpwithteacch.Repository;

import com.teach.helpwithteacch.Entidades.Resultado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResultadoRepos extends JpaRepository<Resultado, Long>, JpaSpecificationExecutor<Resultado> {
    Optional<Resultado> findByEvaluacion_IdEvaluacion(Long idEvaluacion);
    boolean existsByEvaluacion_IdEvaluacion(Long idEvaluacion);
}