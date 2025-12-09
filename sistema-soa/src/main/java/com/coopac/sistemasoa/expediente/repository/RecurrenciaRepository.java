package com.coopac.sistemasoa.expediente.repository;

import com.coopac.sistemasoa.expediente.model.Recurrencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecurrenciaRepository extends JpaRepository<Recurrencia, Integer> {
}