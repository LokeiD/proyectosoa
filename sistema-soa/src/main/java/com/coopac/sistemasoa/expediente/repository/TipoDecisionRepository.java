package com.coopac.sistemasoa.expediente.repository;

import com.coopac.sistemasoa.expediente.model.TipoDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoDecisionRepository extends JpaRepository<TipoDecision, Integer> {
}