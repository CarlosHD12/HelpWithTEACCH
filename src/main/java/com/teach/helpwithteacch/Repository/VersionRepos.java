package com.teach.helpwithteacch.Repository;

import com.teach.helpwithteacch.Entidades.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VersionRepos extends JpaRepository<Version, Long>, JpaSpecificationExecutor<Version> {
    Optional<Version> findByPrueba_IdPruebaAndNumeroVersion(Long idPrueba, String version);
    boolean existsByPrueba_IdPruebaAndNumeroVersion(Long idPrueba, String version);
    boolean existsByPrueba_IdPruebaAndNumeroVersionAndIdVersionNot(Long idPrueba, String version, Long idVersion);
}