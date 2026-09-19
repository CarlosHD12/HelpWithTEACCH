package com.teach.helpwithteacch.Repository;

import com.teach.helpwithteacch.Entidades.ResultadoSubtest;
import com.teach.helpwithteacch.Enum.TipoSubtest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResultadoSubtestRepos extends JpaRepository<ResultadoSubtest, Long>, JpaSpecificationExecutor<ResultadoSubtest> {
    Optional<ResultadoSubtest> findByResultado_IdResultadoAndTipo(Long idResultado, TipoSubtest tipo);
    boolean existsByResultado_IdResultadoAndTipo(Long idResultado, TipoSubtest tipo);
    List<ResultadoSubtest> findByResultado_IdResultadoOrderByTipoAsc(Long idResultado);
}