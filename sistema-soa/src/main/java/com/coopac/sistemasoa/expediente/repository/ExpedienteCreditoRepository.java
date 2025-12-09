package com.coopac.sistemasoa.expediente.repository;

import com.coopac.sistemasoa.expediente.model.ExpedienteCredito;
import com.coopac.sistemasoa.socio.model.Socio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpedienteCreditoRepository extends JpaRepository<ExpedienteCredito, Integer> {
    List<ExpedienteCredito> findBySocio(Socio socio);
}