package com.teach.helpwithteacch.Repository;

import com.teach.helpwithteacch.Entidades.Prueba;
import com.teach.helpwithteacch.Enum.TipoPrueba;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PruebaRepos extends JpaRepository<Prueba, Long>, JpaSpecificationExecutor<Prueba> {
    Optional<Prueba> findByTipo(TipoPrueba tipo);
    boolean existsByTipo(TipoPrueba tipo);
    boolean existsByTipoAndIdPruebaNot(TipoPrueba tipo, Long idPrueba);
}