package com.coopac.sistemasoa.credito.repository;

import com.coopac.sistemasoa.credito.model.EstadoCuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoCuotaRepository extends JpaRepository<EstadoCuota, Integer> {
}