package com.coopac.sistemasoa.repository;

import com.coopac.sistemasoa.model.TipoDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoDecisionRepository extends JpaRepository<TipoDecision, Integer> {
}