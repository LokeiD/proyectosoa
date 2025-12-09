package com.coopac.sistemasoa.repository;

import com.coopac.sistemasoa.model.Credito;
import com.coopac.sistemasoa.model.Cuotas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CuotasRepository extends JpaRepository<Cuotas, Integer> {
    List<Cuotas> findByCreditoOrderByFechaVencimientoAsc(Credito credito);
}