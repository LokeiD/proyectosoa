package com.coopac.sistemasoa.repository;

import com.coopac.sistemasoa.model.TipoDesembolso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoDesembolsoRepository extends JpaRepository<TipoDesembolso, Integer> {
}