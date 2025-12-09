package com.coopac.sistemasoa.repository;

import com.coopac.sistemasoa.model.Riesgo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RiesgoRepository extends JpaRepository<Riesgo, Integer> {
}