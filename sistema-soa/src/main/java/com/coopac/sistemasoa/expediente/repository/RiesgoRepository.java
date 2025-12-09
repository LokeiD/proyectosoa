package com.coopac.sistemasoa.expediente.repository;

import com.coopac.sistemasoa.expediente.model.Riesgo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RiesgoRepository extends JpaRepository<Riesgo, Integer> {
}