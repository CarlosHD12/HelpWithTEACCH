package com.teach.helpwithteacch.Security.Repository;

import com.teach.helpwithteacch.Enum.RolNombre;
import com.teach.helpwithteacch.Security.Entidades.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepos extends JpaRepository<Rol, Long>, JpaSpecificationExecutor<Rol> {
    Optional<Rol> findByNombre(RolNombre nombre);
    boolean existsByNombre(RolNombre nombre);
}