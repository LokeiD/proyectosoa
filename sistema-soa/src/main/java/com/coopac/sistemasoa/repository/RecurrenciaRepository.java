package com.coopac.sistemasoa.repository;

import com.coopac.sistemasoa.model.Recurrencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecurrenciaRepository extends JpaRepository<Recurrencia, Integer> {
}