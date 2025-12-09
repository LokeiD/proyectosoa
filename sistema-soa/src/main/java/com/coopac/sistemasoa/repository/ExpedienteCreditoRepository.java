package com.coopac.sistemasoa.repository;

import com.coopac.sistemasoa.model.ExpedienteCredito;
import com.coopac.sistemasoa.model.Socio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpedienteCreditoRepository extends JpaRepository<ExpedienteCredito, Integer> {
    List<ExpedienteCredito> findBySocio(Socio socio);
}