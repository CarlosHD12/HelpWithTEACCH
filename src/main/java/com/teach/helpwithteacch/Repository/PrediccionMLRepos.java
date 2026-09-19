package com.teach.helpwithteacch.Repository;

import com.teach.helpwithteacch.Entidades.PrediccionML;
import com.teach.helpwithteacch.Enum.ModeloML;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrediccionMLRepos extends JpaRepository<PrediccionML, Long>, JpaSpecificationExecutor<PrediccionML> {
    Optional<PrediccionML> findByEvaluacion_IdEvaluacionAndModelo(Long idEvaluacion, ModeloML modelo);
    List<PrediccionML> findByEvaluacion_IdEvaluacion(Long idEvaluacion);
    boolean existsByEvaluacion_IdEvaluacionAndModelo(Long idEvaluacion, ModeloML modelo);
    List<PrediccionML> findByEvaluacion_IdEvaluacionOrderByModeloAsc(Long idEvaluacion);
}