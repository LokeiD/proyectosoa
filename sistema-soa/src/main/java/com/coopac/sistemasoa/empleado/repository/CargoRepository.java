package com.coopac.sistemasoa.empleado.repository;

import com.coopac.sistemasoa.empleado.model.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CargoRepository extends JpaRepository<Cargo, Integer> {
}