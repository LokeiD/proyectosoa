package com.coopac.sistemasoa.empleado.repository;

import com.coopac.sistemasoa.empleado.model.Trabajador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrabajadorRepository extends JpaRepository<Trabajador, Integer> {
}