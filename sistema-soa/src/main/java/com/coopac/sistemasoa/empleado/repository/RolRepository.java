package com.coopac.sistemasoa.empleado.repository;

import com.coopac.sistemasoa.empleado.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
}