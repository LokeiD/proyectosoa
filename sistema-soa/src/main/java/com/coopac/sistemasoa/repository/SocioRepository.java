package com.coopac.sistemasoa.repository;

import com.coopac.sistemasoa.model.Socio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SocioRepository extends JpaRepository<Socio, Integer> {
    Optional<Socio> findByDni(String dni);
}