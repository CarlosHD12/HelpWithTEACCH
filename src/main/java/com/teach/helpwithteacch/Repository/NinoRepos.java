package com.teach.helpwithteacch.Repository;

import com.teach.helpwithteacch.Entidades.Nino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface NinoRepos extends JpaRepository<Nino, Long>, JpaSpecificationExecutor<Nino> {
}