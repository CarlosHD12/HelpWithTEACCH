package com.teach.helpwithteacch.Repository;

import com.teach.helpwithteacch.Entidades.Respuesta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RespuestaRepos extends JpaRepository<Respuesta, Long>, JpaSpecificationExecutor<Respuesta> {
    Optional<Respuesta> findByEvaluacion_IdEvaluacionAndItemIdAndSerieId(Long idEvaluacion, Integer itemId, Integer serieId);
    List<Respuesta> findByEvaluacion_IdEvaluacion(Long idEvaluacion);
    boolean existsByEvaluacion_IdEvaluacionAndItemIdAndSerieId(Long idEvaluacion, Integer itemId, Integer serieId);
    List<Respuesta> findByEvaluacion_IdEvaluacionOrderByItemIdAscSerieIdAsc(Long idEvaluacion);
    long countByEvaluacion_IdEvaluacion(Long idEvaluacion);
}