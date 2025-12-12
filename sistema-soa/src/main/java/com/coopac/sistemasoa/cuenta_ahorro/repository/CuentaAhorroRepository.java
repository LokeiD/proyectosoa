package com.coopac.sistemasoa.cuenta_ahorro.repository;

import com.coopac.sistemasoa.cuenta_ahorro.model.CuentaAhorro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuentaAhorroRepository extends JpaRepository<CuentaAhorro, Integer> {
    // Aquí puedes agregar búsquedas personalizadas si necesitas,
    // por ejemplo buscar por DNI del socio:
    // List<CuentaAhorro> findBySocioDni(String dni);
}