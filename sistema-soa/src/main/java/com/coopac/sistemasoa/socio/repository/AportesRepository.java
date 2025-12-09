package com.coopac.sistemasoa.socio.repository;

import com.coopac.sistemasoa.socio.model.Aportes;
import com.coopac.sistemasoa.socio.model.Socio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AportesRepository extends JpaRepository<Aportes, Integer> {
    boolean existsBySocioAndEstadoTrue(Socio socio);
}