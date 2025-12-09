package com.coopac.sistemasoa.repository;

import com.coopac.sistemasoa.model.Aportes;
import com.coopac.sistemasoa.model.Socio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AportesRepository extends JpaRepository<Aportes, Integer> {
    boolean existsBySocioAndEstadoTrue(Socio socio);
}