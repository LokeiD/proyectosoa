package com.coopac.sistemasoa.expediente.repository;

import com.coopac.sistemasoa.expediente.model.Evaluacion;
import com.coopac.sistemasoa.expediente.model.ExpedienteCredito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluacionRepository extends JpaRepository<Evaluacion, Integer> {
    List<Evaluacion> findByExpediente(ExpedienteCredito expediente);
}