package com.teach.helpwithteacch.Repository;

import com.teach.helpwithteacch.Entidades.Evaluacion;
import com.teach.helpwithteacch.Enum.EstadoEvaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EvaluacionRepos extends JpaRepository<Evaluacion, Long>, JpaSpecificationExecutor<Evaluacion> {
    Optional<Evaluacion> findByIdEvaluacionAndUsuario_IdUsuario(Long idEvaluacion, Long idUsuario);
    boolean existsByIdEvaluacionAndUsuario_IdUsuario(Long idEvaluacion, Long idUsuario);
    boolean existsByNino_IdNinoAndUsuario_IdUsuarioAndEstado(Long idNino, Long idUsuario, EstadoEvaluacion estado);
}